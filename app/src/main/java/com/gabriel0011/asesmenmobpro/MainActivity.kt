package com.gabriel0011.asesmenmobpro // Sesuaikan dengan package project kamu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.gabriel0011.asesmenmobpro.navigation.SetupNavGraph
import com.gabriel0011.asesmenmobpro.ui.theme.Mobpro1Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Mobpro1Theme {
                // Memanggil MainScreen sebagai tampilan utama [cite: 341, 349]
                SetupNavGraph()
            }
        }
    }
}