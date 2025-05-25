package com.localclasstech.layanandesa.feature.keluhan.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localclasstech.layanandesa.feature.keluhan.data.Keluhan
import com.localclasstech.layanandesa.feature.keluhan.data.KeluhanRequest
import com.localclasstech.layanandesa.feature.keluhan.data.repository.KeluhanRepository
import kotlinx.coroutines.launch

class DetailkeluhanViewModel(private val repository: KeluhanRepository) : ViewModel() {
    private val _detailKeluhan = MutableLiveData<Keluhan>()
    val detailKeluhan: LiveData<Keluhan> get() = _detailKeluhan

    private val _operationResult = MutableLiveData<Boolean>()
    val operationResult: LiveData<Boolean> get() = _operationResult

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _deleteResult = MutableLiveData<Boolean>()
    val deleteResult: LiveData<Boolean> get() = _deleteResult

    fun fetchKeluhanDetail(id: Int) {
        viewModelScope.launch {
            try {
                val response = repository.getDetailKeluhanById(id)
                if (response.isSuccessful && response.body() != null) {
                    _detailKeluhan.postValue(response.body()?.data) //Type mismatch: inferred type is List<Keluhan>? but Keluhan! was expected
                } else {
                    _error.postValue("Gagal memuat detail keluhan: ${response.message()}")
                }
            }catch (e: Exception){
                _error.postValue("Error: ${e.message}")
                Log.e("DetailkeluhanViewModel", "Exception: ${e.message}")
            }
        }
    }

    fun createKeluhan(request: KeluhanRequest) {
        viewModelScope.launch {
            try {
                val response = repository.createKeluhan(request)
                if (response.isSuccessful && response.body() != null) {
                    _operationResult.postValue(true)
                } else {
                    _operationResult.postValue(false)
                    _error.postValue("Gagal membuat keluhan: ${response.message()}")
                }
            } catch (e: Exception) {
                _operationResult.postValue(false)
                _error.postValue("Error: ${e.message}")
                Log.e("DetailkeluhanViewModel", "Exception: ${e.message}")
            }
        }
    }

    fun updateKeluhan(id: Int, request: KeluhanRequest) {
        viewModelScope.launch {
            try {
                val response = repository.updateKeluhan(id, request)
                if (response.isSuccessful && response.body() != null) {
                    _operationResult.postValue(true)
                } else {
                    _operationResult.postValue(false)
                    _error.postValue("Gagal memperbarui keluhan: ${response.message()}")
                    }
            } catch (e: Exception) {
                _operationResult.postValue(false)
                _error.postValue("Error: ${e.message}")
                Log.e("DetailkeluhanViewModel", "Exception: ${e.message}")
            }
        }
    }

    fun deleteKeluhan(id: Int) {
        viewModelScope.launch {
            try {
                val response = repository.deleteKeluhan(id)
                if (response.isSuccessful && response.body() != null) {
                    _deleteResult.postValue(true)
                } else {
                    _deleteResult.postValue(false)
                    _error.postValue("Gagal menghapus keluhan: ${response.message()}")
                }
                } catch (e: Exception) {
                _deleteResult.postValue(false)
                _error.postValue("Error: ${e.message}")
                Log.e("DetailkeluhanViewModel", "Exception: ${e.message}")
            }
        }
    }
}