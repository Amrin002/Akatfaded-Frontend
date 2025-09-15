package com.localclasstech.layanandesa.feature.version.data

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface VersionApiService {
    @POST("api/app-version/check")
    suspend fun checkVersion(@Body request: VersionCheckRequest): Response<VersionCheckResponse>

    @GET("api/app-version/latest")
    suspend fun getLatestVersion(@Query("platform") platform: String = "android"): Response<VersionCheckResponse>

}