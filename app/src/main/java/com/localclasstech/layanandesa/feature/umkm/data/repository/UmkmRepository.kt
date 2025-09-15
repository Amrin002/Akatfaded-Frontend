package com.localclasstech.layanandesa.feature.umkm.data.repository

import com.localclasstech.layanandesa.feature.umkm.data.CreateUmkmRequest
import com.localclasstech.layanandesa.feature.umkm.data.SingleUmkmResponse
import com.localclasstech.layanandesa.feature.umkm.data.UmkmOptionsResponse
import com.localclasstech.layanandesa.feature.umkm.data.UmkmPublicResponse
import com.localclasstech.layanandesa.feature.umkm.data.UmkmResponse
import com.localclasstech.layanandesa.feature.umkm.data.network.UmkmApiService
import com.localclasstech.layanandesa.settings.PreferencesHelper
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.io.File

class UmkmRepository(
    private val api: UmkmApiService,
    private val preferencesHelper: PreferencesHelper
) {

    private fun getBearerToken(): String {
        val token = preferencesHelper.getToken()
        return "Bearer $token"
    }

    // AUTHENTICATED ENDPOINTS
    suspend fun getMyUmkm(): Response<UmkmResponse> {
        return api.getMyUmkm()
    }

    suspend fun getUmkmById(id: Int): Response<SingleUmkmResponse> {
        return api.getUmkmById(id)
    }

    suspend fun createUmkm(request: CreateUmkmRequest): Response<SingleUmkmResponse> {
        return api.createUmkm(request)
    }

    suspend fun createUmkmWithImage(
        request: CreateUmkmRequest,
        imageFile: File?
    ): Response<SingleUmkmResponse> {
        return api.createUmkmWithImage(
            namaUsaha = request.nama_usaha.toRequestBody("text/plain".toMediaTypeOrNull()),
            kategori = request.kategori.toRequestBody("text/plain".toMediaTypeOrNull()),
            namaProduk = request.nama_produk.toRequestBody("text/plain".toMediaTypeOrNull()),
            deskripsiProduk = request.deskripsi_produk.toRequestBody("text/plain".toMediaTypeOrNull()),
            hargaProduk = request.harga_produk?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull()), // TAMBAHAN
            nomorTelepon = request.nomor_telepon.toRequestBody("text/plain".toMediaTypeOrNull()),
            linkFacebook = request.link_facebook?.toRequestBody("text/plain".toMediaTypeOrNull()),
            linkInstagram = request.link_instagram?.toRequestBody("text/plain".toMediaTypeOrNull()),
            linkTiktok = request.link_tiktok?.toRequestBody("text/plain".toMediaTypeOrNull()),
            fotoProduk = imageFile?.let {
                val requestFile = it.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("foto_produk", it.name, requestFile)
            }
        )
    }

    suspend fun updateUmkm(id: Int, request: CreateUmkmRequest): Response<SingleUmkmResponse> {
        return api.updateUmkm(id, request)
    }

    suspend fun updateUmkmWithImage(
        id: Int,
        request: CreateUmkmRequest,
        imageFile: File?
    ): Response<SingleUmkmResponse> {
        return api.updateUmkmWithImage(
            id = id,
            method = "PUT".toRequestBody("text/plain".toMediaTypeOrNull()),
            namaUsaha = request.nama_usaha.toRequestBody("text/plain".toMediaTypeOrNull()),
            kategori = request.kategori.toRequestBody("text/plain".toMediaTypeOrNull()),
            namaProduk = request.nama_produk.toRequestBody("text/plain".toMediaTypeOrNull()),
            deskripsiProduk = request.deskripsi_produk.toRequestBody("text/plain".toMediaTypeOrNull()),
            hargaProduk = request.harga_produk?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull()), // TAMBAHAN
            nomorTelepon = request.nomor_telepon.toRequestBody("text/plain".toMediaTypeOrNull()),
            linkFacebook = request.link_facebook?.toRequestBody("text/plain".toMediaTypeOrNull()),
            linkInstagram = request.link_instagram?.toRequestBody("text/plain".toMediaTypeOrNull()),
            linkTiktok = request.link_tiktok?.toRequestBody("text/plain".toMediaTypeOrNull()),
            fotoProduk = imageFile?.let {
                val requestFile = it.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("foto_produk", it.name, requestFile)
            }
        )
    }

    suspend fun deleteUmkm(id: Int): Response<SingleUmkmResponse> {
        return api.deleteUmkm(id)
    }

    // PUBLIC ENDPOINTS (tidak perlu authentication)
    suspend fun getPublicUmkm(
        kategori: String? = null,
        search: String? = null,
        limit: Int = 12,
        page: Int = 1
    ): Response<UmkmPublicResponse> {
        return api.getPublicUmkm(kategori, search, limit, page)
    }

    suspend fun getPublicUmkmById(id: Int): Response<SingleUmkmResponse> {
        return api.getPublicUmkmById(id)
    }

    suspend fun getUmkmOptions(): Response<UmkmOptionsResponse> {
        return api.getUmkmOptions()
    }
}