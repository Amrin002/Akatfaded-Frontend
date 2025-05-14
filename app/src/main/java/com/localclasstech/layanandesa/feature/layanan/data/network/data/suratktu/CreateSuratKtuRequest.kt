package com.localclasstech.layanandesa.feature.layanan.data.network.data.suratktu

import com.google.gson.annotations.SerializedName

data class CreateSuratKtuRequest(
    val nama: String,
    @SerializedName("tempat_lahir")
    val tempatLahir: String,
    @SerializedName("tanggal_lahir")
    val tanggalLahir: String,
    @SerializedName("jenis_kelamin")
    val jenisKelamin: String,
    val kewarganegaraan: String,
    val agama: String,
    val pekerjaan: String,
    val alamat: String,
    @SerializedName("nama_usaha")
    val namaUsaha: String,
    @SerializedName("jenis_usaha")
    val jenisUsaha: String,
    @SerializedName("alamat_usaha")
    val alamatUsaha: String,
    @SerializedName("pemilik_usaha")
    val pemilikUsaha: String,
    val keterangan: String?
)
