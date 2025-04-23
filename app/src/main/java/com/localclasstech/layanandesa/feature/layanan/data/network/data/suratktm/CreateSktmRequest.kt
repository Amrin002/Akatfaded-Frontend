package com.localclasstech.layanandesa.feature.layanan.data.network.data.suratktm

data class CreateSktmRequest(  val nama: String,
                               val tempat_lahir: String,
                               val tanggal_lahir: String,
                               val jenis_kelamin: String,
                               val status_kawin: String,
                               val kewarganegaraan: String,
                               val alamat: String,
                               val keterangan: String)
