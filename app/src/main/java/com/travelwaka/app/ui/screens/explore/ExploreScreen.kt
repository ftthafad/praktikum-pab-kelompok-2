package com.travelwaka.app.ui.screens.explore

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import androidx.lifecycle.viewmodel.compose.viewModel
import com.travelwaka.app.ui.components.*
import com.travelwaka.app.ui.theme.*
import com.travelwaka.app.viewmodel.ExploreViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    currentRoute: NavKey?,
    onNavigate: (NavKey) -> Unit,
    onWisataClick: (String) -> Unit,
    onBack: () -> Unit
) {
    // ✏️ DIUBAH: ganti WisataViewModel → ExploreViewModel
    val viewModel = viewModel<ExploreViewModel>()

    // ✏️ DIUBAH: semua state dari VM, tidak ada remember lokal
    val filteredList by viewModel.filteredList.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategoryId by viewModel.selectedCategoryId.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // ✅ DIHAPUS: dua LaunchedEffect & var searchQuery, selectedCategory, selectedCategoryName

    Scaffold(
        bottomBar = { BottomNavBar(currentRoute, onNavigate) },
        containerColor = Background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Top bar
            Surface(
                color = Primary,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = White)
                        }
                        Text(
                            text = "Jelajahi Wisata",
                            style = MaterialTheme.typography.headlineSmall,
                            color = White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    OutlinedTextField(
                        // ✏️ DIUBAH: value & onValueChange dari VM
                        value = searchQuery,
                        onValueChange = { viewModel.onSearchQueryChange(it) },
                        placeholder = { Text("Cari destinasi wisata...", color = White.copy(alpha = 0.7f)) },
                        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = White) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = White,
                            unfocusedBorderColor = White.copy(alpha = 0.5f),
                            focusedTextColor = White,
                            unfocusedTextColor = White,
                            cursorColor = White
                        ),
                        singleLine = true
                    )
                }
            }

            // Category Filter
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        // ✏️ DIUBAH: cek dari selectedCategoryId VM
                        selected = selectedCategoryId == null,
                        onClick = { viewModel.selectCategory(null) },
                        label = {
                            Text(
                                "Semua",
                                style = MaterialTheme.typography.labelMedium,
                                color = if (selectedCategoryId == null) White else Primary
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Primary,
                            containerColor = Background
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selectedCategoryId == null,
                            borderColor = Primary,
                            selectedBorderColor = Primary
                        )
                    )
                }
                items(categories) { category ->
                    FilterChip(
                        // ✏️ DIUBAH: selectedCategoryName sudah tidak perlu
                        selected = selectedCategoryId == category.id,
                        onClick = { viewModel.selectCategory(category.id) },
                        label = {
                            Text(
                                category.name,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (selectedCategoryId == category.id) White else Primary
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Primary,
                            containerColor = Background
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selectedCategoryId == category.id,
                            borderColor = Primary,
                            selectedBorderColor = Primary
                        )
                    )
                }
            }

            // Loading state
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Primary)
                }
            } else {
                // Result count
                Text(
                    text = "${filteredList.size} destinasi ditemukan",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                )

                // List hasil
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredList) { wisata ->
                        WisataListCard(
                            wisata = wisata,
                            onClick = { onWisataClick(wisata.id.toString()) }
                        )
                    }
                    if (filteredList.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 64.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("😕", style = MaterialTheme.typography.displayMedium)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        "Wisata tidak ditemukan",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ExploreScreenPreview() {
    TravelWakaTheme {
        ExploreScreen(
            currentRoute = null,
            onNavigate = {},
            onWisataClick = {},
            onBack = {}
        )
    }
}