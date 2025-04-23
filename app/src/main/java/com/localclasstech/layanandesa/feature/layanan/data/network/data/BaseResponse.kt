package com.localclasstech.layanandesa.feature.layanan.data.network.data


data class BaseResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T
)