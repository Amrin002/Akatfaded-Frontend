package com.localclasstech.layanandesa.feature.umkm.data

data class CardUmkm(
    val id: Int,
    val namaUsaha: String,
    val namaProduk: String,
    val harga: String?,
    val fotoProduk: String?,
    val status: String,
    val tanggalDaftar: String,
    val kategori: String
) {
    val statusLabel: String
        get() = when(status) {
            "pending" -> "Menunggu Persetujuan"
            "approved" -> "Disetujui"
            "rejected" -> "Ditolak"
            else -> "Tidak Diketahui"
        }

    val canEdit: Boolean
        get() = status in listOf("pending", "rejected")

    val canDelete: Boolean
        get() = status in listOf("pending", "rejected")
}