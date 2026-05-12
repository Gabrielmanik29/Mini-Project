package com.gabriel0011.asesmenmobpro.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.gabriel0011.asesmenmobpro.model.HistoryDao
import com.gabriel0011.asesmenmobpro.model.HistoryEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HistoryViewModel(private val dao: HistoryDao) : ViewModel() {
    val data: StateFlow<List<HistoryEntity>> = dao.getAllHistory().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = emptyList()
    )

    fun insertHistory(namaLatihan: String, berat: String, repetisi: String, hasil1RM: String, tanggal: String) {

        Thread {
            val newHistory = HistoryEntity(
                namaLatihan = namaLatihan,
                berat = berat,
                repetisi = repetisi,
                hasil1RM = hasil1RM,
                tanggal = tanggal
            )
            dao.insertHistory(newHistory)
        }.start()
    }

    companion object {
        fun factory(dao: HistoryDao) = viewModelFactory {
            initializer {
                HistoryViewModel(dao)
            }
        }
    }

    fun deleteHistory(id: Long) {
        Thread {
            dao.deleteHistoryById(id)
        }.start()
    }

    fun getHistory(id: Long, onResult: (HistoryEntity?) -> Unit) {
        Thread {
            val result = dao.getHistoryById(id)
            onResult(result)
        }.start()
    }

    fun updateHistory(entity: HistoryEntity) {
        Thread {
            dao.updateHistory(entity)
        }.start()
    }
}