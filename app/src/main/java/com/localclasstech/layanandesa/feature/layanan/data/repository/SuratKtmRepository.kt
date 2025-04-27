package com.localclasstech.layanandesa.feature.layanan.data.repository

import android.util.Log
import com.localclasstech.layanandesa.feature.layanan.data.network.apiservice.SuratApiService
import com.localclasstech.layanandesa.feature.layanan.data.network.data.BaseResponse
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

    suspend fun createSuratKtm(request: CreateSktmRequest): Response<BaseResponse<SktmResponse>> {
        return apiService.createSuratKtm(getBearerToken(), request)
    }

    suspend fun getSuratKtmByUser(id: Int): Response<BaseResponse<List<SktmResponse>>> {

        return apiService.getSuratKtmByUser(getBearerToken())
    }

    suspend fun getDetailSuratKtmById(id: Int): Response<BaseResponse<SktmResponse>> {
        return apiService.getDetailSuratKtmById(getBearerToken(), id)
    }

    suspend fun updateSuratKtm(id: Int, request: CreateSktmRequest): Response<BaseResponse<SktmResponse>> {
        return apiService.updateSuratKtm(getBearerToken(), id, request)
    }
    suspend fun exportPdfSuratKtm(id: Int): Response<ResponseBody> {
        return apiService.exportPdfSuratKtm(getBearerToken(), id)
    }
    suspend fun deleteSuratKtm(id: Int): Response<BaseResponse<String>> {
        return apiService.deleteSuratKtm(getBearerToken(), id)
    }
}
