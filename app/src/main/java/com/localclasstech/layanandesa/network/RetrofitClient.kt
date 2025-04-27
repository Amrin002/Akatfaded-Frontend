package com.localclasstech.layanandesa.network

import com.localclasstech.layanandesa.feature.layanan.data.network.apiservice.SuratApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private const val BASE_URL = "http://192.168.56.1:8000/"
//    private lateinit var BASE_URL: String

     // Ganti sesuai IP atau hosting kamu
    private val client = OkHttpClient.Builder()
         .addInterceptor(logging)
         .connectTimeout(40, TimeUnit.SECONDS) // ⏱️ waktu koneksi ke server
         .readTimeout(40, TimeUnit.SECONDS)    // ⏱️ waktu tunggu response
         .writeTimeout(40, TimeUnit.SECONDS)   // ⏱️ waktu kirim data ke server
         .build()

    val clientService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(ApiService::class.java)
    }

    val suratApiService: SuratApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addConverterFactory(ScalarsConverterFactory.create())
            .client(client)
            .build()
            .create(SuratApiService::class.java)
    }
}
