package com.localclasstech.layanandesa
import com.localclasstech.layanandesa.feature.keluhan.data.Keluhan
import com.localclasstech.layanandesa.feature.keluhan.data.KeluhanRequest
import com.localclasstech.layanandesa.feature.keluhan.data.network.KeluhanApiService
import com.localclasstech.layanandesa.feature.layanan.data.network.data.BaseResponse

import com.localclasstech.layanandesa.network.RetrofitClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlinx.coroutines.runBlocking
import retrofit2.Response

class KeluhanApiServiceTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var keluhanApiService: KeluhanApiService

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        // Retrofit client pointing to MockWebServer URL
        keluhanApiService = RetrofitClient.KeluhanApiService
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun testGetKeluhans() = runBlocking {
        val mockResponse = MockResponse()
            .setBody("{\n" +
                    "   \"success\": true,\n" +
                    "   \"message\": \"Data keluhan berhasil diambil\",\n" +
                    "   \"data\": [\n" +
                    "       {\n" +
                    "           \"id\": 1,\n" +
                    "           \"judul\": \"Keluhan A\",\n" +
                    "           \"isi\": \"Keluhan tentang A\",\n" +
                    "           \"status\": \"pending\",\n" +
                    "           \"user_id\": 2,\n" +
                    "           \"created_at\": \"2025-05-22T06:15:14.000000Z\",\n" +
                    "           \"updated_at\": \"2025-05-22T06:16:11.000000Z\"\n" +
                    "       }\n" +
                    "   ]\n" +
                    "}")
            .setResponseCode(200)  // Status code
        mockWebServer.enqueue(mockResponse)

        val response: Response<BaseResponse<List<Keluhan>>> =
            keluhanApiService.getKeluhans("Bearer token_here") //Type mismatch: inferred type is Response<com.localclasstech.layanandesa.network.BaseResponse<List<Keluhan>>> but Response<com.localclasstech.layanandesa.feature.layanan.data.network.data.BaseResponse<List<Keluhan>>> was expected

        assert(response.isSuccessful)
        assert(response.body()?.success == true)
        assert(response.body()?.data?.isNotEmpty() == true)
    }

    @Test
    fun testCreateKeluhan() = runBlocking {
        val keluhanRequest = KeluhanRequest(judul = "Judul Keluhan Baru", isi = "Isi keluhan baru")

        val mockResponse = MockResponse()
            .setBody("{\n" +
                    "   \"success\": true,\n" +
                    "   \"message\": \"Keluhan berhasil dibuat\",\n" +
                    "   \"data\": {\n" +
                    "       \"id\": 2,\n" +
                    "       \"judul\": \"Judul Keluhan Baru\",\n" +
                    "       \"isi\": \"Isi keluhan baru\",\n" +
                    "       \"status\": \"pending\",\n" +
                    "       \"user_id\": 2,\n" +
                    "       \"created_at\": \"2025-05-22T06:15:14.000000Z\",\n" +
                    "       \"updated_at\": \"2025-05-22T06:16:11.000000Z\"\n" +
                    "   }\n" +
                    "}")
            .setResponseCode(201)
        mockWebServer.enqueue(mockResponse)

        val response: Response<BaseResponse<Keluhan>> =
            keluhanApiService.createKeluhan("Bearer token_here", keluhanRequest) //Type mismatch: inferred type is Response<com.localclasstech.layanandesa.network.BaseResponse<Keluhan>> but Response<com.localclasstech.layanandesa.feature.layanan.data.network.data.BaseResponse<Keluhan>> was expected

        assert(response.isSuccessful)
        assert(response.body()?.success == true)
        assert(response.body()?.data?.judul == "Judul Keluhan Baru")
    }

}