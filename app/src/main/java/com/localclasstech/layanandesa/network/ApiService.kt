package com.localclasstech.layanandesa.network


import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
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
    @GET("api/user")
    fun getUser(@Header("Authorization") authHeader: String): Call<UserResponse>

    @POST("api/register")
    fun register(@Body request: RegisterRequest): Call<RegisterResponse>


    @POST("api/logout")
    fun logout(): Call<Void>

    // API untuk memperbarui data user
    @Multipart
    @PUT("api/update/{id}")
    fun updateUser(
        @Path("id") userId: Int,
        @Part("name") name: RequestBody,
        @Part("email") email: RequestBody,
        @Part("nik") nik: RequestBody,
        @Part("password") password: RequestBody?,
        @Part("no_telp") noTelp: RequestBody,
        @Part image: MultipartBody.Part?,
        @Header("Authorization") authHeader: String
    ): Call<UpdateUserResponse>

    // API untuk mengirim OTP ke email
    @POST("api/send-otp")
    fun sendOtp(@Body request: SendOtpRequest): Call<ResetPasswordOtpResponse>

    @POST("api/reset-password/otp")
    fun resetPasswordWithOtp(@Body request: ResetPasswordOtpRequest): Call<ResetPasswordOtpResponse>
}