package com.gabriel0011.asesmenmobpro.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.gabriel0011.asesmenmobpro.model.HistoryDao
import com.gabriel0011.asesmenmobpro.model.HistoryEntity

@Database(entities = [HistoryEntity::class], version = 2, exportSchema = false)
abstract class HistoryDb : RoomDatabase() {

    abstract val dao: HistoryDao

    companion object {
        @Volatile
        private var INSTANCE: HistoryDb? = null

        fun getInstance(context: Context): HistoryDb {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    HistoryDb::class.java,
                    "history.db"
                ).fallbackToDestructiveMigration().build()
            }
        }

    }

}