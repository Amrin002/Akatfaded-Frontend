package com.localclasstech.layanandesa.feature.layanan.data.repository

import android.util.Log
import com.localclasstech.layanandesa.feature.layanan.data.network.apiservice.SuratApiService
import com.localclasstech.layanandesa.feature.layanan.data.network.data.BaseResponseAll
import com.localclasstech.layanandesa.feature.layanan.data.network.data.DownloadUrlResponse
import com.localclasstech.layanandesa.feature.layanan.data.network.data.suratpindahdomisili.CreateSuratPindahRequest
import com.localclasstech.layanandesa.feature.layanan.data.network.data.suratpindahdomisili.SuratPindahResponse
import com.localclasstech.layanandesa.settings.PreferencesHelper
import okhttp3.ResponseBody
import retrofit2.Response

class SuratPindahRepository(
    private val apiService: SuratApiService,
    private val preferencesHelper: PreferencesHelper
) {

    // Get the bearer token from preferences
    private fun getBearerToken(): String {
        val token = preferencesHelper.getToken()
        Log.d("SuratPindahRepository", "Bearer Token: $token")
        return "Bearer $token"
    }

    // Create Surat Pindah
    suspend fun createSuratPindah(request: CreateSuratPindahRequest): Response<BaseResponseAll<SuratPindahResponse>> {
        return apiService.createSuratPindah(getBearerToken(), request)
    }

    // Get Surat Pindah list by user
    suspend fun getSuratPindahByUser(id: Int): Response<BaseResponseAll<List<SuratPindahResponse>>> {
        return apiService.getSuratPindahByUser(getBearerToken())
    }

    // Get Surat Pindah detail by ID
    suspend fun getDetailSuratPindahById(id: Int): Response<BaseResponseAll<SuratPindahResponse>> {
        return apiService.getDetailSuratPindahById(getBearerToken(), id)
    }

    // Update Surat Pindah by ID
    suspend fun updateSuratPindah(id: Int, request: CreateSuratPindahRequest): Response<BaseResponseAll<SuratPindahResponse>> {
        return apiService.updateSuratPindah(getBearerToken(), id, request)
    }

    // Delete Surat Pindah by ID
    suspend fun deleteSuratPindah(id: Int): Response<BaseResponseAll<String>> {
        return apiService.deleteSuratPindah(getBearerToken(), id)
    }

    // Export Surat Pindah as PDF
    suspend fun exportPdfSuratPindah(id: Int): Response<ResponseBody> {
        return apiService.exportPdfSuratPindah(getBearerToken(), id)
    }

    // Get download URL for Surat Pindah
    suspend fun getDownloadUrl(id: Int): Response<DownloadUrlResponse> {
        return apiService.getDownloadUrlSuratPindah(getBearerToken(), id)
    }
}
