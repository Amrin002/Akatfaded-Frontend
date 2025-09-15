package com.localclasstech.layanandesa.network

import com.localclasstech.layanandesa.feature.apbdes.data.network.ApbdesApiService
import com.localclasstech.layanandesa.feature.berita.data.network.BeritaApiService
import com.localclasstech.layanandesa.feature.keluhan.data.network.KeluhanApiService
import com.localclasstech.layanandesa.feature.layanan.data.network.apiservice.SuratApiService
import com.localclasstech.layanandesa.feature.umkm.data.network.UmkmApiService
import com.localclasstech.layanandesa.feature.version.data.VersionApiService
import com.localclasstech.layanandesa.settings.utils.UrlConstant
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

    private val BASE_URL = UrlConstant.BASE_URL
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

    val beritaApiService: BeritaApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(BeritaApiService::class.java)
    }
    val keluhanApiService: KeluhanApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(KeluhanApiService::class.java)
    }
    val apbdesApiService: ApbdesApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(ApbdesApiService::class.java)
    }
    // Tambahkan VersionApiService
    val versionApiService: VersionApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(VersionApiService::class.java)
    }
    val umkmApiService: UmkmApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(UmkmApiService::class.java)
    }
}
