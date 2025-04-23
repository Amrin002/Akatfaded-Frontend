package com.localclasstech.layanandesa.feature.layanan.data

data class DataClassSuratFilter(
    val tipeSurat: String,
    val listSurat: List<DataClassCardSurat>,
    var isExpanded: Boolean = false
) {
    val jumlahSurat: Int
        get() = listSurat.size
}
