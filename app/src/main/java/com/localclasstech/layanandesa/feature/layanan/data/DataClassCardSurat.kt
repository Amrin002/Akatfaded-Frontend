package com.localclasstech.layanandesa.feature.layanan.data

import android.os.Parcelable
import androidx.versionedparcelable.ParcelField
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DataClassCardSurat(
    val id: Int,
    val jenisSurat: String,
    val namaPengirim: String,
    val tanggalPembuatan: String,
    val statusSurat: String
): Parcelable
