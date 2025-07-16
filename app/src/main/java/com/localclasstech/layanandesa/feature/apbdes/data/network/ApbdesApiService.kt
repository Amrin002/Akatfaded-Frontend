package com.localclasstech.layanandesa.feature.apbdes.data.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApbdesApiService {

    @GET("api/apbdes")
    suspend fun getAllApbdes(): Response<ApbdesResponse>

    @GET("api/apbdes/{id}")
    suspend fun getApbdesById(@Path("id") id: Int): Response<ApbdesDetailResponse>
}