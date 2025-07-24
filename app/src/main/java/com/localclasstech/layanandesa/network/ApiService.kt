package com.localclasstech.layanandesa.network


import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path



interface ApiService {
    @POST("api/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("api/register")
    fun register(@Body request: RegisterRequest): Call<RegisterResponse>


    @POST("api/logout")
    fun logout(): Call<Void>


//    api baru untuk update user
@GET("api/user")
suspend fun getUserProfile(
    @Header("Authorization") token: String
): Response<UserData>

    @PUT("api/update/{id}")
    suspend fun updateUserProfile(
        @Header("Authorization") token: String,
        @Path("id") userId: Int,
        @Body updateData: Map<String, @JvmSuppressWildcards Any>
    ): Response<UserData>

    // API untuk update user profile dengan gambar
    @Multipart
    @POST("api/update/{id}")
    suspend fun updateUserWithImage(
        @Header("Authorization") token: String,
        @Path("id") userId: Int,
        @Part("_method") method: RequestBody,
        @Part("email") email: RequestBody,
        @Part("nik") nik: RequestBody,
        @Part("no_telp") noTelp: RequestBody,
        @Part("password") password: RequestBody?,
        @Part image: MultipartBody.Part?
    ): Response<UpdateUserResponse>
    // API untuk mengirim OTP ke email
    @POST("api/send-otp")
    fun sendOtp(@Body request: SendOtpRequest): Call<ResetPasswordOtpResponse>

    @POST("api/reset-password/otp")
    fun resetPasswordWithOtp(@Body request: ResetPasswordOtpRequest): Call<ResetPasswordOtpResponse>
}