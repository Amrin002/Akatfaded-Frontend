package com.localclasstech.layanandesa.feature.layanan.data.network.apiservice

import com.localclasstech.layanandesa.feature.layanan.data.network.data.BaseResponse
import com.localclasstech.layanandesa.feature.layanan.data.network.data.suratdomisili.CreateSuratDomisiliRequest
import com.localclasstech.layanandesa.feature.layanan.data.network.data.suratdomisili.SuratDomisiliResponse
import com.localclasstech.layanandesa.feature.layanan.data.network.data.suratktm.CreateSktmRequest
import com.localclasstech.layanandesa.feature.layanan.data.network.data.suratktm.SktmResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface SuratApiService {

    @POST("api/suratktm")
    suspend fun createSuratKtm(
        @Header("Authorization") token: String,
        @Body request: CreateSktmRequest
    ): Response<BaseResponse<SktmResponse>>

    @GET("api/suratktm")
    suspend fun getSuratKtmByUser(
        @Header("Authorization") token: String,
    ): Response<BaseResponse<List<SktmResponse>>>

    @GET("api/suratktm/{id}")
    suspend fun getDetailSuratKtmById(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<BaseResponse<SktmResponse>>
    @PUT("api/suratktm/{id}")
    suspend fun updateSuratKtm(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body request: CreateSktmRequest
    ): Response<BaseResponse<SktmResponse>>

    @DELETE("api/suratktm/{id}")
    suspend fun deleteSuratKtm(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<BaseResponse<String>>


//    @GET("suratktm/{id}/export")
//    suspend fun exportPdfSuratKtm(
//        @Path("id") id: Int
//    ): Response<ResponseBody>


    //Surat Domisili
    @GET("api/suratdomisili")
    suspend fun getAllSurat(
        @Header("Authorization") token: String,
    ): Response<BaseResponse<List<SuratDomisiliResponse>>>

    @GET("api/suratdomisili/{id}")
    suspend fun getSuratById(
        @Header("Authorization") token: String,
        @Path("id") id: Int): Response<BaseResponse<SuratDomisiliResponse>>

    @POST("api/suratdomisili")
    suspend fun createSurat(
        @Header("Authorization") token: String,
        @Body request: CreateSuratDomisiliRequest): Response<BaseResponse<SuratDomisiliResponse>>

    @PUT("api/suratdomisili/{id}")
    suspend fun updateSurat( @Header("Authorization") token: String, @Path("id") id: Int, @Body request: CreateSuratDomisiliRequest): Response<BaseResponse<SuratDomisiliResponse>>

    @DELETE("api/suratdomisili/{id}")
    suspend fun deleteSurat( @Header("Authorization") token: String, @Path("id") id: Int): Response<BaseResponse<Unit>>
}
