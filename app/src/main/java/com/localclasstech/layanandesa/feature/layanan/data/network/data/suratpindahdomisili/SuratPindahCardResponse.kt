package com.localclasstech.layanandesa.feature.layanan.data.network.data.suratpindahdomisili

import com.localclasstech.layanandesa.feature.layanan.data.DataClassCardSurat

fun SuratPindahResponse.toCardSuratPindah(): DataClassCardSurat {
    return DataClassCardSurat(
        id = this.id,
        jenisSurat = "Surat Pindah Domisili", // Anda bisa sesuaikan ini jika perlu
        namaPengirim = this.nama,
        tanggalPembuatan = this.createdAt,
        statusSurat = this.status
    )
}