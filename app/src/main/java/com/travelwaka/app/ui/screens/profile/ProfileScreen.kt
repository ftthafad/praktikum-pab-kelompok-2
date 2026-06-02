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
    val userName by viewModel.userName.collectAsState(initial = "")
    val userEmail by viewModel.userEmail.collectAsState(initial = "")
    val userRole by viewModel.userRole.collectAsState(initial = "user")
    var showLogoutDialog by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            icon = {
                Icon(
                    Icons.Filled.Logout,
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
                        Text(
                            text = if (!userName.isNullOrEmpty()) userName!!.first().toString() else "?",
                            style = MaterialTheme.typography.displaySmall,
                            color = Primary,
                            fontWeight = FontWeight.Bold
                        )
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

                ProfileMenuCard {
                    ProfileMenuItem(icon = Icons.Filled.Person, title = "Edit Profil", subtitle = "Ubah nama dan foto profil", onClick = {})
                    HorizontalDivider(color = DividerColor, modifier = Modifier.padding(horizontal = 16.dp))
                    ProfileMenuItem(icon = Icons.Filled.Notifications, title = "Notifikasi", subtitle = "Status pengajuan pengelola", onClick = onNotifikasi)
                    HorizontalDivider(color = DividerColor, modifier = Modifier.padding(horizontal = 16.dp))
                    ProfileMenuItem(icon = Icons.Filled.Lock, title = "Keamanan", subtitle = "Ubah password", onClick = {})
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
                        icon = Icons.Filled.Logout,
                        title = "Keluar",
                        subtitle = "Keluar dari akun",
                        onClick = { showLogoutDialog = true },
                        tintColor = ErrorColor
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
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