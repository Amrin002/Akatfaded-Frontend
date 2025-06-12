package com.localclasstech.layanandesa.feature.pengaturan.viewmodel.editprofile

import android.util.Log
import com.google.gson.Gson
import com.localclasstech.layanandesa.network.ApiService
import com.localclasstech.layanandesa.network.BaseResponse
import com.localclasstech.layanandesa.network.UpdateUserResponse
import com.localclasstech.layanandesa.network.UserData
import com.localclasstech.layanandesa.settings.PreferencesHelper
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.io.File

class UpdateProfileRepository(
    private val apiService: ApiService,
    private val preferencesHelper: PreferencesHelper
) {
    private fun getBearerToken(): String {
        val token = preferencesHelper.getToken()
        Log.d("UpdateProfileRepository", "Bearer Token: $token")
        return "Bearer $token"
    }

    suspend fun getUserProfile(): Response<UserData> {
        val response = apiService.getUserProfile(getBearerToken())
        Log.d("UpdateProfileRepository", "Response Code: ${response.code()}")
        Log.d("UpdateProfileRepository", "Response Body: ${response.body()}")
        return response
    }

    suspend fun updateUserProfile(
        userId: Int,

        email: String,
        nik: String,
        noTelp: String,
        password: String? = null,
        imageFile: File? = null
    ): Response<UpdateUserResponse> {

        val emailRequestBody = email.toRequestBody("text/plain".toMediaTypeOrNull())
        val nikRequestBody = nik.toRequestBody("text/plain".toMediaTypeOrNull())
        val noTelpRequestBody = noTelp.toRequestBody("text/plain".toMediaTypeOrNull())
        val passwordRequestBody = password?.toRequestBody("text/plain".toMediaTypeOrNull())
        // Override HTTP method
        val methodPart = "PUT"
            .toRequestBody("text/plain".toMediaTypeOrNull())
        val imagePart: MultipartBody.Part? = imageFile?.let {
            val requestFile = it.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("image", it.name, requestFile)
        }

        return apiService.updateUserWithImage(
            getBearerToken(),
            userId,
            methodPart,
            emailRequestBody,
            nikRequestBody,
            noTelpRequestBody,
            passwordRequestBody,
            imagePart
        )
    }

}