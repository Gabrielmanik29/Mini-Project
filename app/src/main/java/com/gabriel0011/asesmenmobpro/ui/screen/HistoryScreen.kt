package com.gabriel0011.asesmenmobpro.ui.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.gabriel0011.asesmenmobpro.R
import com.gabriel0011.asesmenmobpro.database.HistoryDb
import com.gabriel0011.asesmenmobpro.model.HistoryEntity
import com.gabriel0011.asesmenmobpro.model.User
import com.gabriel0011.asesmenmobpro.navigation.Screen
import com.gabriel0011.asesmenmobpro.network.UserDataStore
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(modifier: Modifier = Modifier, navController: NavHostController) {
    val context = LocalContext.current
    val db = HistoryDb.getInstance(context)
    val viewModel: HistoryViewModel = viewModel(factory = HistoryViewModel.factory(db.dao))
    val data by viewModel.data.collectAsState()

    val dataStore = remember { SettingsDataStore(context) }
    val isList by dataStore.isList.collectAsState(initial = true)
    val themeColor by dataStore.themeColor.collectAsState(initial = 0)
    val scope = rememberCoroutineScope()

    var showDialog by remember { mutableStateOf(false) }
    var selectedId by remember { mutableLongStateOf(0L) }

    val userDataStore = remember { UserDataStore(context) }
    val user by userDataStore.userFlow.collectAsState(initial = User())
    var showProfileDialog by remember { mutableStateOf(false) }
    val filteredData = data.filter { it.userEmail == user.email }

    LaunchedEffect(user.email) {
        if (user.email.isNotEmpty()) {
            viewModel.fetchData(user.email)
        }
    }
    if (viewModel.status == ApiStatus.FAILED) {
        Toast.makeText(context, viewModel.errorMessage, Toast.LENGTH_SHORT).show()
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Hapus Riwayat") },
            text = { Text("Apakah kamu yakin ingin menghapus catatan latihan ini?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteHistory(selectedId)
                    showDialog = false
                }) {
                    Text("Hapus", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Batal") }
            }
        )
    }

    if (showProfileDialog) {
        ProfilDialog(
            user = user,
            onDismiss = { showProfileDialog = false },
            onLogin = { newUser ->
                scope.launch { userDataStore.saveData(newUser) }
            },
            onLogout = {
                scope.launch { userDataStore.saveData(User()) } // Reset user menjadi kosong
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Riwayat GymMax") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                ),
                actions = {
                    IconButton(onClick = { showProfileDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Profil",
                            tint = Color.White
                        )
                    }
                    IconButton(onClick = {
                        scope.launch { dataStore.saveLayout(!isList) }
                    }) {
                        Icon(
                            painter = painterResource(
                                id = if (isList) R.drawable.baseline_grid_view_24 else R.drawable.baseline_view_list_24
                            ),
                            contentDescription = if (isList) "Tampilan Grid" else "Tampilan List",
                            tint = Color.White
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.Home.route) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                modifier = Modifier.padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Tambah",
                    modifier = Modifier.size(36.dp)
                )
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {

            Card(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Tema:", fontWeight = FontWeight.Bold)
                    ColorButton(color = Color(0xFF1976D2), isSelected = themeColor == 0) {
                        scope.launch { dataStore.saveThemeColor(0) }
                    }
                    ColorButton(color = Color(0xFFD32F2F), isSelected = themeColor == 1) {
                        scope.launch { dataStore.saveThemeColor(1) }
                    }
                    ColorButton(color = Color(0xFF388E3C), isSelected = themeColor == 2) {
                        scope.launch { dataStore.saveThemeColor(2) }
                    }
                }
            }

            if (viewModel.status == ApiStatus.LOADING) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (filteredData.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = if (user.email.isEmpty()) "Silakan login terlebih dahulu" else "Belum ada riwayat latihan")
                }
            } else {
                if (isList) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 8.dp, end = 8.dp, bottom = 84.dp)
                    ) {
                        items(filteredData) { history ->
                            HistoryItem(
                                history = history,
                                onDeleteClick = {
                                    selectedId = history.id
                                    showDialog = true
                                },
                                onItemClick = { navController.navigate(Screen.Update.withId(history.id)) }
                            )
                            HorizontalDivider()
                        }
                    }
                } else {
                    LazyVerticalStaggeredGrid(
                        columns = StaggeredGridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        verticalItemSpacing = 8.dp,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(8.dp, 8.dp, 8.dp, 84.dp)
                    ) {
                        items(data) { history ->
                            HistoryGridItem(
                                history = history,
                                onDeleteClick = {
                                    selectedId = history.id
                                    showDialog = true
                                },
                                onItemClick = { navController.navigate(Screen.Update.withId(history.id)) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ColorButton(color: Color, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(color)
            .clickable { onClick() }
            .border(
                width = if (isSelected) 3.dp else 0.dp,
                color = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                shape = CircleShape
            )
    )
}

@Composable
fun HistoryItem(history: HistoryEntity, onDeleteClick: () -> Unit, onItemClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { onItemClick() }) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "${history.namaLatihan} - 1RM: ${history.hasil1RM} ${history.satuan}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Text(text = "Beban: ${history.berat} ${history.satuan} | Reps: ${history.repetisi}")
                Text(text = history.tanggal, style = MaterialTheme.typography.bodySmall)
            }
            IconButton(onClick = onDeleteClick) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun HistoryGridItem(history: HistoryEntity, onDeleteClick: () -> Unit, onItemClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable { onItemClick() }) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = history.namaLatihan, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                IconButton(onClick = onDeleteClick, modifier = Modifier.size(24.dp)) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error)
                }
            }
            Text(text = "1RM: ${history.hasil1RM} ${history.satuan}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Text(text = "Beban: ${history.berat} ${history.satuan}\nReps: ${history.repetisi}", style = MaterialTheme.typography.bodyMedium)
            Text(text = history.tanggal, style = MaterialTheme.typography.bodySmall)
        }
    }
}