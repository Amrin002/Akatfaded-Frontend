package com.localclasstech.layanandesa.feature.umkm.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localclasstech.layanandesa.feature.umkm.data.CardUmkm
import com.localclasstech.layanandesa.feature.umkm.data.Umkm
import com.localclasstech.layanandesa.feature.umkm.data.repository.UmkmRepository
import kotlinx.coroutines.launch

class KelolaUmkmViewModel(private val repository: UmkmRepository) : ViewModel() {

    companion object {
        private const val TAG = "KelolaUmkmViewModel"
    }

    private val _listUmkm = MutableLiveData<List<CardUmkm>>()
    val listUmkm: LiveData<List<CardUmkm>> get() = _listUmkm

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _deleteResult = MutableLiveData<Boolean>()
    val deleteResult: LiveData<Boolean> get() = _deleteResult

    fun fetchMyUmkm() {
        Log.d(TAG, "fetchMyUmkm() called")
        _isLoading.value = true

        viewModelScope.launch {
            try {
                Log.d(TAG, "Calling repository.getMyUmkm()")
                val response = repository.getMyUmkm()

                Log.d(TAG, "Response received - isSuccessful: ${response.isSuccessful}")
                Log.d(TAG, "Response code: ${response.code()}")
                Log.d(TAG, "Response message: ${response.message()}")

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    Log.d(TAG, "Response body: $responseBody")

                    val umkmList = responseBody?.data ?: emptyList()
                    Log.d(TAG, "UMKM list from API: $umkmList")
                    Log.d(TAG, "UMKM list size: ${umkmList.size}")

                    val mappedList = umkmList.map { umkm ->
                        Log.d(TAG, "Mapping UMKM: $umkm")
                        umkm.toCardUmkm()
                    }

                    Log.d(TAG, "Mapped CardUmkm list: $mappedList")
                    Log.d(TAG, "Mapped list size: ${mappedList.size}")

                    _listUmkm.value = mappedList
                    Log.d(TAG, "Data posted to LiveData successfully")
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e(TAG, "API Error - Code: ${response.code()}")
                    Log.e(TAG, "API Error - Message: ${response.message()}")
                    Log.e(TAG, "API Error - Error Body: $errorBody")

                    _errorMessage.value = "Gagal memuat data UMKM: ${response.message()}"
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception in fetchMyUmkm: ${e.javaClass.simpleName}")
                Log.e(TAG, "Exception message: ${e.message}")
                Log.e(TAG, "Exception localizedMessage: ${e.localizedMessage}")
                Log.e(TAG, "Full exception: ", e)

                _errorMessage.value = "Terjadi kesalahan: ${e.message}"
            } finally {
                Log.d(TAG, "Setting loading to false")
                _isLoading.value = false
            }
        }
    }

    fun deleteUmkm(id: Int) {
        Log.d(TAG, "deleteUmkm() called with id: $id")

        viewModelScope.launch {
            try {
                Log.d(TAG, "Calling repository.deleteUmkm($id)")
                val response = repository.deleteUmkm(id)

                Log.d(TAG, "Delete response - isSuccessful: ${response.isSuccessful}")
                Log.d(TAG, "Delete response code: ${response.code()}")

                if (response.isSuccessful) {
                    Log.d(TAG, "Delete successful, setting deleteResult to true")
                    _deleteResult.value = true
                    // Refresh data setelah delete
                    Log.d(TAG, "Refreshing data after delete")
                    fetchMyUmkm()
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e(TAG, "Delete failed - Error Body: $errorBody")

                    _deleteResult.value = false
                    _errorMessage.value = "Gagal menghapus UMKM: ${response.message()}"
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception in deleteUmkm: ${e.javaClass.simpleName}")
                Log.e(TAG, "Exception message: ${e.message}")
                Log.e(TAG, "Full exception: ", e)

                _deleteResult.value = false
                _errorMessage.value = "Terjadi kesalahan: ${e.message}"
            }
        }
    }

    // Extension function untuk convert Umkm ke CardUmkm
    private fun Umkm.toCardUmkm(): CardUmkm {
        Log.d(TAG, "Converting Umkm to CardUmkm: $this")

        val cardUmkm = CardUmkm(
            id = this.id,
            namaUsaha = this.nama_usaha,
            namaProduk = this.nama_produk,
            harga = this.harga_produk,
            fotoProduk = this.foto_produk,
            status = this.status,
            tanggalDaftar = this.created_at,
            kategori = this.kategori,
        )

        Log.d(TAG, "Converted to CardUmkm: $cardUmkm")
        return cardUmkm
    }
}