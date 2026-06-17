package com.gabriel0011.asesmenmobpro.network

import com.gabriel0011.asesmenmobpro.model.CloudinaryResponse
import com.gabriel0011.asesmenmobpro.model.HistoryEntity
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

data class ImgBbResponse(val data: ImgBbData?)
data class ImgBbData(val url: String)

interface ApiService {
    @GET("history")
    suspend fun getHistory(): List<HistoryEntity>

    @PUT("history/{id}")
    suspend fun updateHistory(
        @Path("id") id: Long,
        @Body history: HistoryEntity
    ): Response<HistoryEntity>

    @POST("history")
    suspend fun insertHistory(@Body history: HistoryEntity): Response<HistoryEntity>

    @DELETE("history/{id}")
    suspend fun deleteHistory(@Path("id") id: Long): Response<Unit>


    @Multipart
    @POST("https://api.cloudinary.com/v1_1/dsbnr4wql/image/upload")
    suspend fun uploadImageToCloudinary(
        @Part file: MultipartBody.Part,
        @Part("upload_preset") uploadPreset: RequestBody
    ): CloudinaryResponse
}