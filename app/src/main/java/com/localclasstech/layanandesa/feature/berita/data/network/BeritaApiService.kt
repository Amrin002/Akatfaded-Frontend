package com.localclasstech.layanandesa.feature.berita.data.network

import com.localclasstech.layanandesa.feature.berita.data.BeritaResponse
import com.localclasstech.layanandesa.feature.berita.data.DetailBeritaResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface BeritaApiService {
    @GET("api/berita")
    fun getBerita(): Call<BeritaResponse>

    @GET("api/berita/{id}")
    fun getDetailBerita(@Path("id") id: Int): Call<DetailBeritaResponse>
}