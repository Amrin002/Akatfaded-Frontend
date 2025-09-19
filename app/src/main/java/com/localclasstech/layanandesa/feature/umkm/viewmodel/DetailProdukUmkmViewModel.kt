package com.localclasstech.layanandesa.feature.umkm.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localclasstech.layanandesa.feature.umkm.data.Umkm
import com.localclasstech.layanandesa.feature.umkm.data.repository.UmkmRepository
import kotlinx.coroutines.launch
import retrofit2.HttpException

class DetailProdukUmkmViewModel(private val repository: UmkmRepository) : ViewModel() {

    // LiveData untuk detail UMKM
    private val _umkmDetail = MutableLiveData<Umkm?>()
    val umkmDetail: LiveData<Umkm?> = _umkmDetail

    // Loading state
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // Error state
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    /**
     * Load detail UMKM by ID
     */
    fun loadUmkmDetail(id: Int) {
        viewModelScope.launch {
            _isLoading.postValue(true)
            try {
                val response = repository.getPublicUmkmById(id)
                if (response.isSuccessful) {
                    response.body()?.let { umkmResponse ->
                        if (umkmResponse.success) {
                            _umkmDetail.postValue(umkmResponse.data)
                        } else {
                            _errorMessage.postValue(umkmResponse.message)
                            _umkmDetail.postValue(null)
                        }
                    }
                } else {
                    _errorMessage.postValue("Gagal memuat detail UMKM")
                    _umkmDetail.postValue(null)
                }
            } catch (e: HttpException) {
                _errorMessage.postValue("Error: ${e.message()}")
                _umkmDetail.postValue(null)
            } catch (e: Exception) {
                _errorMessage.postValue("Tidak dapat memuat data. Periksa koneksi internet.")
                _umkmDetail.postValue(null)
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    /**
     * Refresh detail UMKM
     */
    fun refreshDetail() {
        _umkmDetail.value?.let { currentUmkm ->
            loadUmkmDetail(currentUmkm.id)
        }
    }

    /**
     * Clear error message
     */
    fun clearErrorMessage() {
        _errorMessage.postValue("")
    }

    /**
     * Format harga untuk display
     */
    fun formatPrice(price: String?): String {
        return try {
            val priceDouble = price?.toDoubleOrNull()
            if (priceDouble != null && priceDouble > 0) {
                "Rp ${String.format("%,.0f", priceDouble).replace(',', '.')}"
            } else {
                "Harga: Hubungi"
            }
        } catch (e: Exception) {
            "Harga: Hubungi"
        }
    }

    /**
     * Generate WhatsApp message untuk contact
     */
    fun generateWhatsAppMessage(umkm: Umkm): String {
        return "Halo, saya tertarik dengan produk ${umkm.nama_produk} dari ${umkm.nama_usaha}. Bisa minta informasi lebih lanjut?"
    }

    /**
     * Generate share text
     */
    fun generateShareText(umkm: Umkm): String {
        val harga = formatPrice(umkm.harga_produk)
        return """
            *${umkm.nama_usaha}*
            
            📦 ${umkm.nama_produk}
            💰 $harga
            📞 ${umkm.nomor_telepon}
            
            ${umkm.deskripsi_produk}
            
            #UMKM #${umkm.kategori_label}
        """.trimIndent()
    }

    /**
     * Check apakah social media link tersedia
     */
    fun hasSocialMediaLinks(umkm: Umkm): Boolean {
        return !umkm.link_facebook.isNullOrEmpty() ||
                !umkm.link_instagram.isNullOrEmpty() ||
                !umkm.link_tiktok.isNullOrEmpty()
    }
}