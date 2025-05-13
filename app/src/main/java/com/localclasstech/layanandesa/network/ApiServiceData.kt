package com.localclasstech.layanandesa.network

import com.google.gson.annotations.SerializedName

data class LoginRequest(val nik: String, val password: String)
data class LoginResponse(
    val message: String,
    val access_token: String,
    val token_type: String,
    val user: User
)
data class ErrorResponse(
    val message: String,
    val errors: Map<String, List<String>>?
)


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
data class RegisterRequest(
    val name: String,
    val email: String,
    val nik: String,
    val no_telp: String,
    val password: String,
)
data class RegisterResponse(
    @SerializedName("message") val message: String,
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("token_type") val tokenType: String,
    @SerializedName("user") val user: User
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
    @SerializedName("penduduk_id") val pendudukId: String?,
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("email_verified_at") val emailVerifiedAt: String?,
    @SerializedName("role") val role: String,
    @SerializedName("image") val image: String?,
    @SerializedName("no_telp") val noTelp: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String
)
data class SendOtpRequest(
    val email: String
)

data class ResetPasswordOtpRequest(
    val email: String,
    val otp: String,
    val password: String,
    val password_confirmation: String
)
data class ResetPasswordOtpResponse(
    @SerializedName("message") val message: String
)
data class BaseResponse<T>(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("nik") val nik: String? = null,
    @SerializedName("penduduk_id") val pendudukId: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("email_verified_at") val emailVerifiedAt: String? = null,
    @SerializedName("role") val role: String? = null,
    @SerializedName("image") val image: String? = null,
    @SerializedName("no_telp") val noTelp: String? = null,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val data: T? = null
)

