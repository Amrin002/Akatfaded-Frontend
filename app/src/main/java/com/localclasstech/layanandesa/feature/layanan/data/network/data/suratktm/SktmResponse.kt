package com.localclasstech.layanandesa.feature.layanan.data.network.data.suratktm

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SktmResponse(
    val id: Int,
    @SerializedName("user_id")
    val userId: Int,
    val nama: String,
    @SerializedName("type_surat")
    val typeSurat: String,
    @SerializedName("tempat_lahir")
    val tempatLahir: String,
    @SerializedName("tanggal_lahir")
    val tanggalLahir: String,
    @SerializedName("jenis_kelamin")
    val jenisKelamin: String,
    @SerializedName("status_kawin")
    val statusKawin: String,
    val kewarganegaraan: String,
    val alamat: String,
    val keterangan: String,
    val status: String,
    @SerializedName("no_surat")
    val noSurat: String?, // nullable
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("deleted_at")
    val deletedAt: String? // nullable
) :Parcelable
