package com.localclasstech.layanandesa.feature.keluhan.data

import com.bumptech.glide.RequestBuilder
import com.google.gson.annotations.SerializedName
import com.localclasstech.layanandesa.network.User
import kotlinx.parcelize.Parcelize
import okhttp3.MultipartBody
import okhttp3.Request
import okhttp3.RequestBody
import retrofit2.http.Multipart


data class Keluhan(
    val id: Int,
    val judul: String,
    val isi: String,
    val status: String,
    val gambar: String?,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    val user: User
)


data class KeluhanRequest(
    val judul: String,
    val isi: String,
    var gambar: MultipartBody.Part?
)