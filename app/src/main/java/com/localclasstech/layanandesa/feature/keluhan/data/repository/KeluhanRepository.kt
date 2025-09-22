package com.localclasstech.layanandesa.feature.keluhan.data.repository

import android.util.Log
import com.localclasstech.layanandesa.feature.keluhan.data.Keluhan
import com.localclasstech.layanandesa.feature.keluhan.data.KeluhanRequest
import com.localclasstech.layanandesa.feature.keluhan.data.network.KeluhanApiService
import com.localclasstech.layanandesa.feature.layanan.data.network.data.BaseResponseAll

import com.localclasstech.layanandesa.settings.PreferencesHelper
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response

class KeluhanRepository(
    private val apiService: KeluhanApiService,
    private val preferencesHelper: PreferencesHelper
) {
    private fun getBearerToken():String{
        val token = preferencesHelper.getToken()
        return "Bearer $token"
    }

    suspend fun createKeluhan(request: KeluhanRequest): Response<BaseResponseAll<Keluhan>> {
        val judulRequestBody = request.judul.toRequestBody("text/plain".toMediaTypeOrNull())
        val isiRequestBody = request.isi.toRequestBody("text/plain".toMediaTypeOrNull())

        return apiService.createKeluhan(
            getBearerToken(),
            request.gambar,
            judulRequestBody,
            isiRequestBody
        )
    }

    suspend fun getKeluhanByUser(id: Int): Response<BaseResponseAll<List<Keluhan>>>{
        return apiService.getKeluhans(getBearerToken())
    }

    suspend fun getDetailKeluhanById(id: Int): Response<BaseResponseAll<Keluhan>>{
        return apiService.getKeluhanById(getBearerToken(), id)
    }

    suspend fun updateKeluhan(id: Int, request: KeluhanRequest): Response<BaseResponseAll<Keluhan>> {
        val judulRequestBody = request.judul.toRequestBody("text/plain".toMediaTypeOrNull())
        val isiRequestBody = request.isi.toRequestBody("text/plain".toMediaTypeOrNull())

        // Tambahkan _method untuk override ke PUT
        val methodRequestBody = "PUT".toRequestBody("text/plain".toMediaTypeOrNull())

        // Debug log
        Log.d("KeluhanRepository", "Updating keluhan $id with judul: ${request.judul}, isi: ${request.isi}")

        return apiService.updateKeluhan(
            getBearerToken(),
            id,
            methodRequestBody,  // _method
            request.gambar,
            judulRequestBody,
            isiRequestBody
        )
    }

    suspend fun deleteKeluhan(id: Int):Response<BaseResponseAll<String>>{
        return apiService.deleteKeluhan(getBearerToken(), id)
    }
}