package com.localclasstech.layanandesa.feature.layanan.data.network.data.suratdomisili

data class CreateSuratDomisiliRequest(
    val nama: String,
    val tempat_lahir: String,
    val tanggal_lahir: String,
    val jenis_kelamin: String,
    val status_kawin: String,
    val kewarganegaraan: String,
    val pekerjaan: String,
    val alamat: String,
    val keterangan: String
)
