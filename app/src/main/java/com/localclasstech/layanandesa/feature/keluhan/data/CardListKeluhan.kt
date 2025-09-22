package com.localclasstech.layanandesa.feature.keluhan.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CardListKeluhan(
    val id: Int,
    val judul: String,
    val isi: String,
    val namaPengirim: String,
    val tanggalPembuatan: String,
    val statusKeluhan: String,
): Parcelable
