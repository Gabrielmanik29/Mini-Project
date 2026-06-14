package com.gabriel0011.asesmenmobpro.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val namaLatihan: String,
    val berat: String,
    val repetisi: String,
    val hasil1RM: String,
    val tanggal: String,
    val satuan: String,
    val userEmail: String = "",
    val imageUrl: String = ""
)