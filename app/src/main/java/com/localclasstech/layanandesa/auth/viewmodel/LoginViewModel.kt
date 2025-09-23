package com.localclasstech.layanandesa.auth.viewmodel

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.stream.MalformedJsonException
import com.localclasstech.layanandesa.network.ApiService
import com.localclasstech.layanandesa.network.ErrorResponse
import com.localclasstech.layanandesa.network.LoginRequest
import com.localclasstech.layanandesa.network.LoginResponse
import com.localclasstech.layanandesa.network.RetrofitClient
import com.localclasstech.layanandesa.settings.PreferencesHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import org.json.JSONObject
import java.io.IOException

class LoginViewModel(val context: Context) : ViewModel() {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    private val _loginState = MutableLiveData<Boolean>()
    val loginState: LiveData<Boolean> get() = _loginState

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _loginMode = MutableLiveData<String?>()
    val loginMode: LiveData<String?> get() = _loginMode
    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String> get() = _userName
    private val _image = MutableLiveData<String>()
    val image: LiveData<String> get() = _image

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _loginSuccess = MutableLiveData<Boolean>()
    val loginSuccess: LiveData<Boolean> get() = _loginSuccess

    fun setProfileImage(url: String) {
        Log.d("LoginViewModel", "Profile image set: $url")
        _image.value = url
    }
    init {
        val savedLoginMode = sharedPreferences.getString("login_mode", null)
        val savedProfileImage = sharedPreferences.getString("user_profile_image", "") ?: ""

        _loginState.value = savedLoginMode != null
        _loginMode.value = savedLoginMode
        _image.value = savedProfileImage
    }

    fun login(nik: String, password: String) {
        if (nik.isEmpty() || password.isEmpty()) {
            _errorMessage.value = "NIK atau Password tidak boleh kosong"
            return
        }
        _isLoading.value = true

        performApiLogin(nik, password)

    }
    //login hardcoded
    private fun performHardcodedLogin() {
        _isLoading.value = false
        // Akun hardcoded
        val hardcodedNik = "1234567890"
        val hardcodedName = "User Hardcoded"
        val hardcodedImage = "https://example.com/hardcoded_user.jpg" // Ganti dengan URL gambar yang sesuai
        val hardcodedToken = "hardcoded_token" // Ganti dengan token yang sesuai (jika diperlukan)
        //simpan ke shared preferences
        saveUserLogin(hardcodedNik,hardcodedName,hardcodedImage,hardcodedToken)
        _userName.value = hardcodedName
        _image.value = hardcodedImage
        _loginSuccess.value = true
    }
    private fun isHardcodedLogin(nik: String, password: String): Boolean{
        //set akun hardcoded
        val hardcodedNik = "1234567890"
        val hardcodedPassword = "password"
        return nik == hardcodedNik && password == hardcodedPassword
    }

    //login api
    private fun performApiLogin(nik: String, password: String){
        val request = LoginRequest(nik, password)
        RetrofitClient.clientService.login(request).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                _isLoading.value = false

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.access_token != null && body.user != null) {
                        val user = body.user
                        val token = body.access_token

                        saveUserLogin(user.nik, user.name, user.image.orEmpty(), token)
                        _userName.value = user.name
                        _image.value = user.image ?: ""
                        _loginSuccess.value = true
                    } else {
                        _errorMessage.value = "Data tidak lengkap dari server"
                    }

                } else {
                    try {
                        val errorBody = response.errorBody()?.string()
                        Log.d("LoginViewModel", "Error Body: $errorBody")

                        // 💡 parse manual dengan Gson
                        val gson = Gson()
                        val errorResponse = gson.fromJson(errorBody, ErrorResponse::class.java)
                        val message = errorResponse.message ?: "Login gagal"

                        _errorMessage.value = message

                    } catch (e: Exception) {
                        Log.e("LoginViewModel", "Parsing error response failed", e)
                        _errorMessage.value = "Terjadi kesalahan saat parsing error"
                    }
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _isLoading.value = false

                Log.e("LoginViewModel", "Login error: ${t::class.java.simpleName} - ${t.message}", t)

                when (t) {
                    is IOException -> {
                        // Tidak ada internet atau timeout
                        _errorMessage.value = "Tidak dapat terhubung ke server. Periksa koneksi internet Anda."
                    }
                    is MalformedJsonException -> {
                        _errorMessage.value = "Respon server tidak valid. Hubungi admin atau coba lagi nanti."
                    }
                    else -> {
                        _errorMessage.value = "Terjadi kesalahan tak terduga: ${t.localizedMessage}"
                    }
                }
            }


        })
    }


    fun logout() {
        sharedPreferences.edit().clear().apply()
        _loginState.value = false
        _loginMode.value = null
    }

    fun getUsername(): String {
        return sharedPreferences.getString("user_name", "Guest") ?: "Guest"
    }

    fun updateUserProfile(name: String, profileImage: String?) {
        val preferencesHelper = PreferencesHelper(context)
        preferencesHelper.syncUserProfile(name, profileImage)

        // Pastikan update LiveData
        _loginMode.postValue(name)
        _userName.postValue(name)
        _image.postValue(profileImage ?: "")

        Log.d("LoginViewModel", "Profile updated: name=$name, image=$profileImage")
    }
    private fun saveUserLogin(nik: String, name: String,profileImage : String?, token: String?) {
        with(sharedPreferences.edit()) {
            putString("login_mode", name)
            putString("user_name", name)
            putString("user_profile_image", profileImage?.ifEmpty { "" } ?: "")
            putString("auth_token", token)
            val userId = nik.toIntOrNull() ?: nik.hashCode().let { if (it < 0) -it else it }
            putInt("user_id", userId)
            Log.d("LoginViewModel", "Saving profile image: $profileImage")

            apply()
        }
        _loginState.value = true
        _loginMode.value = name
        _image.value = profileImage ?: "" // Pastikan LiveData tidak null
    }
}