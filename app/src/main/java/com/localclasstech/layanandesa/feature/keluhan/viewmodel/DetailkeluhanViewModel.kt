package com.localclasstech.layanandesa.feature.keluhan.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localclasstech.layanandesa.feature.keluhan.data.Keluhan
import com.localclasstech.layanandesa.feature.keluhan.data.KeluhanRequest

import com.localclasstech.layanandesa.feature.keluhan.data.repository.KeluhanRepository
import kotlinx.coroutines.launch

class DetailkeluhanViewModel(private val repository: KeluhanRepository) : ViewModel() {
    private val _keluhanDetail = MutableLiveData<Keluhan>()
    val keluhanDetail: LiveData<Keluhan> get() = _keluhanDetail

    private val _operationResult = MutableLiveData<Boolean>()
    val operationResult: LiveData<Boolean> get() = _operationResult

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    /**
     * Mengambil detail keluhan berdasarkan ID
     */
    fun getKeluhanDetail(id: Int) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.getDetailKeluhanById(id)
                if (response.isSuccessful) {
                    _keluhanDetail.postValue(response.body()?.data)
                } else {
                    _error.postValue("Gagal mengambil detail keluhan: ${response.message()}")
                }
            } catch (e: Exception) {
                _error.postValue("Error: ${e.message}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    /**
     * Mengirimkan keluhan baru (termasuk gambar)
     */
    fun createKeluhan(request: KeluhanRequest) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.createKeluhan(request)
                if (response.isSuccessful) {
                    _operationResult.postValue(true)
                } else {
                    _error.postValue("Gagal membuat keluhan: ${response.message()}")
                    _operationResult.postValue(false)
                }
            } catch (e: Exception) {
                _error.postValue("Gagal membuat keluhan: ${e.message}")
                _operationResult.postValue(false)
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    /**
     * Memperbarui keluhan (termasuk gambar)
     */
    fun updateKeluhan(id: Int, request: KeluhanRequest) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.updateKeluhan(id, request)
                if (response.isSuccessful) {
                    _operationResult.postValue(true)
                } else {
                    _error.postValue("Gagal memperbarui keluhan: ${response.message()}")
                    _operationResult.postValue(false)
                }
            } catch (e: Exception) {
                _error.postValue("Gagal memperbarui keluhan: ${e.message}")
                _operationResult.postValue(false)
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    /**
     * Menghapus keluhan berdasarkan ID
     */
    fun deleteKeluhan(id: Int) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.deleteKeluhan(id)
                if (response.isSuccessful) {
                    _operationResult.postValue(true)
                } else {
                    _error.postValue("Gagal menghapus keluhan: ${response.message()}")
                    _operationResult.postValue(false)
                }
            } catch (e: Exception) {
                _error.postValue("Gagal menghapus keluhan: ${e.message}")
                _operationResult.postValue(false)
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
}

