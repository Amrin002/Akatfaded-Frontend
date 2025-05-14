package com.localclasstech.layanandesa.feature.layanan.data.network.data.suratpindahdomisili

import com.google.gson.annotations.SerializedName

data class CreateSuratPindahRequest(
    val nama: String,
    @SerializedName("tempat_lahir")
    val tempatLahir: String,
    @SerializedName("tanggal_lahir")
    val tanggalLahir: String,
    @SerializedName("jenis_kelamin")
    val jenisKelamin: String,
    @SerializedName("status_kawin")
    val statusKawin: String,
    val kewarganegaraan: String,
    val pekerjaan: String,
    val alamat: String,
    val kecamatan: String,
    val kabupaten: String,
    @SerializedName("desa_pindah")
    val desaPindah: String,
    val rt: String,
    val rw: String,
    val jalan: String,
    @SerializedName("kecamatan_pindah")
    val kecamatanPindah: String,
    @SerializedName("kabupaten_pindah")
    val kabupatenPindah: String,
    val provinsi: String,
    val keterangan: String
)
