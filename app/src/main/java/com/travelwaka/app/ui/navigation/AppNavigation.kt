package com.travelwaka.app.ui.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import androidx.hilt.navigation.compose.hiltViewModel
import com.travelwaka.app.viewmodel.NavigationViewModel
import com.travelwaka.app.ui.screens.onboarding.OnboardingScreen
import com.travelwaka.app.ui.screens.auth.LoginScreen
import com.travelwaka.app.ui.screens.auth.RegisterScreen
import com.travelwaka.app.ui.screens.home.HomeScreen
import com.travelwaka.app.ui.screens.explore.ExploreScreen
import com.travelwaka.app.ui.screens.detail.DetailWisataScreen
import com.travelwaka.app.ui.screens.detail.ReviewScreen
import com.travelwaka.app.ui.screens.bookmark.BookmarkScreen
import com.travelwaka.app.ui.screens.profile.ProfileScreen
import com.travelwaka.app.ui.screens.profile.FormPengajuanScreen
import com.travelwaka.app.ui.screens.profile.NotifikasiScreen
import com.travelwaka.app.ui.screens.pengelola.DaftarWisataSayaScreen
import com.travelwaka.app.ui.screens.pengelola.FormWisataScreen
import com.travelwaka.app.ui.screens.superadmin.DashboardApprovalScreen
import kotlinx.serialization.Serializable

// --- Navigation Keys ---

@Serializable
data object Onboarding : NavKey

@Serializable
data object Login : NavKey

@Serializable
data object Register : NavKey

@Serializable
data object Home : NavKey

@Serializable
data object Explore : NavKey

@Serializable
data class DetailWisata(val wisataId: String) : NavKey

@Serializable
data class Review(val wisataId: String) : NavKey

@Serializable
data object Bookmark : NavKey

@Serializable
data object Profile : NavKey

@Serializable
data object FormPengajuan : NavKey

@Serializable
data object Notifikasi : NavKey

@Serializable
data object DaftarWisataSaya : NavKey

@Serializable
data class FormWisata(val wisataId: String = "") : NavKey

@Serializable
data object DashboardApproval : NavKey

// --- AppNavigation ---

@Composable
fun AppNavigation(
    viewModel: NavigationViewModel = hiltViewModel()
) {
    val hasSeenOnboarding by viewModel.hasSeenOnboarding.collectAsState()
    val token by viewModel.token.collectAsState()
    val userRole by viewModel.userRole.collectAsState()

    var startDestination by remember { mutableStateOf<NavKey?>(null) }

    LaunchedEffect(hasSeenOnboarding, token, userRole) {
        val t = token
        if (t != null && startDestination == null) {
            startDestination = when {
                !hasSeenOnboarding -> Onboarding
                t.isNotEmpty() -> when (userRole) {
                    "super_admin" -> DashboardApproval
                    else -> Home
                }
                else -> Login
            }
        }
    }

    if (startDestination == null) return

    // Back stack as a SnapshotStateList
    val backStack = remember { mutableStateListOf<NavKey>(startDestination!!) }

    // Helper to get the current route (top of the stack)
    val currentRoute = backStack.lastOrNull()

    // Navigation helper functions
    fun navigateTo(route: NavKey) {
        backStack.add(route)
    }

    fun navigateAndClearTo(route: NavKey) {
        backStack.clear()
        backStack.add(route)
    }

    fun goBack() {
        if (backStack.size > 1) {
            backStack.removeLastOrNull()
        }
    }

    // Switch bottom nav tabs: clear stack and go to the tab
    fun navigateToTab(route: NavKey) {
        if (currentRoute != route) {
            backStack.clear()
            backStack.add(route)
        }
    }

    val entryProvider = entryProvider<NavKey> {
        entry<Onboarding> {
            OnboardingScreen(
                onFinish = {
                    navigateAndClearTo(Login)
                }
            )
        }

        entry<Login> {
            LoginScreen(
                onLoginSuccess = { role ->
                    when (role) {
                        "super_admin" -> navigateAndClearTo(DashboardApproval)
                        else -> navigateAndClearTo(Home)
                    }
                },
                onNavigateToRegister = { navigateTo(Register) },
                onSkip = {
                    navigateAndClearTo(Home)
                }
            )
        }

        entry<Register> {
            RegisterScreen(
                onRegisterSuccess = {
                    navigateAndClearTo(Home)
                },
                onNavigateToLogin = { goBack() }
            )
        }

        entry<Home> {
            HomeScreen(
                currentRoute = currentRoute,
                onNavigate = { navigateToTab(it) },
                onWisataClick = { id -> navigateTo(DetailWisata(wisataId = id)) },
                onSearchClick = { navigateTo(Explore) },
                onNotifikasi = { navigateTo(Notifikasi) }
            )
        }

        entry<Explore> {
            ExploreScreen(
                currentRoute = currentRoute,
                onNavigate = { navigateToTab(it) },
                onWisataClick = { id -> navigateTo(DetailWisata(wisataId = id)) },
                onBack = { goBack() }
            )
        }

        entry<DetailWisata> { key ->
            DetailWisataScreen(
                wisataId = key.wisataId,
                onBack = { goBack() },
                onWriteReview = { navigateTo(Review(wisataId = key.wisataId)) },
                onNavigateToLogin = { navigateAndClearTo(Login) }
            )
        }

        entry<Review> { key ->
            ReviewScreen(
                wisataId = key.wisataId,
                onBack = { goBack() },
                onSubmit = { goBack() }
            )
        }

        entry<Bookmark> {
            BookmarkScreen(
                currentRoute = currentRoute,
                onNavigate = { navigateToTab(it) },
                onWisataClick = { id -> navigateTo(DetailWisata(wisataId = id)) }
            )
        }

        entry<Profile> {
            ProfileScreen(
                currentRoute = currentRoute,
                onNavigate = { navigateToTab(it) },
                onAjukanPengelola = { navigateTo(FormPengajuan) },
                onKelolWisata = { navigateTo(DaftarWisataSaya) },
                onNotifikasi = { navigateTo(Notifikasi) },
                onLogout = {
                    navigateAndClearTo(Login)
                }
            )
        }

        entry<FormPengajuan> {
            FormPengajuanScreen(
                onBack = { goBack() },
                onSubmit = { goBack() }
            )
        }

        entry<Notifikasi> {
            NotifikasiScreen(
                onBack = { goBack() },
                onAjukanPengelola = { navigateTo(FormPengajuan) }
            )
        }

        entry<DaftarWisataSaya> {
            DaftarWisataSayaScreen(
                onBack = { goBack() },
                onTambahWisata = { navigateTo(FormWisata()) },
                onEditWisata = { id -> navigateTo(FormWisata(wisataId = id)) }
            )
        }

        entry<FormWisata> { key ->
            FormWisataScreen(
                wisataId = key.wisataId,
                onBack = { goBack() },
                onSave = { goBack() }
            )
        }

        entry<DashboardApproval> {
            DashboardApprovalScreen(
                onLogout = { navigateAndClearTo(Login) }
            )
        }
    }

    NavDisplay(
        backStack = backStack,
        entryProvider = entryProvider,
        onBack = { goBack() }
    )
}