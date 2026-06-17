package com.gabriel0011.asesmenmobpro.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
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
    val viewModel: HistoryViewModel = viewModel(factory = HistoryViewModel.factory(db.dao()))

    var inputBerat by rememberSaveable { mutableStateOf("") }
    var inputRepetisi by rememberSaveable { mutableStateOf("") }
    var namaLatihan by rememberSaveable { mutableStateOf("") }
    var tanggalLama by remember { mutableStateOf("") }
    var satuanLama by remember { mutableStateOf("") }
    var imageUrlLama by remember { mutableStateOf("") }
    var userEmailLama by remember { mutableStateOf("") }

    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(id) {
        viewModel.getHistory(id) { history ->
            history?.let {
                inputBerat = it.berat
                inputRepetisi = it.repetisi
                namaLatihan = it.namaLatihan
                tanggalLama = it.tanggal
                satuanLama = it.satuan
                imageUrlLama = it.imageUrl ?: ""
                userEmailLama = it.userEmail
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus Riwayat") },
            text = { Text("Apakah kamu yakin ingin menghapus catatan latihan ini?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteHistory(id)
                    showDeleteDialog = false
                    navController.popBackStack()
                }) {
                    Text("Hapus", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Catatan", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        showDeleteDialog = true
                    }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Hapus",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            OutlinedTextField(
                value = inputBerat,
                onValueChange = { inputBerat = it },
                label = { Text("Berat Beban (Kg)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                )
            )

            OutlinedTextField(
                value = inputRepetisi,
                onValueChange = { inputRepetisi = it },
                label = { Text("Repetisi") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                )
            )

            Button(
                onClick = {
                    if (inputBerat.isBlank() || inputRepetisi.isBlank()) {
                        Toast.makeText(context, "Kolom tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val berat = inputBerat.toDoubleOrNull()
                    val reps = inputRepetisi.toDoubleOrNull()

                    if (berat == null || reps == null || berat <= 0 || reps <= 0) {
                        Toast.makeText(context, "Input harus berupa angka valid", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val hasilBaru = berat * (1 + (reps / 30))

                    val updatedEntity = HistoryEntity(
                        id = id,
                        namaLatihan = namaLatihan,
                        berat = inputBerat,
                        repetisi = inputRepetisi,
                        hasil1RM = "%.2f".format(hasilBaru),
                        tanggal = tanggalLama,
                        satuan = satuanLama,
                        imageUrl = imageUrlLama,
                        userEmail = userEmailLama
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