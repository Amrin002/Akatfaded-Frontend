package com.localclasstech.layanandesa.network

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private const val BASE_URL = "http://192.168.56.1:8000/"
//    private lateinit var BASE_URL: String

     // Ganti sesuai IP atau hosting kamu
    private val client = OkHttpClient.Builder()
         .addInterceptor(logging)
         .build()

    val clientService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(ApiService::class.java)
    }
}
