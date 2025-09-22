package com.localclasstech.layanandesa.feature.umkm.utils

object UmkmConstants {

    // Status UMKM
    const val STATUS_PENDING = "pending"
    const val STATUS_APPROVED = "approved"
    const val STATUS_REJECTED = "rejected"

    // Kategori UMKM
    const val KATEGORI_MAKANAN = "makanan"
    const val KATEGORI_JASA = "jasa"
    const val KATEGORI_KERAJINAN = "kerajinan"
    const val KATEGORI_PERTANIAN = "pertanian"
    const val KATEGORI_PERDAGANGAN = "perdagangan"
    const val KATEGORI_LAINNYA = "lainnya"

    // Status Labels
    val STATUS_LABELS = mapOf(
        STATUS_PENDING to "Menunggu Persetujuan",
        STATUS_APPROVED to "Disetujui",
        STATUS_REJECTED to "Ditolak"
    )

    // Kategori Labels
    val KATEGORI_LABELS = mapOf(
        KATEGORI_MAKANAN to "Makanan & Minuman",
        KATEGORI_JASA to "Jasa",
        KATEGORI_KERAJINAN to "Kerajinan",
        KATEGORI_PERTANIAN to "Pertanian",
        KATEGORI_PERDAGANGAN to "Perdagangan",
        KATEGORI_LAINNYA to "Lainnya"
    )

    // Request codes
    const val REQUEST_CODE_PICK_IMAGE = 1001

    // Pagination
    const val DEFAULT_PAGE_SIZE = 12
}