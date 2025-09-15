package com.localclasstech.layanandesa.feature.umkm.data

import com.localclasstech.layanandesa.network.Penduduk
import com.localclasstech.layanandesa.network.User

data class UmkmResponse(
    val success: Boolean,
    val message: String,
    val data: List<Umkm>? = null
)

data class SingleUmkmResponse(
    val success: Boolean,
    val message: String,
    val data: Umkm? = null
)

data class Umkm(
    val id: Int,
    val user_id: Int,
    val nik: String,
    val nama_usaha: String,
    val kategori: String,
    val nama_produk: String,
    val deskripsi_produk: String,
    val harga_produk: String? = null, // UBAH: String bukan Double
    val foto_produk: String? = null,
    val nomor_telepon: String,
    val link_facebook: String? = null,
    val link_instagram: String? = null,
    val link_tiktok: String? = null,
    val status: String,
    val catatan_admin: String? = null,
    val approved_at: String? = null,
    val approved_by: Int? = null, // UBAH: Int bukan User object
    val created_at: String,
    val updated_at: String,
    val penduduk: Penduduk? = null
) {
    // Helper functions untuk accessor yang hilang
    val harga_double: Double?
        get() = harga_produk?.toDoubleOrNull()

    val kategori_label: String
        get() = when(kategori) {
            "makanan" -> "Makanan & Minuman"
            "jasa" -> "Jasa"
            "kerajinan" -> "Kerajinan"
            "pertanian" -> "Pertanian"
            "perdagangan" -> "Perdagangan"
            else -> "Lainnya"
        }

    val status_label: String
        get() = when(status) {
            "pending" -> "Menunggu Persetujuan"
            "approved" -> "Disetujui"
            "rejected" -> "Ditolak"
            else -> "Tidak Diketahui"
        }
}

data class CreateUmkmRequest(
    val nama_usaha: String,
    val kategori: String,
    val nama_produk: String,
    val deskripsi_produk: String,
    val harga_produk: Double? = null, // TAMBAHAN
    val nomor_telepon: String,
    val link_facebook: String? = null,
    val link_instagram: String? = null,
    val link_tiktok: String? = null
)

