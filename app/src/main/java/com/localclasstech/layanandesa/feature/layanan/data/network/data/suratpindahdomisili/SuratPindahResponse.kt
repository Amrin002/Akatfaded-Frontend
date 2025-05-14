package com.localclasstech.layanandesa.feature.layanan.data.network.data.suratpindahdomisili

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class SuratPindahResponse(
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
    @SerializedName("status_kawin")
    val statusKawin: String,
    val kewarganegaraan: String,
    val pekerjaan: String,
    val alamat: String,
    val kecamatan: String,
    val kabupaten: String,
    val desaPindah: String,
    val rt: String,
    val rw: String,
    val jalan: String,
    @SerializedName("kecamatan_pindah")
    val kecamatanPindah: String,
    @SerializedName("kabupaten_pindah")
    val kabupatenPindah: String,
    val provinsi: String,
    val keterangan: String,
    val status: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("deleted_at")
    val deletedAt: String? // nullable
) : Parcelable
