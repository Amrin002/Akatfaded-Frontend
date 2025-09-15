package com.localclasstech.layanandesa.feature.umkm.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localclasstech.layanandesa.feature.umkm.data.Umkm
import com.localclasstech.layanandesa.feature.umkm.data.UmkmOptions
import com.localclasstech.layanandesa.feature.umkm.data.repository.UmkmRepository
import kotlinx.coroutines.launch
import retrofit2.HttpException

class UmkmViewModel(private val repository: UmkmRepository) : ViewModel() {

    // LiveData untuk Produk Terbaru (Grid Layout)
    private val _produkTerbaru = MutableLiveData<List<Umkm>>()
    val produkTerbaru: LiveData<List<Umkm>> = _produkTerbaru

    // LiveData untuk Semua Produk (Horizontal Layout)
    private val _semuaProduk = MutableLiveData<List<Umkm>>()
    val semuaProduk: LiveData<List<Umkm>> = _semuaProduk

    // LiveData untuk Options (Kategori, Status)
    private val _umkmOptions = MutableLiveData<UmkmOptions>()
    val umkmOptions: LiveData<UmkmOptions> = _umkmOptions

    // Loading states
    private val _isLoadingProdukTerbaru = MutableLiveData<Boolean>()
    val isLoadingProdukTerbaru: LiveData<Boolean> = _isLoadingProdukTerbaru

    private val _isLoadingSemuaProduk = MutableLiveData<Boolean>()
    val isLoadingSemuaProduk: LiveData<Boolean> = _isLoadingSemuaProduk

    // Error states
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    // Current filter
    private val _currentKategori = MutableLiveData<String?>()
    val currentKategori: LiveData<String?> = _currentKategori

    init {
        loadUmkmOptions()
        loadProdukTerbaru()
        loadSemuaProduk()
    }

    /**
     * Load UMKM Options (Kategori dan Status)
     */
    fun loadUmkmOptions() {
        viewModelScope.launch {
            try {
                val response = repository.getUmkmOptions()
                if (response.isSuccessful) {
                    response.body()?.let { optionsResponse ->
                        if (optionsResponse.success) {
                            _umkmOptions.postValue(optionsResponse.data)
                        }
                    }
                }
            } catch (e: Exception) {
                // Silent fail untuk options, tidak critical
            }
        }
    }

    /**
     * Load Produk Terbaru untuk Grid Layout
     */
    fun loadProdukTerbaru() {
        viewModelScope.launch {
            _isLoadingProdukTerbaru.postValue(true)
            try {
                val response = repository.getPublicUmkm(
                    kategori = null,
                    search = null,
                    limit = 6, // Tampilkan 6 produk terbaru (3 rows x 2 cols)
                    page = 1
                )

                if (response.isSuccessful) {
                    response.body()?.let { umkmResponse ->
                        if (umkmResponse.success) {
                            _produkTerbaru.postValue(umkmResponse.data ?: emptyList())
                        } else {
                            _errorMessage.postValue(umkmResponse.message)
                        }
                    }
                } else {
                    _errorMessage.postValue("Gagal memuat produk terbaru")
                }
            } catch (e: HttpException) {
                _errorMessage.postValue("Error: ${e.message()}")
            } catch (e: Exception) {
                _errorMessage.postValue("Tidak dapat memuat data. Periksa koneksi internet.")
            } finally {
                _isLoadingProdukTerbaru.postValue(false)
            }
        }
    }

    /**
     * Load Semua Produk untuk Horizontal Layout
     */
    fun loadSemuaProduk(kategori: String? = null) {
        viewModelScope.launch {
            _isLoadingSemuaProduk.postValue(true)
            try {
                val response = repository.getPublicUmkm(
                    kategori = kategori,
                    search = null,
                    limit = 10, // Tampilkan 10 produk untuk horizontal scroll
                    page = 1
                )

                if (response.isSuccessful) {
                    response.body()?.let { umkmResponse ->
                        if (umkmResponse.success) {
                            _semuaProduk.postValue(umkmResponse.data ?: emptyList())
                            _currentKategori.postValue(kategori)
                        } else {
                            _errorMessage.postValue(umkmResponse.message)
                        }
                    }
                } else {
                    _errorMessage.postValue("Gagal memuat semua produk")
                }
            } catch (e: HttpException) {
                _errorMessage.postValue("Error: ${e.message()}")
            } catch (e: Exception) {
                _errorMessage.postValue("Tidak dapat memuat data. Periksa koneksi internet.")
            } finally {
                _isLoadingSemuaProduk.postValue(false)
            }
        }
    }

    /**
     * Filter produk berdasarkan kategori
     */
    fun filterByKategori(kategori: String?) {
        loadSemuaProduk(kategori)
    }

    /**
     * Refresh semua data
     */
    fun refreshData() {
        loadProdukTerbaru()
        loadSemuaProduk(_currentKategori.value)
    }

    /**
     * Clear error message
     */
    fun clearErrorMessage() {
        _errorMessage.postValue("")
    }

    /**
     * Get detail UMKM by ID
     */
    fun getUmkmDetail(id: Int, onResult: (Umkm?) -> Unit) {
        viewModelScope.launch {
            try {
                val response = repository.getPublicUmkmById(id)
                if (response.isSuccessful) {
                    response.body()?.let { umkmResponse ->
                        if (umkmResponse.success) {
                            onResult(umkmResponse.data)
                        } else {
                            onResult(null)
                            _errorMessage.postValue(umkmResponse.message)
                        }
                    }
                } else {
                    onResult(null)
                    _errorMessage.postValue("Gagal memuat detail UMKM")
                }
            } catch (e: Exception) {
                onResult(null)
                _errorMessage.postValue("Error: ${e.message}")
            }
        }
    }

    /**
     * Utility function untuk format harga
     */
    fun formatPrice(price: Double?): String {
        return if (price != null && price > 0) {
            "Rp ${String.format("%,.0f", price).replace(',', '.')}"
        } else {
            "Harga: Hubungi"
        }
    }

    /**
     * Utility function untuk get kategori label
     */
    fun getKategoriLabel(kategoriKey: String): String {
        return _umkmOptions.value?.kategori_options?.get(kategoriKey) ?: kategoriKey.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase() else it.toString()
        }
    }
}