package com.localclasstech.layanandesa.feature.keluhan.data

import com.google.gson.annotations.SerializedName
import com.localclasstech.layanandesa.network.User
import kotlinx.parcelize.Parcelize


data class Keluhan(
    val id: Int,
    val judul: String,
    val isi: String,
    val status: String,
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
    val isi: String
)