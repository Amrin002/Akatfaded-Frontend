package com.localclasstech.layanandesa.feature.berita.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localclasstech.layanandesa.feature.berita.data.DataClassBerita
import com.localclasstech.layanandesa.feature.berita.data.repository.BeritaRepository
import com.localclasstech.layanandesa.feature.berita.data.toUiModel
import kotlinx.coroutines.launch

class DetailBeritaViewModel(private val repository: BeritaRepository) : ViewModel() {
    private val _beritaDetail = MutableLiveData<DataClassBerita?>()
    val beritaDetail: LiveData<DataClassBerita?> get() = _beritaDetail

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    fun fetchDetailBerita(beritaId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                repository.getDetailBerita(beritaId) { result ->
                    _isLoading.value = false
                    if (result != null) {
                        _beritaDetail.value = result.toUiModel()
                        Log.d("DetailBeritaViewModel", "Berita detail fetched: ${result.judul}")
                    } else {
                        _error.value = "Tidak dapat memuat detail berita."
                        Log.e("DetailBeritaViewModel", "Gagal memuat detail berita. Error: null result")
                    }
                }
            } catch (e: Exception) {
                _isLoading.value = false
                _error.value = "Terjadi kesalahan: ${e.localizedMessage}"
                Log.e("DetailBeritaViewModel", "Error fetching berita detail", e)
            }
        }
    }
}