package com.travelwaka.app.ui.screens.pengelola

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.travelwaka.app.datastore.TokenDataStore
import com.travelwaka.app.network.model.Photo
import com.travelwaka.app.ui.screens.profile.FormField
import com.travelwaka.app.ui.theme.*
import com.travelwaka.app.viewmodel.PengelolaWisataViewModel
import com.travelwaka.app.viewmodel.WisataViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormWisataScreen(
    wisataId: String,
    onBack: () -> Unit,
    onSave: () -> Unit,
    pengelolaViewModel: PengelolaWisataViewModel = hiltViewModel(),
    wisataViewModel: WisataViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val isEditMode = wisataId.isNotEmpty()

    // State dari VM
    val wisataDetail by pengelolaViewModel.wisataDetail.collectAsState()
    val isSaving by pengelolaViewModel.isSaving.collectAsState()
    val isUploading by pengelolaViewModel.isUploading.collectAsState()
    val errorMessage by pengelolaViewModel.errorMessage.collectAsState()
    val successMessage by pengelolaViewModel.successMessage.collectAsState()
    val categories by wisataViewModel.categories.collectAsState()

    // Form state
    var namaWisata by remember { mutableStateOf("") }
    var deskripsi by remember { mutableStateOf("") }
    var lokasi by remember { mutableStateOf("") }
    var hargaTiket by remember { mutableStateOf("") }
    var jamBuka by remember { mutableStateOf("") }
    var jamTutup by remember { mutableStateOf("") }
    var latitude by remember { mutableStateOf("") }
    var longitude by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf(0) }
    var selectedCategoryName by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var showMapPicker by remember { mutableStateOf(false) }

    var namaWisataError by remember { mutableStateOf<String?>(null) }
    var lokasiError by remember { mutableStateOf<String?>(null) }
    var categoryError by remember { mutableStateOf<String?>(null) }
    var hargaTiketError by remember { mutableStateOf<String?>(null) }

    fun validateFormLocal(): Boolean {
        var isValid = true
        if (namaWisata.isBlank()) {
            namaWisataError = "Nama wisata tidak boleh kosong"
            isValid = false
        } else {
            namaWisataError = null
        }

        if (lokasi.isBlank()) {
            lokasiError = "Lokasi tidak boleh kosong"
            isValid = false
        } else {
            lokasiError = null
        }

        if (selectedCategoryId == 0) {
            categoryError = "Kategori wisata harus dipilih"
            isValid = false
        } else {
            categoryError = null
        }

        if (hargaTiket.isBlank()) {
            hargaTiketError = "Harga tiket tidak boleh kosong"
            isValid = false
        } else {
            hargaTiketError = null
        }
        return isValid
    }

    // Foto yang sudah ada (mode edit)
    var existingPhotos by remember { mutableStateOf<List<Photo>>(emptyList()) }

    val snackbarHostState = remember { SnackbarHostState() }

    // Image picker
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            if (isEditMode) {
                val id = wisataId.toIntOrNull() ?: return@let
                pengelolaViewModel.uploadFoto(context, id, it) {
                    // Refresh detail wisata setelah upload
                    pengelolaViewModel.loadWisataForEdit(id)
                }
            } else {
                selectedImageUri = it
            }
        }
    }

    // Load data awal
    LaunchedEffect(Unit) {
        wisataViewModel.getCategories()
        if (isEditMode) {
            pengelolaViewModel.getWisataSaya()
        }
    }

    // Load data saat wisataList sudah terisi
    LaunchedEffect(isEditMode, wisataId) {
        if (isEditMode) {
            val id = wisataId.toIntOrNull() ?: return@LaunchedEffect
            pengelolaViewModel.loadWisataForEdit(id)
        }
    }

    // Pre-fill form saat data wisata tersedia (mode edit)
    LaunchedEffect(wisataDetail) {
        wisataDetail?.let { w ->
            namaWisata = w.name
            deskripsi = w.description
            lokasi = w.location
            hargaTiket = w.price
            val jam = w.opening_hours?.split("-") ?: listOf("", "")
            jamBuka = jam.getOrNull(0)?.trim() ?: ""
            jamTutup = jam.getOrNull(1)?.trim() ?: ""
            latitude = w.latitude?.toString() ?: ""
            longitude = w.longitude?.toString() ?: ""
            selectedCategoryId = w.category_id
            selectedCategoryName = w.category?.name ?: ""
            existingPhotos = w.photos ?: emptyList()
        }
    }

    // Navigasi setelah sukses simpan
    LaunchedEffect(successMessage) {
        successMessage?.let {
            snackbarHostState.showSnackbar(it)
            pengelolaViewModel.clearMessages()
            if (isEditMode) {
                // Hanya navigate back jika bukan sukses upload foto
                if (!it.contains("foto", ignoreCase = true)) {
                    onSave()
                }
            } else {
                // Dalam mode tambah baru, jika tidak ada foto, langsung kembali.
                // Jika ada foto, kita tunggu sampai foto ter-upload (pesan sukses mengandung "foto") baru kembali.
                if (selectedImageUri == null) {
                    onSave()
                } else if (it.contains("foto", ignoreCase = true)) {
                    onSave()
                }
            }
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            pengelolaViewModel.clearMessages()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isEditMode) "Edit Wisata" else "Tambah Wisata",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, enabled = !isSaving) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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

            // ── Foto Wisata ───────────────────────────────────────────────────
            Text(
                text = "Foto Wisata",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )

            if (isEditMode && existingPhotos.isNotEmpty()) {
                // Tampilkan foto yang sudah ada + tombol hapus
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(existingPhotos, key = { it.id }) { photo ->
                        Box(modifier = Modifier.size(100.dp)) {
                            AsyncImage(
                                model = photo.photo_url,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(10.dp))
                            )
                            // Badge cover
                            if (photo.is_cover == 1) {
                                Surface(
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(4.dp),
                                    shape = RoundedCornerShape(4.dp),
                                    color = Primary.copy(alpha = 0.85f)
                                ) {
                                    Text(
                                        "Cover",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = White,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            // Tombol hapus foto
                            IconButton(
                                onClick = {
                                    val id = wisataId.toIntOrNull()
                                    if (id != null) {
                                        pengelolaViewModel.deleteFoto(id, photo.id) {
                                            existingPhotos = existingPhotos.filter { it.id != photo.id }
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(24.dp)
                                    .background(ErrorColor, RoundedCornerShape(4.dp))
                            ) {
                                Icon(
                                    Icons.Filled.Close,
                                    contentDescription = "Hapus foto",
                                    tint = White,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                    // Tombol tambah foto baru
                    item {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(PrimaryLight.copy(alpha = 0.3f))
                                .border(2.dp, PrimaryMedium, RoundedCornerShape(10.dp))
                                .clickable { imagePickerLauncher.launch("image/*") },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isUploading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Primary,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        Icons.Filled.Add,
                                        contentDescription = null,
                                        tint = Primary,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Text(
                                        "Tambah",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Primary
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // Belum ada foto / mode tambah baru
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(PrimaryLight.copy(alpha = 0.3f))
                        .border(2.dp, PrimaryMedium, RoundedCornerShape(16.dp))
                        .clickable { imagePickerLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (isUploading) {
                        CircularProgressIndicator(color = Primary)
                    } else if (selectedImageUri != null) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            AsyncImage(
                                model = selectedImageUri,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            // Tombol hapus foto
                            IconButton(
                                onClick = { selectedImageUri = null },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(24.dp)
                                    .background(ErrorColor, RoundedCornerShape(4.dp))
                            ) {
                                Icon(
                                    Icons.Filled.Close,
                                    contentDescription = "Hapus foto",
                                    tint = White,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Filled.AddPhotoAlternate,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Tap untuk memilih foto sampul",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Primary,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                "JPG/PNG, maks. 2MB per foto",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }

            HorizontalDivider(color = DividerColor)

            // ── Informasi Wisata ──────────────────────────────────────────────
            Text(
                text = "Informasi Wisata",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )

            FormField(
                value = namaWisata,
                onValueChange = { 
                    namaWisata = it 
                    if (namaWisataError != null) namaWisataError = null
                },
                label = "Nama Wisata",
                icon = Icons.Filled.Landscape,
                enabled = !isSaving,
                isError = namaWisataError != null,
                errorText = namaWisataError
            )

            FormField(
                value = lokasi,
                onValueChange = { 
                    lokasi = it 
                    if (lokasiError != null) lokasiError = null
                },
                label = "Lokasi / Alamat",
                icon = Icons.Filled.LocationOn,
                enabled = !isSaving,
                isError = lokasiError != null,
                errorText = lokasiError
            )

            // Dropdown Kategori dari API
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { if (!isSaving) expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedCategoryName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Kategori") },
                    leadingIcon = {
                        Icon(Icons.Filled.Category, contentDescription = null, tint = if (categoryError != null) ErrorColor else Primary)
                    },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        focusedLabelColor = Primary
                    ),
                    enabled = !isSaving,
                    isError = categoryError != null,
                    supportingText = {
                        if (categoryError != null) {
                            Text(categoryError!!, color = ErrorColor)
                        }
                    }
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category.name) },
                            onClick = {
                                selectedCategoryId = category.id
                                selectedCategoryName = category.name
                                if (categoryError != null) categoryError = null
                                expanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = deskripsi,
                onValueChange = { deskripsi = it },
                label = { Text("Deskripsi") },
                leadingIcon = {
                    Icon(Icons.Filled.Description, contentDescription = null, tint = Primary)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    focusedLabelColor = Primary,
                    cursorColor = Primary
                ),
                maxLines = 4,
                enabled = !isSaving
            )

            FormField(
                value = hargaTiket,
                onValueChange = { 
                    hargaTiket = it 
                    if (hargaTiketError != null) hargaTiketError = null
                },
                label = "Harga Tiket (contoh: Rp 20.000 atau Gratis)",
                icon = Icons.Filled.ConfirmationNumber,
                enabled = !isSaving,
                isError = hargaTiketError != null,
                errorText = hargaTiketError
            )

            HorizontalDivider(color = DividerColor)

            // ── Jam Operasional ───────────────────────────────────────────────
            Text(
                text = "Jam Operasional",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = jamBuka,
                    onValueChange = { jamBuka = it },
                    label = { Text("Jam Buka") },
                    leadingIcon = {
                        Icon(Icons.Filled.AccessTime, contentDescription = null, tint = Primary)
                    },
                    placeholder = { Text("08.00") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        focusedLabelColor = Primary,
                        cursorColor = Primary
                    ),
                    singleLine = true,
                    enabled = !isSaving
                )
                OutlinedTextField(
                    value = jamTutup,
                    onValueChange = { jamTutup = it },
                    label = { Text("Jam Tutup") },
                    leadingIcon = {
                        Icon(Icons.Filled.AccessTime, contentDescription = null, tint = Primary)
                    },
                    placeholder = { Text("17.00") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        focusedLabelColor = Primary,
                        cursorColor = Primary
                    ),
                    singleLine = true,
                    enabled = !isSaving
                )
            }

            HorizontalDivider(color = DividerColor)

            // ── Koordinat ─────────────────────────────────────────────────────
            Text(
                text = "Koordinat (Opsional)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = latitude,
                    onValueChange = { latitude = it },
                    label = { Text("Latitude") },
                    leadingIcon = {
                        Icon(Icons.Filled.MyLocation, contentDescription = null, tint = Primary)
                    },
                    placeholder = { Text("-7.xxx") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        focusedLabelColor = Primary,
                        cursorColor = Primary
                    ),
                    singleLine = true,
                    enabled = !isSaving
                )
                OutlinedTextField(
                    value = longitude,
                    onValueChange = { longitude = it },
                    label = { Text("Longitude") },
                    leadingIcon = {
                        Icon(Icons.Filled.MyLocation, contentDescription = null, tint = Primary)
                    },
                    placeholder = { Text("110.xxx") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        focusedLabelColor = Primary,
                        cursorColor = Primary
                    ),
                    singleLine = true,
                    enabled = !isSaving
                )
            }

            OutlinedButton(
                onClick = { showMapPicker = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Primary),
                border = BorderStroke(1.dp, Primary)
            ) {
                Icon(Icons.Filled.Map, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Pilih Lokasi di Peta", fontWeight = FontWeight.Bold)
            }

            if (showMapPicker) {
                OsmMapPickerDialog(
                    initialLatitude = latitude.toDoubleOrNull(),
                    initialLongitude = longitude.toDoubleOrNull(),
                    onDismiss = { showMapPicker = false },
                    onLocationSelected = { lat, lng ->
                        latitude = lat.toString()
                        longitude = lng.toString()
                        showMapPicker = false
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ── Tombol Simpan ─────────────────────────────────────────────────
            Button(
                onClick = {
                    if (validateFormLocal()) {
                        val openingHours = if (jamBuka.isNotBlank() && jamTutup.isNotBlank())
                            "$jamBuka - $jamTutup" else jamBuka

                            if (isEditMode) {
                                pengelolaViewModel.updateWisata(
                                    wisataId = wisataId.toInt(),
                                    name = namaWisata,
                                    description = deskripsi,
                                    location = lokasi,
                                    latitude = latitude.toDoubleOrNull(),
                                    longitude = longitude.toDoubleOrNull(),
                                    price = hargaTiket,
                                    openingHours = openingHours,
                                    categoryId = selectedCategoryId,
                                    onSuccess = {}
                                )
                            } else {
                                pengelolaViewModel.tambahWisata(
                                    name = namaWisata,
                                    description = deskripsi,
                                    location = lokasi,
                                    latitude = latitude.toDoubleOrNull(),
                                    longitude = longitude.toDoubleOrNull(),
                                    price = hargaTiket,
                                    openingHours = openingHours,
                                    categoryId = selectedCategoryId,
                                    onSuccess = { newId ->
                                        selectedImageUri?.let { uri ->
                                            pengelolaViewModel.uploadFoto(context, newId, uri) {
                                                // Success callback
                                            }
                                        }
                                    }
                                )
                            }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                enabled = !isSaving
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = if (isEditMode) Icons.Filled.Save else Icons.Filled.Add,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        if (isEditMode) "Simpan Perubahan" else "Tambah Wisata",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun OsmMapPickerDialog(
    initialLatitude: Double?,
    initialLongitude: Double?,
    onDismiss: () -> Unit,
    onLocationSelected: (Double, Double) -> Unit
) {
    val context = LocalContext.current

    // Initialize OSM Configuration
    LaunchedEffect(Unit) {
        org.osmdroid.config.Configuration.getInstance().userAgentValue = context.packageName
    }

    val defaultLat = -7.7956 // Yogyakarta default
    val defaultLng = 110.3695

    val startLat = initialLatitude ?: defaultLat
    val startLng = initialLongitude ?: defaultLng

    var centerLat by remember { mutableStateOf(startLat) }
    var centerLng by remember { mutableStateOf(startLng) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Pilih Lokasi Wisata",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Geser peta untuk mengarahkan pin di tengah ke lokasi yang tepat.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, DividerColor, RoundedCornerShape(12.dp))
                ) {
                    // OSM Map
                    AndroidView(
                        factory = { ctx ->
                            org.osmdroid.views.MapView(ctx).apply {
                                setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK)
                                setMultiTouchControls(true)
                                controller.setZoom(16.5)
                                controller.setCenter(org.osmdroid.util.GeoPoint(startLat, startLng))

                                // Update center coordinates when map gestures complete / moves
                                addMapListener(object : org.osmdroid.events.MapListener {
                                    override fun onScroll(event: org.osmdroid.events.ScrollEvent?): Boolean {
                                        val center = mapCenter
                                        centerLat = center.latitude
                                        centerLng = center.longitude
                                        return true
                                    }

                                    override fun onZoom(event: org.osmdroid.events.ZoomEvent?): Boolean {
                                        val center = mapCenter
                                        centerLat = center.latitude
                                        centerLng = center.longitude
                                        return true
                                    }
                                })
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )

                    // Fixed Pin in Center of Box
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = "Pilih lokasi ini",
                        tint = ErrorColor,
                        modifier = Modifier
                            .size(40.dp)
                            .align(Alignment.Center)
                            .offset(y = (-20).dp) // Offset pin so the tip points to center
                    )
                }

                // Show current coordinates
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Lat: ${String.format("%.6f", centerLat)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                    Text(
                        text = "Lng: ${String.format("%.6f", centerLng)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onLocationSelected(centerLat, centerLng)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Text("Selesai")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal", color = TextSecondary)
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun FormWisataScreenPreview() {
    TravelWakaTheme {
        FormWisataScreen(wisataId = "", onBack = {}, onSave = {})
    }
}

@Preview(showBackground = true)
@Composable
fun FormWisataEditScreenPreview() {
    TravelWakaTheme {
        FormWisataScreen(wisataId = "1", onBack = {}, onSave = {})
    }
}