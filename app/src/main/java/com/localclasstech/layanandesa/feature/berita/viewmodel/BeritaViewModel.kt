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
import kotlinx.coroutines.withTimeout

class BeritaViewModel(private val repository: BeritaRepository) : ViewModel() {
    val beritaUiList: LiveData<List<DataClassBerita>> get() = _beritaUiList
    private val _beritaUiList = MutableLiveData<List<DataClassBerita>>()

    val beritaTerkiniUiList: LiveData<List<DataClassBerita>> get() = _beritaTerkiniUiList
    private val _beritaTerkiniUiList = MutableLiveData<List<DataClassBerita>>()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    private var isFirstLoad = true

    fun fetchBerita(isRefresh: Boolean = false) {
        viewModelScope.launch {
            // Hanya tampilkan loading untuk pertama kali load
            if (isFirstLoad) {
                _isLoading.value = true
                isFirstLoad = false
            }

            _error.value = null

            try {
                repository.getAllBerita { result ->
                    _isLoading.value = false
                    if (result != null && result.isNotEmpty()) {
                        _beritaUiList.value = result.map { it.toUiModel() }
                        Log.d("BeritaViewModel", "Berita fetched: ${result.size}")
                    } else {
                        _error.value = "Tidak ada data berita yang tersedia."
                        Log.e("BeritaViewModel", "Gagal memuat data berita. Error: null result")
                    }
                }
            } catch (e: Exception) {
                _isLoading.value = false
                _error.value = "Terjadi kesalahan: ${e.localizedMessage}"
                Log.e("BeritaViewModel", "Error fetching berita", e)
            }
        }
    }

    fun fetchBeritaTerkini(isRefresh: Boolean = false) {
        viewModelScope.launch {
            // Hanya tampilkan loading untuk pertama kali load
            if (isFirstLoad) {
                _isLoading.value = true
                isFirstLoad = false
            }

            _error.value = null

            try {
                repository.getAllBerita { result ->
                    _isLoading.value = false
                    if (result != null && result.isNotEmpty()) {
                        // Sortir berita berdasarkan createdAt (terbaru dulu)
                        val sortedBerita = result.sortedByDescending { it.createdAt }

                        // Ambil hanya 3 berita terbaru
                        val recentBerita = sortedBerita.take(3)

                        // Convert ke UI model dan update LiveData
                        _beritaTerkiniUiList.value = recentBerita.map { it.toUiModel() }

                        Log.d("BeritaViewModel", "Berita terkini fetched: ${recentBerita.size}")
                    } else {
                        _error.value = "Tidak ada berita terkini yang tersedia."
                        Log.e("BeritaViewModel", "Gagal memuat data berita terkini. Error: null result")
                    }
                }
            } catch (e: Exception) {
                _isLoading.value = false
                _error.value = "Terjadi kesalahan: ${e.localizedMessage}"
                Log.e("BeritaViewModel", "Error fetching berita terkini", e)
            }
        }
    }

    fun refreshData() {
        fetchBeritaTerkini(true)
        fetchBerita(true)
    }
}