@file:JvmName("SktmResponseKt")

package com.localclasstech.layanandesa.feature.layanan.data.network.data.suratktm

import com.localclasstech.layanandesa.feature.layanan.data.DataClassCardSurat

fun SktmResponse.toCardSuratKtm(): DataClassCardSurat {
    return DataClassCardSurat(
        id = this.id,
        jenisSurat = this.typeSurat,
        namaPengirim = this.nama,
        tanggalPembuatan = this.createdAt,
        statusSurat = this.status
    )
}

