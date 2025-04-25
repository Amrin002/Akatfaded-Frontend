package com.localclasstech.layanandesa.feature.layanan.data.network.data.suratdomisili

import com.localclasstech.layanandesa.feature.layanan.data.DataClassCardSurat

fun SuratDomisiliResponse.toCardSuratDomisili(): DataClassCardSurat {
    return DataClassCardSurat(
        id = this.id,
        jenisSurat = this.typeSurat,
        namaPengirim = this.nama,
        tanggalPembuatan = this.createdAt,
        statusSurat = this.status
    )
}