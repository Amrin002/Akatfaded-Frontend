package com.localclasstech.layanandesa.feature.pengaturan.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.localclasstech.layanandesa.network.ApiService
import com.localclasstech.layanandesa.settings.PreferencesHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PengaturanViewModel(private val preferencesHelper: PreferencesHelper,
    private val apiService: ApiService
    ) : ViewModel() {
        
    private val _logoutEvent = MutableLiveData<Boolean>()
    val logoutEvent: LiveData<Boolean> get() = _logoutEvent

    fun confirmLogout() {
        // Panggil API logout
        apiService.logout().enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    // Logout berhasil di sisi server
                    // Hapus token dan data login di lokal
                    preferencesHelper.clearLogin()
                    _logoutEvent.value = true
                } else {
                    // Tangani error logout
                    _logoutEvent.value = false
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                // Tangani kegagalan koneksi
                _logoutEvent.value = false
            }
        })
    }



}