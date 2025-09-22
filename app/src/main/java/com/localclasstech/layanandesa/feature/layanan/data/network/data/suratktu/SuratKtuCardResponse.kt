package com.localclasstech.layanandesa.feature.layanan.data.network.data.suratktu

import com.localclasstech.layanandesa.feature.layanan.data.DataClassCardSurat

fun SuratKtuResponse.toCardSuratKtu(): DataClassCardSurat {
    return DataClassCardSurat(
        id = this.id,
        jenisSurat = "Surat Keterangan Tepat Usaha", // Sesuaikan dengan jenis surat
        namaPengirim = this.nama,
        tanggalPembuatan = this.createdAt,
        statusSurat = this.status
    )
}