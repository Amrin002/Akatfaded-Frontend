package com.localclasstech.layanandesa.feature.keluhan.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localclasstech.layanandesa.feature.keluhan.data.CardListKeluhan
import com.localclasstech.layanandesa.feature.keluhan.data.repository.KeluhanRepository
import com.localclasstech.layanandesa.feature.keluhan.data.toCardListKeluhan
import kotlinx.coroutines.launch

class KeluhanViewModel(private val repository: KeluhanRepository) : ViewModel() {
    private val _listKeluhans = MutableLiveData<List<CardListKeluhan>>()
    val listKeluhans: LiveData<List<CardListKeluhan>> = _listKeluhans

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun fetchKeluhans(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Mengambil data keluhan dari repository
                val response = repository.getKeluhanByUser(id)

                if (response.isSuccessful) {
                    // Jika response berhasil, kita ambil data dan map ke bentuk yang sesuai
                    val data = response.body()?.data.orEmpty()

                    // Ubah data keluhan menjadi format yang sesuai dengan kebutuhan UI
                    val listMapped = data.map { it.toCardListKeluhan() }

                    // Posting hasil yang sudah di-mapping ke LiveData
                    _listKeluhans.postValue(listMapped)

                    // Reset error message
                    _error.postValue(null)
                } else {
                    // Jika ada error dalam response
                    _error.postValue("Gagal memuat data: ${response.message()}")
                }
            } catch (e: Exception) {
                // Menangani error jika terjadi kesalahan lain
                _error.postValue("Terjadi kesalahan: ${e.localizedMessage}")
            } finally {
                // Setelah selesai, set loading menjadi false
                _isLoading.postValue(false)
            }
        }
    }
}
