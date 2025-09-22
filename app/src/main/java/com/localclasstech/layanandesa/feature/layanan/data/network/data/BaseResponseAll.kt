package com.localclasstech.layanandesa.feature.layanan.data.network.data

import com.google.gson.annotations.SerializedName


data class BaseResponseAll<T>(
    val success: Boolean,
    val message: String,
    val data: T
)

data class DownloadUrlResponse(
    val success: Boolean,
    @SerializedName("download_url")
    val download_url: String
)