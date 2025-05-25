package com.localclasstech.layanandesa.feature.keluhan.data.network

import com.localclasstech.layanandesa.feature.keluhan.data.Keluhan
import com.localclasstech.layanandesa.feature.keluhan.data.KeluhanRequest
import com.localclasstech.layanandesa.feature.layanan.data.network.data.BaseResponse

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface KeluhanApiService {
    @GET("api/keluhan")
    suspend fun getKeluhans(
        @Header("Authorization") token: String
    ):Response<BaseResponse<List<Keluhan>>>


    @POST("api/keluhan")
    suspend fun createKeluhan(
        @Header("Authorization") token: String,
        @Body request: KeluhanRequest
    ): Response<BaseResponse<Keluhan>>

    @PUT("api/keluhan/{id}")
    suspend fun updateKeluhan(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body request: KeluhanRequest
    ): Response<BaseResponse<Keluhan>>

    @GET("api/keluhan/{id}")
    suspend fun getKeluhanById(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<BaseResponse<Keluhan>>

    @DELETE("api/keluhan/{id}")
    suspend fun deleteKeluhan(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<BaseResponse<String>>

}