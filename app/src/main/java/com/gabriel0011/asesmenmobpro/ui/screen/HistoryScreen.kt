package com.gabriel0011.asesmenmobpro.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gabriel0011.asesmenmobpro.model.HistoryEntity
import com.gabriel0011.asesmenmobpro.ui.screen.HistoryViewModel
import androidx.navigation.NavHostController
import com.gabriel0011.asesmenmobpro.navigation.Screen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(modifier: Modifier = Modifier, navController: NavHostController) {
    val viewModel: HistoryViewModel = viewModel()
    val data = viewModel.data

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Riwayat GymMax") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(Screen.Home.route) }) {
                Text("+")
            }
        }
    ) { padding ->
        if (data.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Belum ada riwayat 1RM")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(bottom = 84.dp)
            ) {
                items(data) { history ->
                    HistoryItem(history)
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
fun HistoryItem(history: HistoryEntity) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "${history.namaLatihan} - 1RM: ${history.hasil1RM} Kg",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium
        )
        Text(text = "Beban: ${history.berat} Kg | Repetisi: ${history.repetisi}")
        Text(
            text = history.tanggal,
            style = MaterialTheme.typography.bodySmall
        )
    }
}