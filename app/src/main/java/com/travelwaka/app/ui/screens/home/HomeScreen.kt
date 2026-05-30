package com.travelwaka.app.ui.screens.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import coil.compose.AsyncImage
import com.travelwaka.app.ui.components.*
import com.travelwaka.app.ui.theme.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.travelwaka.app.viewmodel.HomeViewModel

val bannerImages = listOf(
    "https://images.unsplash.com/photo-1596402184320-417e7178b2cd?w=800",
    "https://images.unsplash.com/photo-1555400038-63f5ba517a47?w=800",
    "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=800"
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    currentRoute: NavKey?,
    onNavigate: (NavKey) -> Unit,
    onWisataClick: (String) -> Unit,
    onSearchClick: () -> Unit,
    onNotifikasi: () -> Unit
) {
    val viewModel = viewModel<HomeViewModel>()
    val wisataList by viewModel.wisataList.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val selectedCategoryId by viewModel.selectedCategoryId.collectAsState()

    val pagerState = rememberPagerState(pageCount = { bannerImages.size })



    Scaffold(
        bottomBar = { BottomNavBar(currentRoute, onNavigate) },
        containerColor = Background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            // Top Bar
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Primary)
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Travel Waka",
                            style = MaterialTheme.typography.headlineSmall,
                            color = White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Jelajahi Jawa Tengah",
                            style = MaterialTheme.typography.bodySmall,
                            color = PrimaryLight
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(onClick = onSearchClick) {
                            Icon(Icons.Filled.Search, contentDescription = "Search", tint = White)
                        }
                        IconButton(onClick = onNotifikasi ) {
                            Icon(Icons.Filled.Notifications, contentDescription = "Notifikasi", tint = White)
                        }
                    }
                }
            }

            // Banner Carousel
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                ) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        Box {
                            AsyncImage(
                                model = bannerImages[page],
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                androidx.compose.ui.graphics.Color.Transparent,
                                                Primary.copy(alpha = 0.6f)
                                            )
                                        )
                                    )
                            )
                            Text(
                                text = "Destinasi Wisata Unggulan",
                                style = MaterialTheme.typography.titleLarge,
                                color = White,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(16.dp)
                            )
                        }
                    }
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        repeat(bannerImages.size) { index ->
                            Box(
                                modifier = Modifier
                                    .size(if (pagerState.currentPage == index) 20.dp else 8.dp, 8.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (pagerState.currentPage == index) White else White.copy(alpha = 0.5f)
                                    )
                            )
                        }
                    }
                }
            }

            // Kategori
            item {
                Column(modifier = Modifier.padding(top = 20.dp)) {
                    Text(
                        text = "Kategori",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Chip "Semua"
                        item {
                            CategoryChip(
                                label = "Semua",
                                isSelected = selectedCategoryId == null,  // ✏️ fix di sini
                                onClick = { viewModel.selectCategory(null) }
                            )
                        }
                        items(categories) { category ->
                            CategoryChip(
                                label = category.name,
                                isSelected = selectedCategoryId == category.id,  // ✏️ fix di sini
                                onClick = { viewModel.selectCategory(category.id) }
                            )
                        }
                    }
                }
            }

            // Wisata Populer
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(top = 24.dp, bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Wisata Populer",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    TextButton(onClick = onSearchClick) {
                        Text("Lihat Semua", color = Primary)
                    }
                }
            }

            // Loading state
            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Primary)
                    }
                }
            } else {
                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(wisataList.take(5)) { wisata ->
                            WisataCard(
                                wisata = wisata,
                                onClick = { onWisataClick(wisata.id.toString()) }
                            )
                        }
                    }
                }

                // Semua Wisata
                item {
                    Text(
                        text = "Semua Wisata",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .padding(top = 24.dp, bottom = 12.dp)
                    )
                }

                items(wisataList) { wisata ->
                    WisataListCard(
                        wisata = wisata,
                        onClick = { onWisataClick(wisata.id.toString()) },
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryChip(
    label: String,
    isSelected: Boolean = false,
    onClick: () -> Unit = {}
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) Primary else PrimaryLight,
        onClick = onClick
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = if (isSelected) White else Primary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    TravelWakaTheme {
        HomeScreen(
            currentRoute = null,
            onNavigate = {},
            onWisataClick = {},
            onSearchClick = {},
            onNotifikasi = {}
        )
    }
}