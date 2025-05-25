package com.localclasstech.layanandesa.feature.keluhan.data

fun Keluhan.toCardListKeluhan(): CardListKeluhan {
    return CardListKeluhan(
        id = this.id,
        judul = this.judul,
        isi = this.isi,
        namaPengirim = this.user.name,
        tanggalPembuatan = this.createdAt,
        statusKeluhan = this.status,
    )
}