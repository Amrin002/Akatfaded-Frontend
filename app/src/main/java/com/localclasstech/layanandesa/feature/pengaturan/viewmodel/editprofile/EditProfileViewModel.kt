package com.localclasstech.layanandesa.feature.pengaturan.viewmodel.editprofile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.localclasstech.layanandesa.network.ApiService
import com.localclasstech.layanandesa.network.UpdateUserResponse
import com.localclasstech.layanandesa.network.User
import com.localclasstech.layanandesa.network.UserResponse
import com.localclasstech.layanandesa.settings.PreferencesHelper
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class EditProfileViewModel(
    private val apiService: ApiService,
    private val preferencesHelper: PreferencesHelper
) : ViewModel() {

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> get() = _user

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    fun loadUserData() {
        _isLoading.value = true
        val token = "Bearer ${preferencesHelper.getToken()}"

        apiService.getUser(token).enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    response.body()?.let { userResponse ->
                        _user.value = userResponse.toUser()
                        Log.d("EditProfileViewModel", "Data user diterima: $userResponse")
                    }
                } else {
                    _errorMessage.value = "Gagal memuat data: ${response.errorBody()?.string()}"
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.value = "Error jaringan: ${t.message}"
            }
        })
    }

    fun updateUser(
        userId: Int,
        name: String,
        email: String,
        nik: String,
        password: String?,
        noTelp: String,
        imageFile: File?
    ) {
        _isLoading.value = true

        // Convert semua field ke RequestBody
        val nameBody = name.toRequestBody("text/plain".toMediaTypeOrNull())
        val emailBody = email.toRequestBody("text/plain".toMediaTypeOrNull())
        val nikBody = nik.toRequestBody("text/plain".toMediaTypeOrNull())
        val noTelpBody = noTelp.toRequestBody("text/plain".toMediaTypeOrNull())
        val passwordBody = password?.toRequestBody("text/plain".toMediaTypeOrNull())

        // Handle file upload
        val imagePart = imageFile?.let { file ->
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("image", file.name, requestFile)
        }

        val authHeader = "Bearer ${preferencesHelper.getToken()}"

        apiService.updateUser(
            userId,
            nameBody,
            emailBody,
            nikBody,
            passwordBody,
            noTelpBody,
            imagePart,
            authHeader
        ).enqueue(object : Callback<UpdateUserResponse> {
            override fun onResponse(call: Call<UpdateUserResponse>, response: Response<UpdateUserResponse>) {
                _isLoading.value = false

                if (response.isSuccessful) {
                    response.body()?.let { updateResponse ->
                        val userData = updateResponse.user
                        val updatedUser = User(
                            id = userData.id,
                            nik = userData.nik,
                            name = userData.name,
                            no_telp = userData.noTelp,
                            email = userData.email,
                            password = null,
                            image = userData.profileImage ?: "",
                            email_verified_at = null,
                            role = "user",
                            created_at = userData.createdAt,
                            updated_at = userData.updatedAt
                        )

                        _user.value = updatedUser
                        preferencesHelper.saveUser(updatedUser)
                        Log.d("EditProfileViewModel", "Update berhasil: $updatedUser")
                    }
                } else {
                    _errorMessage.value = "Gagal update: ${response.errorBody()?.string()}"
                }
            }

            // Perbaikan tipe parameter di sini
            override fun onFailure(call: Call<UpdateUserResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.value = "Error jaringan: ${t.message}"
            }
        })
    }

    // Extension function untuk konversi UserResponse ke User
    private fun UserResponse.toUser() = User(
        id = id,
        nik = nik ?: "",
        name = name ?: "",
        no_telp = noTelp ?: "",
        email = email ?: "",
        password = null,
        // Di ViewModel
        image = if (profileImage.isNullOrEmpty()) "" else "http://192.168.56.1:8000/$profileImage",
        email_verified_at = null,
        role = "",
        created_at = "",
        updated_at = ""
    )
}