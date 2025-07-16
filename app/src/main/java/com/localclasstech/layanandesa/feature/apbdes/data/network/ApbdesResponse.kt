package com.localclasstech.layanandesa.feature.apbdes.data.network

// Data Classes
data class ApbdesItem(
    val id: Int,
    val pendapatan: String,
    val penyelenggaraan: String,
    val pelaksanaan: String,
    val pembinaan: String,
    val pemberdayaan: String,
    val penanggulangan: String,
    val pejabat: String,
    val tahun: Int,
    val file: String,
    val created_at: String,
    val updated_at: String
)

data class ApbdesResponse(
    val status: Boolean,
    val message: String,
    val data: List<ApbdesItem>
)

data class ApbdesDetailResponse(
    val status: Boolean,
    val message: String,
    val data: ApbdesItem
)