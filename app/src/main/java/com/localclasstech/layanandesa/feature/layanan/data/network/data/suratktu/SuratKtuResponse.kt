package com.localclasstech.layanandesa.feature.layanan.data.network.data.suratktu

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class SuratKtuResponse(
    val id: Int,
    @SerializedName("user_id")
    val userId: Int,
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
    val keterangan: String?,
    val status: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("deleted_at")
    val deletedAt: String? // nullable
) : Parcelable