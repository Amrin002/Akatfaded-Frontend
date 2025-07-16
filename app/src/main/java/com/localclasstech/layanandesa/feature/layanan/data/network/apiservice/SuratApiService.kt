package com.localclasstech.layanandesa.feature.layanan.data.network.apiservice

import com.localclasstech.layanandesa.feature.layanan.data.network.data.BaseResponseAll
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
    ): Response<BaseResponseAll<SktmResponse>>

    @GET("api/suratktm")
    suspend fun getSuratKtmByUser(
        @Header("Authorization") token: String,
    ): Response<BaseResponseAll<List<SktmResponse>>>

    @GET("api/suratktm/{id}")
    suspend fun getDetailSuratKtmById(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<BaseResponseAll<SktmResponse>>
    @PUT("api/suratktm/{id}")
    suspend fun updateSuratKtm(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body request: CreateSktmRequest
    ): Response<BaseResponseAll<SktmResponse>>

    @DELETE("api/suratktm/{id}")
    suspend fun deleteSuratKtm(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<BaseResponseAll<String>>


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
    ): Response<BaseResponseAll<List<SuratDomisiliResponse>>>
    @GET("api/suratdomisili/{id}")
    suspend fun getDetailSuratDomisiliById(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<BaseResponseAll<SuratDomisiliResponse>>

    @GET("api/suratdomisili/{id}")
    suspend fun getSuratById(
        @Header("Authorization") token: String,
        @Path("id") id: Int): Response<BaseResponseAll<SuratDomisiliResponse>>

    @POST("api/suratdomisili")
    suspend fun createSuratDomisili(
        @Header("Authorization") token: String,
        @Body request: CreateSuratDomisiliRequest): Response<BaseResponseAll<SuratDomisiliResponse>>

    @PUT("api/suratdomisili/{id}")
    suspend fun updateSuratDomisili(@Header("Authorization") token: String, @Path("id") id: Int, @Body request: CreateSuratDomisiliRequest): Response<BaseResponseAll<SuratDomisiliResponse>>

    @DELETE("api/suratdomisili/{id}")
    suspend fun deleteSuratDomisili(@Header("Authorization") token: String, @Path("id") id: Int): Response<BaseResponseAll<String>>

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
): Response<BaseResponseAll<SuratPindahResponse>>

    @GET("api/suratpindah")
    suspend fun getSuratPindahByUser(
        @Header("Authorization") token: String,
    ): Response<BaseResponseAll<List<SuratPindahResponse>>>

    @GET("api/suratpindah/{id}")
    suspend fun getDetailSuratPindahById(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<BaseResponseAll<SuratPindahResponse>>

    @PUT("api/suratpindah/{id}")
    suspend fun updateSuratPindah(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body request: CreateSuratPindahRequest
    ): Response<BaseResponseAll<SuratPindahResponse>>

    @DELETE("api/suratpindah/{id}")
    suspend fun deleteSuratPindah(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<BaseResponseAll<String>>

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
    ): Response<BaseResponseAll<SuratKtuResponse>>

    @GET("api/suratktu")
    suspend fun getSuratKtuByUser(
        @Header("Authorization") token: String
    ): Response<BaseResponseAll<List<SuratKtuResponse>>>

    @GET("api/suratktu/{id}")
    suspend fun getDetailSuratKtuById(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<BaseResponseAll<SuratKtuResponse>>

    @PUT("api/suratktu/{id}")
    suspend fun updateSuratKtu(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body request: CreateSuratKtuRequest
    ): Response<BaseResponseAll<SuratKtuResponse>>

    @DELETE("api/suratktu/{id}")
    suspend fun deleteSuratKtu(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<BaseResponseAll<String>>

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
