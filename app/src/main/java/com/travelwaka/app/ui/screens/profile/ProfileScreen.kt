package com.travelwaka.app.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.travelwaka.app.ui.components.BottomNavBar
import com.travelwaka.app.ui.theme.*
import com.travelwaka.app.viewmodel.AuthViewModel
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    currentRoute: NavKey?,
    onNavigate: (NavKey) -> Unit,
    onAjukanPengelola: () -> Unit,
    onKelolWisata: () -> Unit,
    onNotifikasi: () -> Unit,
    onLogout: () -> Unit
) {
    val viewModel: AuthViewModel = hiltViewModel()

    // Ambil data dari DataStore
    val token by viewModel.token.collectAsState(initial = null)
    val userName by viewModel.userName.collectAsState(initial = "")
    val userEmail by viewModel.userEmail.collectAsState(initial = "")
    val userRole by viewModel.userRole.collectAsState(initial = "user")
    val currentUser by viewModel.currentUser.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(token) {
        if (!token.isNullOrEmpty()) {
            viewModel.loadUserProfile()
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            icon = {
                Icon(
                    Icons.AutoMirrored.Filled.Logout,
                    contentDescription = null,
                    tint = ErrorColor
                )
            },
            title = {
                Text(
                    "Keluar dari Akun?",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            },
            text = {
                Text(
                    "Kamu akan keluar dari akun ini. Yakin ingin melanjutkan?",
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        viewModel.logout { onLogout() }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorColor),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Keluar", color = White)
                }
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

    Scaffold(
        bottomBar = { BottomNavBar(currentRoute, onNavigate) },
        containerColor = Background
    ) { paddingValues ->
        if (token.isNullOrEmpty()) {
            ProfileGuestContent(
                paddingValues = paddingValues,
                onLoginClick = { onLogout() }
            )
        } else {
            ProfileUserContent(
                paddingValues = paddingValues,
                userName = userName,
                userEmail = userEmail,
                userRole = userRole,
                currentUser = currentUser,
                onAjukanPengelola = onAjukanPengelola,
                onKelolWisata = onKelolWisata,
                onNotifikasi = onNotifikasi,
                onLogoutClick = { showLogoutDialog = true }
            )
        }
    }
}

@Composable
fun ProfileGuestContent(
    paddingValues: PaddingValues,
    onLoginClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(Primary.copy(alpha = 0.12f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = Primary
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Masuk ke TravelWaka",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Masuk atau buat akun untuk mengakses profil lengkap Anda, mengelola wisata, mengirim pengajuan pengelola, dan melihat notifikasi terbaru.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onLoginClick,
            colors = ButtonDefaults.buttonColors(containerColor = Primary),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(
                text = "Masuk / Daftar Akun",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = White
            )
        }
    }
}

@Composable
fun ProfileUserContent(
    paddingValues: PaddingValues,
    userName: String?,
    userEmail: String?,
    userRole: String?,
    currentUser: com.travelwaka.app.network.model.User?,
    onAjukanPengelola: () -> Unit,
    onKelolWisata: () -> Unit,
    onNotifikasi: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Primary)
                .padding(vertical = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .background(PrimaryLight, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (currentUser?.avatar != null && currentUser.avatar.isNotEmpty()) {
                        AsyncImage(
                            model = currentUser.avatar,
                            contentDescription = "Avatar",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize().clip(CircleShape)
                        )
                    } else {
                        Text(
                            text = if (!userName.isNullOrEmpty()) userName.first().toString() else "?",
                            style = MaterialTheme.typography.displaySmall,
                            color = Primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = userName ?: "-",
                    style = MaterialTheme.typography.headlineSmall,
                    color = White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = userEmail ?: "-",
                    style = MaterialTheme.typography.bodyMedium,
                    color = PrimaryLight
                )
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = PrimaryLight.copy(alpha = 0.3f)
                ) {
                    Text(
                        text = when (userRole) {
                            "pengelola" -> "Pengelola Wisata"
                            "super_admin" -> "Super Admin"
                            else -> "Wisatawan"
                        },
                        style = MaterialTheme.typography.labelMedium,
                        color = White,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Statistics Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                count = currentUser?.bookmarksCount ?: 0,
                label = "Bookmark",
                icon = Icons.Filled.Bookmark
            )
            StatCard(
                modifier = Modifier.weight(1f),
                count = currentUser?.reviewsCount ?: 0,
                label = "Ulasan",
                icon = Icons.Filled.Star
            )
            if (userRole == "pengelola") {
                StatCard(
                    modifier = Modifier.weight(1f),
                    count = currentUser?.wisataCount ?: 0,
                    label = "Wisata Saya",
                    icon = Icons.Filled.Map
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Akun",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            var showAboutDialog by remember { mutableStateOf(false) }
            val context = LocalContext.current

            if (showAboutDialog) {
                AlertDialog(
                    onDismissRequest = { showAboutDialog = false },
                    icon = {
                        Icon(
                            Icons.Filled.Info,
                            contentDescription = null,
                            tint = Primary
                        )
                    },
                    title = {
                        Text(
                            "Tentang TravelWaka",
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    },
                    text = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "TravelWaka",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Primary
                            )
                            Text(
                                "Jelajahi keindahan Jawa Tengah",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "Aplikasi ini dibuat sebagai proyek praktikum PAB Kelompok 2 untuk memudahkan wisatawan menjelajahi destinasi terbaik di Jawa Tengah.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextPrimary,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(color = DividerColor)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                "Versi 1.0.0 (Beta)",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "© 2026 Kelompok 2 PAB. All Rights Reserved.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = { showAboutDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = Primary),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Tutup", color = White)
                        }
                    },
                    containerColor = White,
                    shape = RoundedCornerShape(16.dp)
                )
            }

            ProfileMenuCard {
                ProfileMenuItem(
                    icon = Icons.Filled.Notifications, 
                    title = "Notifikasi", 
                    subtitle = "Status pengajuan pengelola", 
                    onClick = onNotifikasi
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Dukungan & Tentang",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            ProfileMenuCard {
                ProfileMenuItem(
                    icon = Icons.AutoMirrored.Filled.Help, 
                    title = "Hubungi Bantuan", 
                    subtitle = "Chat admin via WhatsApp", 
                    onClick = {
                        val waUrl = "https://wa.me/6289522932718?text=Halo%20Admin%20TravelWaka,%20saya%20butuh%20bantuan."
                        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(waUrl))
                        context.startActivity(intent)
                    }
                )
                HorizontalDivider(color = DividerColor, modifier = Modifier.padding(horizontal = 16.dp))
                ProfileMenuItem(
                    icon = Icons.Filled.Info, 
                    title = "Tentang Aplikasi", 
                    subtitle = "Informasi versi dan pengembang", 
                    onClick = { showAboutDialog = true }
                )
            }

            if (userRole == "user") {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Pengelola",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                ProfileMenuCard {
                    ProfileMenuItem(
                        icon = Icons.Filled.Store,
                        title = "Ajukan Jadi Pengelola",
                        subtitle = "Daftarkan destinasi wisatamu",
                        onClick = onAjukanPengelola,
                        tintColor = PrimaryMedium
                    )
                }
            }

            if (userRole == "pengelola") {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Pengelola Wisata",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                ProfileMenuCard {
                    ProfileMenuItem(
                        icon = Icons.Filled.Map,
                        title = "Kelola Wisata",
                        subtitle = "Tambah, edit, hapus wisata milikmu",
                        onClick = onKelolWisata,
                        tintColor = Primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ErrorColor.copy(alpha = 0.08f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                ProfileMenuItem(
                    icon = Icons.AutoMirrored.Filled.Logout,
                    title = "Keluar",
                    subtitle = "Keluar dari akun",
                    onClick = onLogoutClick,
                    tintColor = ErrorColor
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ProfileMenuCard(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        content()
    }
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    tintColor: androidx.compose.ui.graphics.Color = Primary
) {
    Surface(
        onClick = onClick,
        color = androidx.compose.ui.graphics.Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(tintColor.copy(alpha = 0.12f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = tintColor, modifier = Modifier.size(22.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = TextSecondary)
        }
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    count: Int,
    label: String,
    icon: ImageVector
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(Primary.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    TravelWakaTheme {
        ProfileScreen(
            currentRoute = null,
            onNavigate = {},
            onAjukanPengelola = {},
            onKelolWisata = {},
            onNotifikasi = {},
            onLogout = {}
        )
    }
}