package com.gabriel0011.asesmenmobpro.ui.screen

import androidx.lifecycle.ViewModel
import com.gabriel0011.asesmenmobpro.model.HistoryEntity

class HistoryViewModel : ViewModel() {
    val data = listOf(
        HistoryEntity(1, "Bench Press", "60", "8", "76.00", "2026-05-10 10:00:00"),
        HistoryEntity(2, "Squat", "80", "5", "93.33", "2026-05-09 16:30:00"),
        HistoryEntity(3, "Deadlift", "100", "3", "110.00", "2026-05-08 08:15:00")
    )
}