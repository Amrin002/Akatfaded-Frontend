package com.localclasstech.layanandesa.feature.umkm.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localclasstech.layanandesa.feature.umkm.data.CreateUmkmRequest
import com.localclasstech.layanandesa.feature.umkm.data.Umkm
import com.localclasstech.layanandesa.feature.umkm.data.UmkmOptionsResponse
import com.localclasstech.layanandesa.feature.umkm.data.repository.UmkmRepository
import kotlinx.coroutines.launch
import java.io.File

class DaftarUmkmViewModel(private val repository: UmkmRepository) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _submitResult = MutableLiveData<Boolean>()
    val submitResult: LiveData<Boolean> get() = _submitResult

    private val _umkmDetail = MutableLiveData<Umkm?>()
    val umkmDetail: LiveData<Umkm?> get() = _umkmDetail

    private val _umkmOptions = MutableLiveData<UmkmOptionsResponse>()
    val umkmOptions: LiveData<UmkmOptionsResponse> get() = _umkmOptions

    // State untuk form
    private val _isEditMode = MutableLiveData<Boolean>(false)
    val isEditMode: LiveData<Boolean> get() = _isEditMode

    private val _currentUmkmId = MutableLiveData<Int?>()
    val currentUmkmId: LiveData<Int?> get() = _currentUmkmId

    fun setEditMode(umkmId: Int) {
        _isEditMode.value = true
        _currentUmkmId.value = umkmId
        fetchUmkmDetail(umkmId)
    }

    private val _hasExistingBusiness = MutableLiveData<Boolean>(false)
    val hasExistingBusiness: LiveData<Boolean> get() = _hasExistingBusiness

    private val _existingBusinessNames = MutableLiveData<List<String>>()
    val existingBusinessNames: LiveData<List<String>> get() = _existingBusinessNames

    fun checkExistingBusiness() {
        viewModelScope.launch {
            try {
                val response = repository.getMyUmkm()
                if (response.isSuccessful) {
                    val umkmList = response.body()?.data ?: emptyList()
                    val businessNames = umkmList.map { it.nama_usaha }.distinct()

                    _hasExistingBusiness.value = businessNames.isNotEmpty()
                    _existingBusinessNames.value = businessNames
                } else {
                    _hasExistingBusiness.value = false
                    _existingBusinessNames.value = emptyList()
                }
            } catch (e: Exception) {
                _hasExistingBusiness.value = false
                _existingBusinessNames.value = emptyList()
            }
        }
    }

    fun setCreateMode() {
        _isEditMode.value = false
        _currentUmkmId.value = null
        _umkmDetail.value = null
    }

    fun fetchUmkmOptions() {
        viewModelScope.launch {
            try {
                val response = repository.getUmkmOptions()
                if (response.isSuccessful) {
                    _umkmOptions.value = response.body()
                }
            } catch (e: Exception) {
                _errorMessage.value = "Gagal memuat opsi kategori: ${e.message}"
            }
        }
    }

    private fun fetchUmkmDetail(id: Int) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.getUmkmById(id)
                if (response.isSuccessful) {
                    _umkmDetail.value = response.body()?.data
                } else {
                    _errorMessage.value = "Gagal memuat detail UMKM"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Terjadi kesalahan: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun submitUmkm(
        namaUsaha: String,
        kategori: String,
        namaProduk: String,
        deskripsiProduk: String,
        hargaProduk: Double?,
        nomorTelepon: String,
        linkFacebook: String?,
        linkInstagram: String?,
        linkTiktok: String?,
        imageFile: File?
    ) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val request = CreateUmkmRequest(
                    nama_usaha = namaUsaha,
                    kategori = kategori,
                    nama_produk = namaProduk,
                    deskripsi_produk = deskripsiProduk,
                    harga_produk = hargaProduk,
                    nomor_telepon = nomorTelepon,
                    link_facebook = linkFacebook,
                    link_instagram = linkInstagram,
                    link_tiktok = linkTiktok
                )

                val response = if (_isEditMode.value == true) {
                    // PERUBAHAN: Sekarang hanya satu method untuk edit
                    // Tidak perlu cek imageFile != null lagi
                    val umkmId = _currentUmkmId.value!!
                    repository.updateUmkmWithImage(umkmId, request, imageFile)
                } else {
                    // Create mode tetap sama
                    if (imageFile != null) {
                        repository.createUmkmWithImage(request, imageFile)
                    } else {
                        repository.createUmkm(request)
                    }
                }

                if (response.isSuccessful) {
                    _submitResult.value = true
                } else {
                    val errorBody = response.errorBody()?.string()
                    _errorMessage.value = "Gagal ${if (_isEditMode.value == true) "memperbarui" else "mendaftarkan"} UMKM: $errorBody"
                    _submitResult.value = false
                }
            } catch (e: Exception) {
                _errorMessage.value = "Terjadi kesalahan: ${e.message}"
                _submitResult.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetSubmitResult() {
        _submitResult.value = false
    }

    fun resetErrorMessage() {
        _errorMessage.value = ""
    }
}