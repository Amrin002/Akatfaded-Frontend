package com.localclasstech.layanandesa.network

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

data class LoginRequest(val nik: String, val password: String)
data class RegisterRequest(val name: String, val email: String, val password: String, val password_confirmation: String)
data class UserResponse(
    val id: Int,
    val nik: String?,
    val name: String?,
    @SerializedName("no_telp") val noTelp: String?,
    val email: String?,
    val password: String?,
    @SerializedName("profile_image") val profileImage: String?,
    val token: String?
)
data class LoginResponse(
    val message: String,
    val access_token: String,
    val token_type: String,
    val user: User
)


data class User(
    val id: Int,
    val nik: String,
    val name: String,
    val no_telp: String,
    val email: String,
    val password: String?,
    val image: String?, // Menambahkan URL gambar profil
    val email_verified_at: String?,
    val role: String,
    val created_at: String,
    val updated_at: String
)
// UserResponse.kt
data class UpdateUserResponse(
    @SerializedName("message") val message: String,
    @SerializedName("user") val user: UserData
)

data class UserData(
    @SerializedName("id") val id: Int,
    @SerializedName("nik") val nik: String,
    @SerializedName("name") val name: String,
    @SerializedName("no_telp") val noTelp: String,
    @SerializedName("email") val email: String,
    @SerializedName("profile_image") val profileImage: String?,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String
)

interface ApiService {
    @POST("api/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("api/register")
    fun register(@Body request: RegisterRequest): Call<UserResponse>

    @GET("api/user")
    fun getUser(@Header("Authorization") authHeader: String): Call<UserResponse>

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
}