package com.localclasstech.layanandesa.feature.umkm.data.network

import com.localclasstech.layanandesa.feature.umkm.data.CreateUmkmRequest
import com.localclasstech.layanandesa.feature.umkm.data.SingleUmkmResponse
import com.localclasstech.layanandesa.feature.umkm.data.UmkmOptionsResponse
import com.localclasstech.layanandesa.feature.umkm.data.UmkmPublicResponse
import com.localclasstech.layanandesa.feature.umkm.data.UmkmResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface UmkmApiService {

    // AUTHENTICATED ENDPOINTS
    @GET("api/umkm")
    suspend fun getMyUmkm(
        @Header("Authorization") token: String
    ): Response<UmkmResponse>
    @POST("api/umkm")
    suspend fun createUmkm(
        @Header("Authorization") token: String,
        @Body request: CreateUmkmRequest): Response<SingleUmkmResponse>

    @Multipart
    @POST("api/umkm")
    suspend fun createUmkmWithImage(
        @Header("Authorization") token: String,
        @Part("nama_usaha") namaUsaha: RequestBody,
        @Part("kategori") kategori: RequestBody,
        @Part("nama_produk") namaProduk: RequestBody,
        @Part("deskripsi_produk") deskripsiProduk: RequestBody,
        @Part("harga_produk") hargaProduk: RequestBody?, // TAMBAHAN
        @Part("nomor_telepon") nomorTelepon: RequestBody,
        @Part("link_facebook") linkFacebook: RequestBody?,
        @Part("link_instagram") linkInstagram: RequestBody?,
        @Part("link_tiktok") linkTiktok: RequestBody?,
        @Part fotoProduk: MultipartBody.Part?
    ): Response<SingleUmkmResponse>

    @GET("api/umkm/{id}")
    suspend fun getUmkmById(
        @Path("id") id: Int): Response<SingleUmkmResponse>

    @PUT("api/umkm/{id}")
    suspend fun updateUmkm(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body request: CreateUmkmRequest
    ): Response<SingleUmkmResponse>

    @Multipart
    @POST("api/umkm/{id}/update")
    suspend fun updateUmkmWithImage(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Part("_method") method: RequestBody, // PUT
        @Part("nama_usaha") namaUsaha: RequestBody,
        @Part("kategori") kategori: RequestBody,
        @Part("nama_produk") namaProduk: RequestBody,
        @Part("deskripsi_produk") deskripsiProduk: RequestBody,
        @Part("harga_produk") hargaProduk: RequestBody?, // TAMBAHAN
        @Part("nomor_telepon") nomorTelepon: RequestBody,
        @Part("link_facebook") linkFacebook: RequestBody?,
        @Part("link_instagram") linkInstagram: RequestBody?,
        @Part("link_tiktok") linkTiktok: RequestBody?,
        @Part fotoProduk: MultipartBody.Part?
    ): Response<SingleUmkmResponse>

    @DELETE("api/umkm/{id}")
    suspend fun deleteUmkm(
        @Header("Authorization") token: String,
        @Path("id") id: Int): Response<SingleUmkmResponse>

    // PUBLIC ENDPOINTS (tidak perlu authentication)
    @GET("api/umkm-public")
    suspend fun getPublicUmkm(
        @Query("kategori") kategori: String? = null,
        @Query("search") search: String? = null,
        @Query("limit") limit: Int = 12,
        @Query("page") page: Int = 1
    ): Response<UmkmPublicResponse>

    @GET("api/umkm-public/{id}")
    suspend fun getPublicUmkmById(@Path("id") id: Int): Response<SingleUmkmResponse>

    @GET("api/umkm-options")
    suspend fun getUmkmOptions(): Response<UmkmOptionsResponse>
}