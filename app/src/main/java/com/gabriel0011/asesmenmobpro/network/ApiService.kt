package com.gabriel0011.asesmenmobpro.network

import com.gabriel0011.asesmenmobpro.model.HistoryEntity
import okhttp3.MultipartBody
import retrofit2.http.*

data class ImgBbResponse(val data: ImgBbData)
data class ImgBbData(val url: String)

interface ApiService {
    @GET("history")
    suspend fun getHistory(): List<HistoryEntity>

    @POST("history")
    suspend fun insertHistory(@Body history: HistoryEntity): HistoryEntity

    @DELETE("history/{id}")
    suspend fun deleteHistory(@Path("id") id: Long): HistoryEntity

    @Multipart
    @POST("https://api.imgbb.com/1/upload")
    suspend fun uploadImage(
        @Query("key") key: String,
        @Part image: MultipartBody.Part
    ): ImgBbResponse
}