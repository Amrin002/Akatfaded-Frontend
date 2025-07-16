package com.localclasstech.layanandesa.feature.layanan.data.repository

import com.localclasstech.layanandesa.feature.layanan.data.network.apiservice.SuratApiService
import com.localclasstech.layanandesa.feature.layanan.data.network.data.BaseResponseAll
import com.localclasstech.layanandesa.feature.layanan.data.network.data.DownloadUrlResponse
import com.localclasstech.layanandesa.feature.layanan.data.network.data.suratdomisili.CreateSuratDomisiliRequest
import com.localclasstech.layanandesa.feature.layanan.data.network.data.suratdomisili.SuratDomisiliResponse
import com.localclasstech.layanandesa.settings.PreferencesHelper
import retrofit2.Response

class SuratDomisiliRepository(
    private val api: SuratApiService,
    private val preferencesHelper: PreferencesHelper
) {
    private fun getBearerToken(): String {
        val token = preferencesHelper.getToken()
        return "Bearer $token"
    }

    suspend fun getSuratDomisiliByUser(id: Int) :Response<BaseResponseAll<List<SuratDomisiliResponse>>>{
        return api.getSuratDomisiliByUser(getBearerToken())
    }
    suspend fun getDetailSuratDomisiliById(id: Int): Response<BaseResponseAll<SuratDomisiliResponse>> {
        return api.getDetailSuratDomisiliById(getBearerToken(), id)
    }

    suspend fun getSuratById(id: Int) = api.getSuratById(getBearerToken(), id)

    suspend fun createSuratDomisili(request: CreateSuratDomisiliRequest): Response<BaseResponseAll<SuratDomisiliResponse>> {
        return api.createSuratDomisili(getBearerToken(), request)
    }


    suspend fun updateSuratDomisili(id: Int, request: CreateSuratDomisiliRequest): Response<BaseResponseAll<SuratDomisiliResponse>>{
        return api.updateSuratDomisili(getBearerToken(), id, request)

    }

    suspend fun deleteSuratDomisili(id: Int): Response<BaseResponseAll<String>> {
        return api.deleteSuratDomisili(getBearerToken(), id) //Type mismatch: inferred type is Response<BaseResponse<Unit>> but Response<BaseResponse<String>> was expected
    }

    suspend fun getDownloadUrl(id: Int): Response<DownloadUrlResponse> {
        return api.getDownloadUrlDomisili(getBearerToken(), id)
    }

}
