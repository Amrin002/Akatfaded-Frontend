package com.localclasstech.layanandesa.feature.beranda.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.localclasstech.layanandesa.auth.viewmodel.LoginViewModel
import com.localclasstech.layanandesa.feature.beranda.data.DataClassBerita
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BerandaViewModel(private val loginViewModel: LoginViewModel) : ViewModel() {

    val userName: LiveData<String> = loginViewModel.userName
    val imageUrl: LiveData<String> = loginViewModel.image

    private val _beritaList = MutableLiveData<List<DataClassBerita>>()
    val beritaList: LiveData<List<DataClassBerita>> = _beritaList

    private val _tanggalSekarang = MutableLiveData<String>()
    val tanggalSekarang: LiveData<String> = _tanggalSekarang

    init {
        updateTanggal()
    }

    init {
        loadDummyBerita()
    }

    private fun loadDummyBerita() {
        _beritaList.value = listOf(
            DataClassBerita(
                imgBerita = "https://example.com/image1.jpg",
                judulBerita = "Judul Berita Pertama",
                penulisBerita = "Admin Desa",
                tanggalBerita = "12 Maret 2024",
                kontenBerita = "Isi singkat dari berita pertama lorem Lorem Ipsum Dolor Sit Amet, Consetetur Sadipscing Elitr, Sed Diam Nonumy Eirmod Tempor Invidunt Ut L"
            ),
            DataClassBerita(
                imgBerita = "https://example.com/image2.jpg",
                judulBerita = "Judul Berita Kedua",
                penulisBerita = "Kades",
                tanggalBerita = "10 Maret 2024",
                kontenBerita = "Berita kedua sangat menarik untuk dibaca  Lorem Ipsum Dolor Sit Amet, Consetetur Sadipscing Elitr, Sed Diam Nonumy Eirmod Tempor Invidunt Ut L"
            ),
            DataClassBerita(
                imgBerita = "https://example.com/image2.jpg",
                judulBerita = "Judul Berita Kedua",
                penulisBerita = "Kades",
                tanggalBerita = "10 Maret 2024",
                kontenBerita = "Berita kedua sangat menarik untuk dibaca  Lorem Ipsum Dolor Sit Amet, Consetetur Sadipscing Elitr, Sed Diam Nonumy Eirmod Tempor Invidunt Ut L"
            ),
            DataClassBerita(
                imgBerita = "https://example.com/image2.jpg",
                judulBerita = "Judul Berita Kedua",
                penulisBerita = "Kades",
                tanggalBerita = "10 Maret 2024",
                kontenBerita = "Berita kedua sangat menarik untuk dibaca  Lorem Ipsum Dolor Sit Amet, Consetetur Sadipscing Elitr, Sed Diam Nonumy Eirmod Tempor Invidunt Ut L"
            ),
            DataClassBerita(
                imgBerita = "https://example.com/image2.jpg",
                judulBerita = "Judul Berita Kedua",
                penulisBerita = "Kades",
                tanggalBerita = "10 Maret 2024",
                kontenBerita = "Berita kedua sangat menarik untuk dibaca  Lorem Ipsum Dolor Sit Amet, Consetetur Sadipscing Elitr, Sed Diam Nonumy Eirmod Tempor Invidunt Ut L"
            ),
            DataClassBerita(
                imgBerita = "https://example.com/image2.jpg",
                judulBerita = "Judul Berita Kedua",
                penulisBerita = "Kades",
                tanggalBerita = "10 Maret 2024",
                kontenBerita = "Berita kedua sangat menarik untuk dibaca  Lorem Ipsum Dolor Sit Amet, Consetetur Sadipscing Elitr, Sed Diam Nonumy Eirmod Tempor Invidunt Ut L"
            )
            // Tambah berita dummy lainnya
        )

    }
    private fun updateTanggal() {
        val locale = Locale("id", "ID") // Bahasa Indonesia
        val formatter = SimpleDateFormat("EEE, d MMMM yyyy", locale)
        val tanggal = formatter.format(Date())
        _tanggalSekarang.value = tanggal
    }
}
