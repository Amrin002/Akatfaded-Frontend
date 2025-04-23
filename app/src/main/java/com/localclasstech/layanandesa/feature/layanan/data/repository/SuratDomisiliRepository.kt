package com.localclasstech.layanandesa.feature.layanan.data.repository

import com.localclasstech.layanandesa.feature.layanan.data.network.apiservice.SuratApiService
import com.localclasstech.layanandesa.feature.layanan.data.network.data.suratdomisili.CreateSuratDomisiliRequest
import com.localclasstech.layanandesa.settings.PreferencesHelper

class SuratDomisiliRepository(
    private val api: SuratApiService,
    private val preferencesHelper: PreferencesHelper
) {
    private fun getBearerToken(): String {
        val token = preferencesHelper.getToken()
        return "Bearer $token"
    }

    suspend fun getAllSurat() = api.getAllSurat(getBearerToken())

    suspend fun getSuratById(id: Int) = api.getSuratById(getBearerToken(), id)

    suspend fun createSurat(request: CreateSuratDomisiliRequest) =
        api.createSurat(getBearerToken(), request)

    suspend fun updateSurat(id: Int, request: CreateSuratDomisiliRequest) =
        api.updateSurat(getBearerToken(), id, request)

    suspend fun deleteSurat(id: Int) = api.deleteSurat(getBearerToken(), id)
}
