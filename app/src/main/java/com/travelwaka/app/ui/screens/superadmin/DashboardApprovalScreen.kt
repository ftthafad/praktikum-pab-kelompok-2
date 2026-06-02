package com.travelwaka.app.ui.screens.superadmin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.travelwaka.app.datastore.TokenDataStore
import com.travelwaka.app.network.model.Pengajuan
import com.travelwaka.app.ui.theme.*
import com.travelwaka.app.viewmodel.AuthViewModel
import com.travelwaka.app.viewmodel.DashboardApprovalViewModel
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardApprovalScreen(
    onLogout: () -> Unit = {},
    viewModel: DashboardApprovalViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val authViewModel: AuthViewModel = hiltViewModel()
    val tokenDataStore = remember { TokenDataStore.getInstance(context) }
    val token by tokenDataStore.token.collectAsState(initial = null)

    val pengajuanList by viewModel.pengajuanList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val processingId by viewModel.processingId.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()

    var detailItem by remember { mutableStateOf<Pengajuan?>(null) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    // Dialog reject — menyimpan ID pengajuan yang akan ditolak
    var rejectTargetId by remember { mutableStateOf<Int?>(null) }
    var catatanReject by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(token) {
        token?.let { viewModel.getPengajuanList(it) }
    }

    // Dialog konfirmasi logout
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            icon = {
                Icon(Icons.Filled.Logout, contentDescription = null, tint = ErrorColor)
            },
            title = {
                Text("Keluar dari Akun?", fontWeight = FontWeight.Bold, color = TextPrimary)
            },
            text = {
                Text(
                    "Kamu akan keluar dari akun admin ini. Yakin ingin melanjutkan?",
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        authViewModel.logout { onLogout() }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorColor),
                    shape = RoundedCornerShape(8.dp)
                ) { Text("Keluar", color = White) }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Batal", color = TextSecondary)
                }
            },
            containerColor = White,
            shape = RoundedCornerShape(16.dp)
        )
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    LaunchedEffect(successMessage) {
        successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    // Dialog detail pengajuan
    detailItem?.let { item ->
        AlertDialog(
            onDismissRequest = { detailItem = null },
            title = {
                Text(
                    "Detail Pengajuan",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    DetailRow(label = "Nama", value = item.user?.name ?: "-")
                    DetailRow(label = "Email", value = item.user?.email ?: "-")
                    DetailRow(label = "Nama Usaha", value = item.namaUsaha)
                    DetailRow(label = "Deskripsi", value = item.deskripsi)
                    DetailRow(label = "Alasan", value = item.alasan)
                    DetailRow(label = "Tanggal", value = item.createdAt?.take(10) ?: "-")
                }
            },
            confirmButton = {
                TextButton(onClick = { detailItem = null }) { Text("Tutup") }
            }
        )
    }

    // Dialog input catatan reject
    rejectTargetId?.let { targetId ->
        AlertDialog(
            onDismissRequest = {
                rejectTargetId = null
                catatanReject = ""
            },
            icon = {
                Icon(
                    Icons.Filled.Cancel,
                    contentDescription = null,
                    tint = ErrorColor,
                    modifier = Modifier.size(40.dp)
                )
            },
            title = {
                Text(
                    "Tolak Pengajuan",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "Berikan catatan alasan penolakan untuk pemohon.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                    OutlinedTextField(
                        value = catatanReject,
                        onValueChange = { catatanReject = it },
                        label = { Text("Catatan penolakan") },
                        placeholder = { Text("Contoh: Data yang diberikan kurang lengkap") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ErrorColor,
                            focusedLabelColor = ErrorColor,
                            cursorColor = ErrorColor
                        ),
                        maxLines = 4
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        token?.let {
                            viewModel.rejectPengajuan(it, targetId, catatanReject)
                        }
                        rejectTargetId = null
                        catatanReject = ""
                    },
                    enabled = catatanReject.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorColor)
                ) { Text("Tolak") }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        rejectTargetId = null
                        catatanReject = ""
                    }
                ) { Text("Batal") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Dashboard Admin",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Approval Pengelola Wisata",
                            style = MaterialTheme.typography.bodySmall,
                            color = PrimaryLight
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { token?.let { viewModel.getPengajuanList(it) } }) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Refresh", tint = White)
                    }
                    IconButton(onClick = { showLogoutDialog = true }) {
                        Icon(Icons.Filled.Logout, contentDescription = "Logout", tint = White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Primary,
                    titleContentColor = White
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Stats Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Primary)
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    label = "Pending",
                    count = pengajuanList.size.toString(),
                    color = PendingColor,
                    modifier = Modifier.weight(1f)
                )
            }

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Primary)
                    }
                }

                pengajuanList.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("📋", style = MaterialTheme.typography.displayLarge)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Tidak ada pengajuan pending",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextSecondary
                            )
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(pengajuanList, key = { it.id }) { item ->
                            ApprovalCard(
                                item = item,
                                isProcessing = processingId == item.id,
                                onApprove = {
                                    token?.let { viewModel.approvePengajuan(it, item.id) }
                                },
                                onReject = {
                                    rejectTargetId = item.id
                                },
                                onDetail = { detailItem = item }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(
    label: String,
    count: String,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White.copy(alpha = 0.15f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = White
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = White.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun ApprovalCard(
    item: Pengajuan,
    isProcessing: Boolean,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    onDetail: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Avatar inisial
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (item.user?.name ?: "?").first().toString(),
                        style = MaterialTheme.typography.titleLarge,
                        color = White,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        item.user?.name ?: "-",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        item.user?.email ?: "-",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
                // Badge pending
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = PendingColor.copy(alpha = 0.12f)
                ) {
                    Text(
                        "Menunggu",
                        style = MaterialTheme.typography.labelSmall,
                        color = PendingColor,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = DividerColor)
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                item.namaUsaha,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Text(
                item.deskripsi,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                maxLines = 2
            )
            item.createdAt?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    it.take(10),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (isProcessing) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(28.dp),
                        color = Primary,
                        strokeWidth = 2.dp
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDetail,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Primary),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Primary)
                    ) {
                        Text("Detail", style = MaterialTheme.typography.labelLarge)
                    }
                    Button(
                        onClick = onReject,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ErrorColor)
                    ) {
                        Icon(
                            Icons.Filled.Close,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Tolak", style = MaterialTheme.typography.labelLarge)
                    }
                    Button(
                        onClick = onApprove,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessColor)
                    ) {
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Setuju", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Column {
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = TextSecondary,
            fontWeight = FontWeight.SemiBold
        )
        Text(value, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
        Spacer(modifier = Modifier.height(4.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardApprovalScreenPreview() {
    TravelWakaTheme {
        DashboardApprovalScreen()
    }
}