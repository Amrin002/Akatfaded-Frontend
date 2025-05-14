package com.localclasstech.layanandesa.feature.layanan.data.repository

import android.util.Log
import com.localclasstech.layanandesa.feature.layanan.data.network.apiservice.SuratApiService
import com.localclasstech.layanandesa.feature.layanan.data.network.data.BaseResponse
import com.localclasstech.layanandesa.feature.layanan.data.network.data.DownloadUrlResponse
import com.localclasstech.layanandesa.feature.layanan.data.network.data.suratktu.CreateSuratKtuRequest
import com.localclasstech.layanandesa.feature.layanan.data.network.data.suratktu.SuratKtuResponse
import com.localclasstech.layanandesa.settings.PreferencesHelper
import okhttp3.ResponseBody
import retrofit2.Response

class SuratKtuRepository(
    private val apiService: SuratApiService,
    private val preferencesHelper: PreferencesHelper
) {

    // Get the bearer token from preferences
    private fun getBearerToken(): String {
        val token = preferencesHelper.getToken()
        Log.d("SuratKtuRepository", "Bearer Token: $token")
        return "Bearer $token"
    }

    // Create Surat KTU
    suspend fun createSuratKtu(request: CreateSuratKtuRequest): Response<BaseResponse<SuratKtuResponse>> {
        return apiService.createSuratKtu(getBearerToken(), request)
    }

    // Get Surat KTU list by user
    suspend fun getSuratKtuByUser(id: Int): Response<BaseResponse<List<SuratKtuResponse>>> {
        return apiService.getSuratKtuByUser(getBearerToken())
    }

    // Get Surat KTU detail by ID
    suspend fun getDetailSuratKtuById(id: Int): Response<BaseResponse<SuratKtuResponse>> {
        return apiService.getDetailSuratKtuById(getBearerToken(), id)
    }

    // Update Surat KTU by ID
    suspend fun updateSuratKtu(id: Int, request: CreateSuratKtuRequest): Response<BaseResponse<SuratKtuResponse>> {
        return apiService.updateSuratKtu(getBearerToken(), id, request)
    }

    // Delete Surat KTU by ID
    suspend fun deleteSuratKtu(id: Int): Response<BaseResponse<String>> {
        return apiService.deleteSuratKtu(getBearerToken(), id)
    }

    // Export Surat KTU as PDF
    suspend fun exportPdfSuratKtu(id: Int): Response<ResponseBody> {
        return apiService.exportPdfSuratKtu(getBearerToken(), id)
    }

    // Get download URL for Surat KTU
    suspend fun getDownloadUrlSuratKtu(id: Int): Response<DownloadUrlResponse> {
        return apiService.getDownloadUrlSuratKtu(getBearerToken(), id)
    }
}
