package com.gabriel0011.asesmenmobpro.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Insert
    fun insertHistory(history: HistoryEntity): Long

    @Query("SELECT * FROM history ORDER BY id DESC")
    fun getAllHistory(): Flow<List<HistoryEntity>>

    @Query("DELETE FROM history WHERE id = :id")
    fun deleteHistoryById(id: Long): Int

    @Update
    fun updateHistory(history: HistoryEntity): Int

    @Query("SELECT * FROM history WHERE id = :id")
    fun getHistoryById(id: Long): HistoryEntity?
}