package com.localclasstech.layanandesa.feature.apbdes.data.repository

import com.localclasstech.layanandesa.feature.apbdes.data.network.ApbdesApiService
import com.localclasstech.layanandesa.feature.apbdes.data.network.ApbdesItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ApbdesRepository(private val apiService: ApbdesApiService) {

    suspend fun getAllApbdes(): Result<List<ApbdesItem>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getAllApbdes()
                if (response.isSuccessful && response.body()?.status == true) {
                    Result.success(response.body()?.data ?: emptyList())
                } else {
                    Result.failure(Exception("Failed to fetch APBDes data: ${response.body()?.message}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getApbdesById(id: Int): Result<ApbdesItem> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getApbdesById(id)
                if (response.isSuccessful && response.body()?.status == true) {
                    response.body()?.data?.let { data ->
                        Result.success(data)
                    } ?: Result.failure(Exception("No data found"))
                } else {
                    Result.failure(Exception("Failed to fetch APBDes detail: ${response.body()?.message}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}