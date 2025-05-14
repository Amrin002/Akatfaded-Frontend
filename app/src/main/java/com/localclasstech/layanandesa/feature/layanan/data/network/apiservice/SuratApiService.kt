package com.localclasstech.layanandesa.feature.layanan.data.network.apiservice

import com.localclasstech.layanandesa.feature.layanan.data.network.data.BaseResponse
import com.localclasstech.layanandesa.feature.layanan.data.network.data.DownloadUrlResponse
import com.localclasstech.layanandesa.feature.layanan.data.network.data.suratdomisili.CreateSuratDomisiliRequest
import com.localclasstech.layanandesa.feature.layanan.data.network.data.suratdomisili.SuratDomisiliResponse
import com.localclasstech.layanandesa.feature.layanan.data.network.data.suratktm.CreateSktmRequest
import com.localclasstech.layanandesa.feature.layanan.data.network.data.suratktm.SktmResponse
import com.localclasstech.layanandesa.feature.layanan.data.network.data.suratktu.CreateSuratKtuRequest
import com.localclasstech.layanandesa.feature.layanan.data.network.data.suratktu.SuratKtuResponse
import com.localclasstech.layanandesa.feature.layanan.data.network.data.suratpindahdomisili.CreateSuratPindahRequest
import com.localclasstech.layanandesa.feature.layanan.data.network.data.suratpindahdomisili.SuratPindahResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Streaming

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


    @GET("api/suratktm/{id}/export")
    @Streaming
    suspend fun exportPdfSuratKtm(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<ResponseBody>

    @GET("api/suratktm/{id}/get-download-url")
    suspend fun getDownloadUrl(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<DownloadUrlResponse>

    //Surat Domisili
    @GET("api/suratdomisili")
    suspend fun getSuratDomisiliByUser(
        @Header("Authorization") token: String,
    ): Response<BaseResponse<List<SuratDomisiliResponse>>>
    @GET("api/suratdomisili/{id}")
    suspend fun getDetailSuratDomisiliById(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<BaseResponse<SuratDomisiliResponse>>

    @GET("api/suratdomisili/{id}")
    suspend fun getSuratById(
        @Header("Authorization") token: String,
        @Path("id") id: Int): Response<BaseResponse<SuratDomisiliResponse>>

    @POST("api/suratdomisili")
    suspend fun createSuratDomisili(
        @Header("Authorization") token: String,
        @Body request: CreateSuratDomisiliRequest): Response<BaseResponse<SuratDomisiliResponse>>

    @PUT("api/suratdomisili/{id}")
    suspend fun updateSuratDomisili(@Header("Authorization") token: String, @Path("id") id: Int, @Body request: CreateSuratDomisiliRequest): Response<BaseResponse<SuratDomisiliResponse>>

    @DELETE("api/suratdomisili/{id}")
    suspend fun deleteSuratDomisili(@Header("Authorization") token: String, @Path("id") id: Int): Response<BaseResponse<String>>

    // Untuk mendapatkan URL download Surat Domisili
    @GET("api/suratdomisili/{id}/get-download-url")
    suspend fun getDownloadUrlDomisili(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<DownloadUrlResponse>

//    Surat Pindah
@POST("api/suratpindah")
suspend fun createSuratPindah(
    @Header("Authorization") token: String,
    @Body request: CreateSuratPindahRequest
): Response<BaseResponse<SuratPindahResponse>>

    @GET("api/suratpindah")
    suspend fun getSuratPindahByUser(
        @Header("Authorization") token: String,
    ): Response<BaseResponse<List<SuratPindahResponse>>>

    @GET("api/suratpindah/{id}")
    suspend fun getDetailSuratPindahById(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<BaseResponse<SuratPindahResponse>>

    @PUT("api/suratpindah/{id}")
    suspend fun updateSuratPindah(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body request: CreateSuratPindahRequest
    ): Response<BaseResponse<SuratPindahResponse>>

    @DELETE("api/suratpindah/{id}")
    suspend fun deleteSuratPindah(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<BaseResponse<String>>

    @GET("api/suratpindah/{id}/export")
    @Streaming
    suspend fun exportPdfSuratPindah(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<ResponseBody>

    @GET("api/suratpindah/{id}/get-download-url")
    suspend fun getDownloadUrlSuratPindah(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<DownloadUrlResponse>

    //Surat KTU
    @POST("api/suratktu")
    suspend fun createSuratKtu(
        @Header("Authorization") token: String,
        @Body request: CreateSuratKtuRequest
    ): Response<BaseResponse<SuratKtuResponse>>

    @GET("api/suratktu")
    suspend fun getSuratKtuByUser(
        @Header("Authorization") token: String
    ): Response<BaseResponse<List<SuratKtuResponse>>>

    @GET("api/suratktu/{id}")
    suspend fun getDetailSuratKtuById(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<BaseResponse<SuratKtuResponse>>

    @PUT("api/suratktu/{id}")
    suspend fun updateSuratKtu(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body request: CreateSuratKtuRequest
    ): Response<BaseResponse<SuratKtuResponse>>

    @DELETE("api/suratktu/{id}")
    suspend fun deleteSuratKtu(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<BaseResponse<String>>

    @GET("api/suratktu/{id}/export")
    @Streaming
    suspend fun exportPdfSuratKtu(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<ResponseBody>

    @GET("api/suratktu/{id}/get-download-url")
    suspend fun getDownloadUrlSuratKtu(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<DownloadUrlResponse>

}
