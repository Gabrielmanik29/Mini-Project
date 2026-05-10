package com.gabriel0011.asesmenmobpro.ui.screen

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.gabriel0011.asesmenmobpro.R
import com.gabriel0011.asesmenmobpro.navigation.Screen
import com.gabriel0011.asesmenmobpro.ui.theme.Mobpro1Theme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.app_name))
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                ),
                actions = {
                    IconButton(onClick = {
                        navController.navigate(Screen.About.route)
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = stringResource(R.string.tentang_aplikasi),
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        CalculatorScreen(
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun CalculatorScreen(modifier: Modifier = Modifier) {
    var inputBerat by rememberSaveable { mutableStateOf("") }
    var inputRepetisi by rememberSaveable { mutableDoubleStateOf(1.0) }
    var isKg by rememberSaveable { mutableStateOf(true) }
    var hasil1RM by rememberSaveable { mutableDoubleStateOf(0.0) }
    var isHasilKg by rememberSaveable { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Image(
            painter = painterResource(id = R.mipmap.logo_gym),
            contentDescription = stringResource(R.string.logo_gym),
            modifier = Modifier.size(100.dp)
        )
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        // 1. Pilihan Satuan
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(stringResource(R.string.pilih_satuan))
            Spacer(modifier = Modifier.width(8.dp))
            RadioButton(selected = isKg, onClick = { isKg = true })
            Text("Kg")
            Spacer(modifier = Modifier.width(16.dp))
            RadioButton(selected = !isKg, onClick = { isKg = false })
            Text("Lbs")
        }

        // 2. Input Berat Beban
        OutlinedTextField(
            value = inputBerat,
            onValueChange = { inputBerat = it },
            label = { Text(stringResource(R.string.berat_beban)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            isError = isError,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                Text(
                    text = if (isKg) "Kg" else "Lbs",
                    modifier = Modifier.padding(end = 16.dp),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        )

        // 3. Input Repetisi menggunakan Slider
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Jumlah Repetisi: ${inputRepetisi.toInt()}",
                    style = MaterialTheme.typography.titleMedium
                )
                Slider(
                    value = inputRepetisi.toFloat(),
                    onValueChange = { inputRepetisi = it.toDouble() },
                    valueRange = 1f..15f,
                    steps = 13
                )
            }
        }


        Button(
            onClick = {
                focusManager.clearFocus()

                val berat = inputBerat.toDoubleOrNull()
                val repetisi = inputRepetisi


                if (berat != null && berat > 0) {
                    isError = false
                    hasil1RM = berat * (1 + (repetisi / 30))
                    isHasilKg = isKg
                } else {
                    isError = true
                    hasil1RM = 0.0
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(stringResource(R.string.hitung_1rm))
        }


        if (isError) {
            Text(
                text = stringResource(R.string.input_invalid),
                color = MaterialTheme.colorScheme.error
            )
        }


        if (hasil1RM > 0) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = stringResource(R.string.perkiraan_1rm), style = MaterialTheme.typography.titleMedium)

                    val hasilKg = if (isHasilKg) hasil1RM else hasil1RM / 2.20462
                    val hasilLbs = if (isHasilKg) hasil1RM * 2.20462 else hasil1RM

                    Text(
                        text = "%.2f Kg".format(hasilKg),
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Text(
                        text = "atau %.2f Lbs".format(hasilLbs),
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val satuanLabel = if (isHasilKg) "Kg" else "Lbs"
            val shareMessage = stringResource(
                id = R.string.bagikan_template,
                inputBerat,
                satuanLabel,
                inputRepetisi.toInt().toString(),
                hasil1RM
            )

            Button(
                onClick = {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, shareMessage)
                    }
                    context.startActivity(
                        Intent.createChooser(shareIntent, "Bagikan via...")
                    )
                },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text(text = stringResource(id = R.string.bagikan_aplikasi))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    Mobpro1Theme {
        MainScreen(rememberNavController())
    }
}