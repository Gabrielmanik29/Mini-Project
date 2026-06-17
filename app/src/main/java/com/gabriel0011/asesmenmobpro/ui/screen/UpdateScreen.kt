package com.gabriel0011.asesmenmobpro.ui.screen

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
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

    var newImageUri by remember { mutableStateOf<Uri?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    var isProcessing by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) newImageUri = uri
    }

    val calculated1RM by remember(inputBerat, inputRepetisi) {
        derivedStateOf {
            val berat = inputBerat.toDoubleOrNull() ?: 0.0
            val reps = inputRepetisi.toDoubleOrNull() ?: 0.0
            if (berat > 0 && reps > 0) berat * (1 + (reps / 30)) else 0.0
        }
    }
    LaunchedEffect(viewModel.status) {
        if (isProcessing && viewModel.status == ApiStatus.SUCCESS) {
            navController.popBackStack()
        } else if (isProcessing && viewModel.status == ApiStatus.FAILED) {
            Toast.makeText(context, viewModel.errorMessage, Toast.LENGTH_SHORT).show()
            isProcessing = false
        }
    }

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
                    isProcessing = true
                    viewModel.deleteHistory(id)
                    showDeleteDialog = false
                }) {
                    Text("Hapus", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Batal") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Catatan", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Hapus", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
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

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.LightGray)
                    .clickable {
                        photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                contentAlignment = Alignment.Center
            ) {
                val imageToDisplay = newImageUri ?: imageUrlLama
                if (imageToDisplay.toString().isNotEmpty()) {
                    AsyncImage(
                        model = imageToDisplay,
                        contentDescription = "Foto Latihan",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(50))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.7f))
                        .padding(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Ganti Foto", tint = Color.White)
                }
            }

            OutlinedTextField(
                value = inputBerat,
                onValueChange = { inputBerat = it },
                label = { Text("Berat Beban (Kg)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next)
            )

            OutlinedTextField(
                value = inputRepetisi,
                onValueChange = { inputRepetisi = it },
                label = { Text("Repetisi") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done)
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Estimasi 1RM Baru", style = MaterialTheme.typography.labelLarge)
                    Text(
                        text = "${"%.2f".format(calculated1RM)} $satuanLama",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Button(
                onClick = {
                    if (inputBerat.isBlank() || inputRepetisi.isBlank()) {
                        Toast.makeText(context, "Kolom tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    isProcessing = true
                    val updatedEntity = HistoryEntity(
                        id = id,
                        namaLatihan = namaLatihan,
                        berat = inputBerat,
                        repetisi = inputRepetisi,
                        hasil1RM = "%.2f".format(calculated1RM),
                        tanggal = tanggalLama,
                        satuan = satuanLama,
                        imageUrl = imageUrlLama,
                        userEmail = userEmailLama
                    )

                    viewModel.updateHistoryWithImage(context, newImageUri, updatedEntity)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isProcessing
            ) {
                Text(if (isProcessing) "Menyimpan..." else "Simpan Perubahan")
            }
        }
    }
}