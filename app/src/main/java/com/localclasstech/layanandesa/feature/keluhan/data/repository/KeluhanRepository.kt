package com.localclasstech.layanandesa.feature.keluhan.data.repository

import com.localclasstech.layanandesa.feature.keluhan.data.Keluhan
import com.localclasstech.layanandesa.feature.keluhan.data.KeluhanRequest
import com.localclasstech.layanandesa.feature.keluhan.data.network.KeluhanApiService
import com.localclasstech.layanandesa.feature.layanan.data.network.data.BaseResponse

import com.localclasstech.layanandesa.settings.PreferencesHelper
import retrofit2.Response

class KeluhanRepository(
    private val apiService: KeluhanApiService,
    private val preferencesHelper: PreferencesHelper
) {
    private fun getBearerToken():String{
        val token = preferencesHelper.getToken()
        return "Bearer $token"
    }

    suspend fun createKeluhan(request: KeluhanRequest): Response<BaseResponse<Keluhan>> {
        return apiService.createKeluhan(getBearerToken(), request.gambar, request.judul, request.isi)
    }

    suspend fun getKeluhanByUser(id: Int): Response<BaseResponse<List<Keluhan>>>{
        return apiService.getKeluhans(getBearerToken())
    }
    suspend fun getDetailKeluhanById(id: Int): Response<BaseResponse<Keluhan>>{
        return apiService.getKeluhanById(getBearerToken(), id)
    }
    suspend fun updateKeluhan(id: Int, request: KeluhanRequest): Response<BaseResponse<Keluhan>> {
        return apiService.updateKeluhan(getBearerToken(), id, request.gambar, request.judul, request.isi)
    }
    suspend fun deleteKeluhan(id: Int):Response<BaseResponse<String>>{
        return apiService.deleteKeluhan(getBearerToken(), id)
    }
}