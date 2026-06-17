package com.gabriel0011.asesmenmobpro.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true)
    @Json(name = "id")
    val id: Long = 0L,
    @Json(name = "namaLatihan")
    val namaLatihan: String = "",
    @Json(name = "berat")
    val berat: String = "",
    @Json(name = "repetisi")
    val repetisi: String = "",
    @Json(name = "hasil1RM")
    val hasil1RM: String = "",
    @Json(name = "tanggal")
    val tanggal: String = "",
    @Json(name = "satuan")
    val satuan: String = "",
    @Json(name = "userEmail")
    val userEmail: String = "",
    @Json(name = "imageUrl")
    val imageUrl: String = ""
)