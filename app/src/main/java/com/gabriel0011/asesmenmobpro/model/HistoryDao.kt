package com.gabriel0011.asesmenmobpro.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Insert
    fun insertHistory(history: HistoryEntity): Long

    @Query("SELECT * FROM history ORDER BY id DESC")
    fun getAllHistory(): Flow<List<HistoryEntity>>

    @Query("DELETE FROM history WHERE id = :id")
    fun deleteHistoryById(id: Long): Int
}