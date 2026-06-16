package com.travelwaka.app.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.travelwaka.app.datastore.TokenDataStore
import com.travelwaka.app.ui.theme.*
import com.travelwaka.app.viewmodel.PengajuanViewModel
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormPengajuanScreen(
    onBack: () -> Unit,
    onSubmit: () -> Unit,
    viewModel: PengajuanViewModel = hiltViewModel()
) {
    val isSubmitting by viewModel.isSubmitting.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()

    var namaUsaha by remember { mutableStateOf("") }
    var deskripsi by remember { mutableStateOf("") }
    var alasan by remember { mutableStateOf("") }
    var showSuccessDialog by remember { mutableStateOf(false) }

    var namaUsahaError by remember { mutableStateOf<String?>(null) }
    var deskripsiError by remember { mutableStateOf<String?>(null) }
    var alasanError by remember { mutableStateOf<String?>(null) }

    fun validateForm(): Boolean {
        var isValid = true

        if (namaUsaha.isBlank()) {
            namaUsahaError = "Nama usaha tidak boleh kosong"
            isValid = false
        } else if (namaUsaha.length < 3) {
            namaUsahaError = "Nama usaha minimal 3 karakter"
            isValid = false
        } else {
            namaUsahaError = null
        }

        if (deskripsi.isBlank()) {
            deskripsiError = "Deskripsi tidak boleh kosong"
            isValid = false
        } else if (deskripsi.length < 10) {
            deskripsiError = "Deskripsi minimal 10 karakter"
            isValid = false
        } else {
            deskripsiError = null
        }

        if (alasan.isBlank()) {
            alasanError = "Alasan tidak boleh kosong"
            isValid = false
        } else if (alasan.length < 10) {
            alasanError = "Alasan minimal 10 karakter"
            isValid = false
        } else {
            alasanError = null
        }

        return isValid
    }

    val snackbarHostState = remember { SnackbarHostState() }

    // Tampilkan error via snackbar
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    // Tampilkan dialog sukses setelah API berhasil
    LaunchedEffect(successMessage) {
        if (successMessage != null) {
            showSuccessDialog = true
            viewModel.clearMessages()
        }
    }

    // Dialog sukses
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {},
            icon = {
                Icon(
                    Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = SuccessColor,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    "Pengajuan Terkirim!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    "Pengajuan kamu sebagai pengelola wisata sedang diproses. Kami akan memberitahu kamu melalui notifikasi.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        onSubmit()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Text("OK")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Ajukan Jadi Pengelola",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, enabled = !isSubmitting) {
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
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Stepper / Progress Header
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Langkah 1 dari 3",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = Primary
                        )
                        Text(
                            text = "Mengisi Formulir",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = TextSecondary
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = 0.33f,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = Primary,
                        trackColor = DividerColor
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StepIndicator(step = "1", title = "Isi Form", active = true)
                        StepDivider(modifier = Modifier.weight(1f).align(Alignment.CenterVertically))
                        StepIndicator(step = "2", title = "Tinjau", active = false)
                        StepDivider(modifier = Modifier.weight(1f).align(Alignment.CenterVertically))
                        StepIndicator(step = "3", title = "Aktif", active = false)
                    }
                }
            }

            // Info banner
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = PrimaryLight.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        Icons.Filled.Info,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Pengajuan akan ditinjau oleh admin. Proses verifikasi membutuhkan 1-3 hari kerja.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Primary
                    )
                }
            }

            // Nama Usaha
            FormField(
                value = namaUsaha,
                onValueChange = { 
                    namaUsaha = it 
                    if (namaUsahaError != null) namaUsahaError = null
                },
                label = "Nama Usaha / Tempat Wisata",
                icon = Icons.Filled.Store,
                enabled = !isSubmitting,
                isError = namaUsahaError != null,
                errorText = namaUsahaError
            )

            // Deskripsi
            OutlinedTextField(
                value = deskripsi,
                onValueChange = { 
                    deskripsi = it 
                    if (deskripsiError != null) deskripsiError = null
                },
                label = { Text("Deskripsi Tempat Wisata") },
                leadingIcon = {
                    Icon(Icons.Filled.Description, contentDescription = null, tint = if (deskripsiError != null) ErrorColor else Primary)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    focusedLabelColor = Primary,
                    cursorColor = Primary
                ),
                maxLines = 4,
                enabled = !isSubmitting,
                isError = deskripsiError != null,
                supportingText = {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        if (deskripsiError != null) {
                            Text(deskripsiError!!, color = ErrorColor)
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                        Text("${deskripsi.length}/500", color = TextSecondary)
                    }
                }
            )

            // Alasan
            OutlinedTextField(
                value = alasan,
                onValueChange = { 
                    alasan = it 
                    if (alasanError != null) alasanError = null
                },
                label = { Text("Alasan Mengajukan") },
                leadingIcon = {
                    Icon(Icons.Filled.Edit, contentDescription = null, tint = if (alasanError != null) ErrorColor else Primary)
                },
                placeholder = { Text("Jelaskan mengapa kamu ingin menjadi pengelola wisata...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    focusedLabelColor = Primary,
                    cursorColor = Primary
                ),
                maxLines = 4,
                enabled = !isSubmitting,
                isError = alasanError != null,
                supportingText = {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        if (alasanError != null) {
                            Text(alasanError!!, color = ErrorColor)
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                        Text("${alasan.length}/500", color = TextSecondary)
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    if (validateForm()) {
                        viewModel.submitPengajuan(
                            namaUsaha = namaUsaha,
                            deskripsi = deskripsi,
                            alasan = alasan,
                            onSuccess = {} // handled via LaunchedEffect successMessage
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                enabled = !isSubmitting
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        "Kirim Pengajuan",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun StepIndicator(step: String, title: String, active: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(if (active) Primary else DividerColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = step,
                color = if (active) White else TextSecondary,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (active) FontWeight.Bold else FontWeight.Normal,
            color = if (active) TextPrimary else TextSecondary
        )
    }
}

@Composable
fun StepDivider(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .padding(horizontal = 8.dp)
            .height(2.dp)
            .background(DividerColor)
    )
}

@Composable
fun FormField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    enabled: Boolean = true,
    isError: Boolean = false,
    errorText: String? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, contentDescription = null, tint = if (isError) ErrorColor else Primary) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Primary,
            focusedLabelColor = Primary,
            cursorColor = Primary
        ),
        singleLine = true,
        enabled = enabled,
        isError = isError,
        supportingText = {
            if (errorText != null) {
                Text(errorText, color = ErrorColor)
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun FormPengajuanScreenPreview() {
    TravelWakaTheme {
        FormPengajuanScreen(onBack = {}, onSubmit = {})
    }
}