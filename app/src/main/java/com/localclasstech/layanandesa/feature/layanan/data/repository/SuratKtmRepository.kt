package com.localclasstech.layanandesa.feature.layanan.data.repository

import android.util.Log
import com.localclasstech.layanandesa.feature.layanan.data.network.apiservice.SuratApiService
import com.localclasstech.layanandesa.feature.layanan.data.network.data.BaseResponseAll
import com.localclasstech.layanandesa.feature.layanan.data.network.data.DownloadUrlResponse
import com.localclasstech.layanandesa.feature.layanan.data.network.data.suratktm.CreateSktmRequest
import com.localclasstech.layanandesa.feature.layanan.data.network.data.suratktm.SktmResponse
import com.localclasstech.layanandesa.settings.PreferencesHelper
import okhttp3.ResponseBody
import retrofit2.Response

class SuratKtmRepository(
    private val apiService: SuratApiService,
    private val preferencesHelper: PreferencesHelper
) {
    private fun getBearerToken(): String {
        val token = preferencesHelper.getToken()
        Log.d("SuratKtmRepository", "Bearer Token: $token")
        return "Bearer $token"
    }

    suspend fun createSuratKtm(request: CreateSktmRequest): Response<BaseResponseAll<SktmResponse>> {
        return apiService.createSuratKtm(getBearerToken(), request)
    }

    suspend fun getSuratKtmByUser(id: Int): Response<BaseResponseAll<List<SktmResponse>>> {

        return apiService.getSuratKtmByUser(getBearerToken())
    }
    suspend fun deleteSuratKtm(id: Int): Response<BaseResponseAll<String>> {
        return apiService.deleteSuratKtm(getBearerToken(), id)
    }
    suspend fun getDetailSuratKtmById(id: Int): Response<BaseResponseAll<SktmResponse>> {
        return apiService.getDetailSuratKtmById(getBearerToken(), id)
    }

    suspend fun updateSuratKtm(id: Int, request: CreateSktmRequest): Response<BaseResponseAll<SktmResponse>> {
        return apiService.updateSuratKtm(getBearerToken(), id, request)
    }
    suspend fun exportPdfSuratKtm(id: Int): Response<ResponseBody> {
        return apiService.exportPdfSuratKtm(getBearerToken(), id)
    }

    suspend fun getDownloadUrl(id: Int): Response<DownloadUrlResponse> {
        return apiService.getDownloadUrl(getBearerToken(), id)
    }
}
