package com.localclasstech.layanandesa.feature.umkm.data

data class UmkmPublicResponse(
    val success: Boolean,
    val message: String,
    val data: List<Umkm>? = null,
    val pagination: Pagination? = null,
    val filters: UmkmFilters? = null,
    val kategori_options: Map<String, String>? = null
)

data class Pagination(
    val current_page: Int,
    val last_page: Int,
    val per_page: Int,
    val total: Int
)

data class UmkmFilters(
    val kategori: String? = null,
    val search: String? = null
)

data class UmkmOptionsResponse(
    val success: Boolean,
    val message: String,
    val data: UmkmOptions? = null
)

data class UmkmOptions(
    val kategori_options: Map<String, String>,
    val status_options: Map<String, String>
)