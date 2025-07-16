package com.localclasstech.layanandesa.feature.apbdes.viewmodel

import android.icu.text.DecimalFormat
import android.icu.util.Calendar
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localclasstech.layanandesa.feature.apbdes.data.network.ApbdesItem
import com.localclasstech.layanandesa.feature.apbdes.data.repository.ApbdesRepository
import kotlinx.coroutines.launch

class ApbdesViewModel(private val repository: ApbdesRepository) : ViewModel() {

    private val _apbdesList = MutableLiveData<List<ApbdesItem>>()
    val apbdesList: LiveData<List<ApbdesItem>> = _apbdesList

    private val _filteredApbdesList = MutableLiveData<List<ApbdesItem>>()
    val filteredApbdesList: LiveData<List<ApbdesItem>> = _filteredApbdesList

    private val _selectedApbdes = MutableLiveData<ApbdesItem>()
    val selectedApbdes: LiveData<ApbdesItem> = _selectedApbdes

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _availableYears = MutableLiveData<List<Int>>()
    val availableYears: LiveData<List<Int>> = _availableYears

    private val _selectedYear = MutableLiveData<Int>()
    val selectedYear: LiveData<Int> = _selectedYear

    init {
        // Set tahun default ke tahun saat ini
        _selectedYear.value = Calendar.getInstance().get(Calendar.YEAR)
        loadAllApbdes()
    }

    fun loadAllApbdes() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            repository.getAllApbdes()
                .onSuccess { apbdesList ->
                    _apbdesList.value = apbdesList

                    // Extract available years and sort them
                    val years = apbdesList.map { it.tahun }.distinct().sortedDescending()
                    _availableYears.value = years

                    // Filter by selected year if available
                    _selectedYear.value?.let { year ->
                        filterByYear(year)
                    }
                }
                .onFailure { exception ->
                    _errorMessage.value = exception.message
                }

            _isLoading.value = false
        }
    }

    fun loadApbdesById(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            repository.getApbdesById(id)
                .onSuccess { apbdes ->
                    _selectedApbdes.value = apbdes
                }
                .onFailure { exception ->
                    _errorMessage.value = exception.message
                }

            _isLoading.value = false
        }
    }

    fun filterByYear(year: Int) {
        _selectedYear.value = year
        val allData = _apbdesList.value ?: emptyList()
        val filtered = allData.filter { it.tahun == year }
        _filteredApbdesList.value = filtered

        // Set selected apbdes to first item if available
        if (filtered.isNotEmpty()) {
            _selectedApbdes.value = filtered.first()
        }
    }

    fun setSelectedYear(year: Int) {
        if (_selectedYear.value != year) {
            filterByYear(year)
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun refreshData() {
        loadAllApbdes()
    }

    // Helper function to format currency
    fun formatCurrency(amount: String): String {
        return try {
            val number = amount.toLongOrNull() ?: 0L
            "Rp. ${DecimalFormat("#,###").format(number)}"
        } catch (e: Exception) {
            "Rp. $amount"
        }
    }

    // Get current selected apbdes data for UI
    fun getCurrentApbdesData(): ApbdesItem? {
        return _selectedApbdes.value
    }
}