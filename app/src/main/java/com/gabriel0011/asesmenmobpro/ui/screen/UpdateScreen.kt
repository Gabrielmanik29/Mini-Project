package com.gabriel0011.asesmenmobpro.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.gabriel0011.asesmenmobpro.database.HistoryDb
import com.gabriel0011.asesmenmobpro.model.HistoryEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateScreen(navController: NavHostController, id: Long) {
    val context = LocalContext.current
    val db = HistoryDb.getInstance(context)
    val viewModel: HistoryViewModel = viewModel(factory = HistoryViewModel.factory(db.dao))

    // State untuk form edit
    var inputBerat by rememberSaveable { mutableStateOf("") }
    var inputRepetisi by rememberSaveable { mutableStateOf("") }
    var namaLatihan by rememberSaveable { mutableStateOf("") }
    var tanggalLama by remember { mutableStateOf("") }

    LaunchedEffect(id) {
        viewModel.getHistory(id) { history ->
            history?.let {
                inputBerat = it.berat
                inputRepetisi = it.repetisi
                namaLatihan = it.namaLatihan
                tanggalLama = it.tanggal
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Catatan") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)) {

            OutlinedTextField(
                value = inputBerat,
                onValueChange = { inputBerat = it },
                label = { Text("Berat Beban (Kg)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = inputRepetisi,
                onValueChange = { inputRepetisi = it },
                label = { Text("Repetisi") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    val berat = inputBerat.toDoubleOrNull() ?: 0.0
                    val reps = inputRepetisi.toDoubleOrNull() ?: 0.0
                    val hasilBaru = berat * (1 + (reps / 30))

                    val updatedEntity = HistoryEntity(
                        id = id,
                        namaLatihan = namaLatihan,
                        berat = inputBerat,
                        repetisi = inputRepetisi,
                        hasil1RM = "%.2f".format(hasilBaru),
                        tanggal = tanggalLama // Tetap gunakan tanggal asli
                    )
                    viewModel.updateHistory(updatedEntity)
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Simpan Perubahan")
            }
        }
    }
}