package com.localclasstech.layanandesa.feature.beranda.data

data class DataClassBerita(
    val idBerita: Int,
    val imgBerita: String,
    val judulBerita: String,
    val penulisBerita: String,
    val tanggalBerita: String,
    val kontenBerita: String
)

// Add a mapping function if needed
fun com.localclasstech.layanandesa.feature.berita.data.DataClassBerita.toBerandaModel(): DataClassBerita {
    return DataClassBerita(
        idBerita = this.idBerita,
        imgBerita = this.imgBerita,
        judulBerita = this.judulBerita,
        penulisBerita = this.penulisBerita,
        tanggalBerita = this.tanggalBerita,
        kontenBerita = this.kontenBerita
    )
}