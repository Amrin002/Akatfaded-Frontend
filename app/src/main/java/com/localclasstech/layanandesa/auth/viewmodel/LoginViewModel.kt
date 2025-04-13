package com.localclasstech.layanandesa.auth.viewmodel

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.localclasstech.layanandesa.network.ApiService
import com.localclasstech.layanandesa.network.LoginRequest
import com.localclasstech.layanandesa.network.LoginResponse
import com.localclasstech.layanandesa.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(private val context: Context) : ViewModel() {
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
        _image.value = savedProfileImage // Expected non-nullable value
    }

    fun login(nik: String, password: String) {
        if (nik.isEmpty() || password.isEmpty()) {
            _errorMessage.value = "NIK atau Password tidak boleh kosong"
            return
        }

        val request = LoginRequest(nik, password)
        RetrofitClient.clientService.login(request).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful && response.body()?.access_token != null) {
                    val user = response.body()?.user
                    val token = response.body()?.access_token

                    if (user != null && token != null) {
                        saveUserLogin(user.nik, user.name,user.image.orEmpty(), token) // Simpan data login

                        // Kirim event LiveData untuk ditampilkan di MainActivity
                        _userName.value = user.name
                        _image.value = user.image ?: ""// Simpan URL foto
                        Log.d("LoginViewModel", "Profile image set in LiveData: ${_image.value}")

                        _loginSuccess.value = true
                    }
                } else {
                    _errorMessage.value = response.body()?.message ?: "Login gagal"
                }
            }


            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _errorMessage.value = "Terjadi kesalahan: ${t.message}"
                Log.e("LoginViewModel", "Login error", t)
            }
        })
    }

    fun loginAsGuest() {
        saveUserLogin("guest", "Guest", "", null)
    }

    fun logout() {
        sharedPreferences.edit().clear().apply()
        _loginState.value = false
        _loginMode.value = null
    }

    fun getUsername(): String {
        return sharedPreferences.getString("user_name", "Guest") ?: "Guest"
    }

    private fun saveUserLogin(nik: String, name: String,profileImage : String?, token: String?) {
        with(sharedPreferences.edit()) {
            putString("login_mode", name)
            putString("user_name", name)
            putString("user_profile_image", profileImage?.ifEmpty { "" } ?: "")
            putString("auth_token", token)
            Log.d("LoginViewModel", "Saving profile image: $profileImage")

            apply()
        }
        _loginState.value = true
        _loginMode.value = name
        _image.value = profileImage ?: "" // Pastikan LiveData tidak null
    }
}
