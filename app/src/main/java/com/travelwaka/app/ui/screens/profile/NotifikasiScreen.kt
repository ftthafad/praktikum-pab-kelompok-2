package com.travelwaka.app.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.travelwaka.app.datastore.TokenDataStore
import com.travelwaka.app.network.model.Pengajuan
import com.travelwaka.app.ui.theme.*
import com.travelwaka.app.viewmodel.PengajuanViewModel
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotifikasiScreen(
    onBack: () -> Unit,
    viewModel: PengajuanViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val tokenDataStore = remember { TokenDataStore.getInstance(context) }
    val token by tokenDataStore.token.collectAsState(initial = null)

    val pengajuanStatus by viewModel.pengajuanStatus.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Load status pengajuan saat screen dibuka
    LaunchedEffect(token) {
        token?.let { viewModel.getPengajuanStatus(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Notifikasi",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Primary,
                    titleContentColor = White,
                    navigationIconContentColor = White
                )
            )
        },
        containerColor = Background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Primary
                    )
                }

                pengajuanStatus == null -> {
                    // Belum pernah mengajukan
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🔔", style = MaterialTheme.typography.displayLarge)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Belum ada notifikasi",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Kamu belum pernah mengajukan\npendaftaran pengelola",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                }

                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Status Pengajuan Pengelola",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        PengajuanStatusCard(pengajuan = pengajuanStatus!!)
                    }
                }
            }
        }
    }
}

@Composable
fun PengajuanStatusCard(pengajuan: Pengajuan) {
    val statusColor = when (pengajuan.status) {
        "approved" -> SuccessColor
        "rejected" -> ErrorColor
        else -> PendingColor
    }
    val statusIcon: ImageVector = when (pengajuan.status) {
        "approved" -> Icons.Filled.CheckCircle
        "rejected" -> Icons.Filled.Cancel
        else -> Icons.Filled.HourglassEmpty
    }
    val statusLabel = when (pengajuan.status) {
        "approved" -> "Disetujui"
        "rejected" -> "Ditolak"
        else -> "Menunggu Review"
    }
    val statusMessage = when (pengajuan.status) {
        "approved" -> "Selamat! Pengajuanmu telah disetujui. Kamu sekarang bisa menambahkan destinasi wisata."
        "rejected" -> "Maaf, pengajuanmu ditolak. Kamu bisa mengajukan kembali melalui halaman Profil."
        else -> "Pengajuanmu sedang dalam proses peninjauan oleh admin. Mohon tunggu."
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            // Header status
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = statusIcon,
                    contentDescription = null,
                    tint = statusColor,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = statusLabel,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                    pengajuan.createdAt?.let {
                        Text(
                            text = it.take(10), // ambil tanggal saja: "2025-04-17"
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                }
            }

            Divider(modifier = Modifier.padding(vertical = 12.dp), color = DividerColor)

            // Info pengajuan
            LabelValue(label = "Nama Usaha", value = pengajuan.namaUsaha)
            Spacer(modifier = Modifier.height(8.dp))
            LabelValue(label = "Deskripsi", value = pengajuan.deskripsi)
            Spacer(modifier = Modifier.height(8.dp))
            LabelValue(label = "Alasan", value = pengajuan.alasan)

            // Catatan admin kalau rejected
            if (pengajuan.status == "rejected" && !pengajuan.catatanAdmin.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = ErrorColor.copy(alpha = 0.08f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Catatan Admin:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = ErrorColor
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = pengajuan.catatanAdmin,
                            style = MaterialTheme.typography.bodySmall,
                            color = ErrorColor
                        )
                    }
                }
            }

            Divider(modifier = Modifier.padding(vertical = 12.dp), color = DividerColor)

            // Pesan status
            Text(
                text = statusMessage,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun LabelValue(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = TextPrimary
        )
    }
}