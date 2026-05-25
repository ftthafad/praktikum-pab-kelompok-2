package com.travelwaka.app.ui.screens.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.travelwaka.app.datastore.TokenDataStore
import com.travelwaka.app.ui.components.RatingBar
import com.travelwaka.app.ui.theme.*
import com.travelwaka.app.viewmodel.ReviewViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreen(
    wisataId: String,
    onBack: () -> Unit,
    onSubmit: () -> Unit,
    viewModel: ReviewViewModel = remember { ReviewViewModel() }
) {
    val context = LocalContext.current
    val tokenDataStore = remember { TokenDataStore.getInstance(context) }
    val token by tokenDataStore.token.collectAsState(initial = null)

    val myReview by viewModel.myReview.collectAsState()
    val isSubmitting by viewModel.isSubmitting.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()

    // Pre-fill dari review yang sudah ada (jika user pernah review)
    var rating by remember { mutableStateOf(0) }
    var reviewText by remember { mutableStateOf("") }

    // Cek review existing saat screen dibuka
    LaunchedEffect(token) {
        token?.let { viewModel.checkMyReview(it, wisataId.toInt()) }
    }

    // Pre-fill form kalau sudah pernah review
    LaunchedEffect(myReview) {
        myReview?.let {
            rating = it.rating
            reviewText = it.comment ?: ""
        }
    }

    // Navigasi balik setelah sukses submit
    LaunchedEffect(successMessage) {
        if (successMessage != null) {
            onSubmit()
            viewModel.clearMessages()
        }
    }

    val ratingLabel = when (rating) {
        1 -> "Sangat Buruk"
        2 -> "Buruk"
        3 -> "Cukup"
        4 -> "Bagus"
        5 -> "Luar Biasa!"
        else -> "Pilih rating"
    }

    val isEditMode = myReview != null
    val snackbarHostState = remember { SnackbarHostState() }

    // Tampilkan error via snackbar
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isEditMode) "Edit Ulasan" else "Tulis Ulasan",
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
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Bagaimana pengalamanmu?",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Berikan rating dan ulasanmu untuk membantu wisatawan lain",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(28.dp))

                    RatingBar(
                        rating = rating,
                        starSize = 48.dp,
                        onRatingChanged = { rating = it }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = ratingLabel,
                        style = MaterialTheme.typography.titleMedium,
                        color = if (rating > 0) StarColor else TextSecondary,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                    Divider(color = DividerColor)
                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = reviewText,
                        onValueChange = { if (it.length <= 500) reviewText = it },
                        label = { Text("Tulis ulasanmu...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            focusedLabelColor = Primary,
                            cursorColor = Primary
                        ),
                        maxLines = 6,
                        enabled = !isSubmitting
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${reviewText.length}/500",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = {
                    token?.let {
                        viewModel.submitReview(
                            token = it,
                            wisataId = wisataId.toInt(),
                            rating = rating,
                            comment = reviewText.ifBlank { null },
                            onSuccess = {} // navigasi di-handle via LaunchedEffect successMessage
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                enabled = rating > 0 && !isSubmitting
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        if (isEditMode) "Simpan Perubahan" else "Kirim Ulasan",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = onBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Primary),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Primary),
                enabled = !isSubmitting
            ) {
                Text("Batal", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReviewScreenPreview() {
    TravelWakaTheme {
        ReviewScreen(wisataId = "1", onBack = {}, onSubmit = {})
    }
}