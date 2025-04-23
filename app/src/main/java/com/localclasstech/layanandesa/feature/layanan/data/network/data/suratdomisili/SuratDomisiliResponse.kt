package com.localclasstech.layanandesa.feature.layanan.data.network.data.suratdomisili

import com.google.gson.annotations.SerializedName

data class SuratDomisiliResponse(
    val id: Int,
    val no_surat: String?,
    @SerializedName("type_surat")
    val typeSurat: String,
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
    val keterangan: String,
    val status: String,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("deleted_at")
    val deletedAt: String?

)