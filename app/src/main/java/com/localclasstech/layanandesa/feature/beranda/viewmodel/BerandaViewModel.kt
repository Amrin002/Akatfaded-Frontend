package com.localclasstech.layanandesa.feature.beranda.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.localclasstech.layanandesa.auth.viewmodel.LoginViewModel
import com.localclasstech.layanandesa.feature.beranda.data.DataClassBerita
import com.localclasstech.layanandesa.feature.berita.data.repository.BeritaRepository
import com.localclasstech.layanandesa.feature.berita.data.toUiModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BerandaViewModel(
    private val loginViewModel: LoginViewModel,
    private val beritaRepository: BeritaRepository // Tambahkan parameter repository
) : ViewModel() {

    val userName: LiveData<String> = loginViewModel.userName
    val imageUrl: LiveData<String> = loginViewModel.image

    private val _beritaList = MutableLiveData<List<DataClassBerita>>()
    val beritaList: LiveData<List<DataClassBerita>> = _beritaList

    private val _tanggalSekarang = MutableLiveData<String>()
    val tanggalSekarang: LiveData<String> = _tanggalSekarang
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    init {
        updateTanggal()
        fetchBeritaUntukBeranda()
    }

    private fun fetchBeritaUntukBeranda() {
        _isLoading.value = true
        beritaRepository.getAllBerita { result ->
            _isLoading.value = false
            if (result != null && result.isNotEmpty()) {
                // Ambil 5 berita terbaru
                val beritaTerbaru = result
                    .sortedByDescending { it.createdAt }
                    .take(5)
                    .map { berita ->
                        // Konversi dari Berita ke BerandaDataClassBerita
                        DataClassBerita(
                            idBerita = berita.toUiModel().idBerita,
                            imgBerita = berita.toUiModel().imgBerita,
                            judulBerita = berita.toUiModel().judulBerita,
                            penulisBerita = berita.toUiModel().penulisBerita,
                            tanggalBerita = berita.toUiModel().tanggalBerita,
                            kontenBerita = berita.toUiModel().kontenBerita
                        )
                    }
                _beritaList.value = beritaTerbaru
            }
        }
    }

    private fun updateTanggal() {
        val locale = Locale("id", "ID") // Bahasa Indonesia
        val formatter = SimpleDateFormat("EEE, d MMMM yyyy", locale)
        val tanggal = formatter.format(Date())
        _tanggalSekarang.value = tanggal
    }
}