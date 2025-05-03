package com.localclasstech.layanandesa.feature.berita.data.repository

import android.content.Context
import com.localclasstech.layanandesa.feature.berita.data.Berita
import com.localclasstech.layanandesa.feature.berita.data.BeritaResponse
import com.localclasstech.layanandesa.feature.berita.data.DetailBeritaResponse
import com.localclasstech.layanandesa.feature.berita.data.network.BeritaApiService

class BeritaRepository(private val apiService: BeritaApiService, private val context: Context) {

    fun getAllBerita(callback: (List<Berita>?) -> Unit) {
        apiService.getBerita().enqueue(object : retrofit2.Callback<BeritaResponse> {
            override fun onResponse(
                call: retrofit2.Call<BeritaResponse>,
                response: retrofit2.Response<BeritaResponse>
            ) {
                if (response.isSuccessful) {
                    callback(response.body()?.data)
                } else {
                    callback(null)
                }
            }

            override fun onFailure(call: retrofit2.Call<BeritaResponse>, t: Throwable) {
                callback(null)
            }
        })
    }

    fun getDetailBerita(id: Int, callback: (Berita?) -> Unit) {
        apiService.getDetailBerita(id).enqueue(object : retrofit2.Callback<DetailBeritaResponse> {
            override fun onResponse(
                call: retrofit2.Call<DetailBeritaResponse>,
                response: retrofit2.Response<DetailBeritaResponse>
            ) {
                if (response.isSuccessful) {
                    callback(response.body()?.data)
                } else {
                    callback(null)
                }
            }

            override fun onFailure(call: retrofit2.Call<DetailBeritaResponse>, t: Throwable) {
                callback(null)
            }
        })
    }
}
