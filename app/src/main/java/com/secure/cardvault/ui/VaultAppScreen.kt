package com.secure.cardvault.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.secure.cardvault.data.CardEntity
import com.secure.cardvault.utils.copyToClipboard

@Composable
fun VaultAppScreen(
    cards: List<CardEntity>,
    onSaveOrUpdateCard: (CardEntity) -> Unit,
    onDeleteCard: (CardEntity) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val listState = rememberLazyListState()

    var showCardDialog by remember { mutableStateOf(false) }
    var cardBeingEdited by remember { mutableStateOf<CardEntity?>(null) }

    Scaffold(
        containerColor = PitchBlack,
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    containerColor = CrispWhite,
                    contentColor = PitchBlack,
                    snackbarData = data
                )
            }
        },
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = CrispWhite)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                "CARDVAULT",
                                color = CrispWhite,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp,
                                letterSpacing = 2.sp
                            )
                            Text(
                                "RELEASE R8 OPTIMIZED • 12 METALLIC FINISHES",
                                color = MutedWhite,
                                fontSize = 9.sp,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PitchBlack)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    cardBeingEdited = null
                    showCardDialog = true
                },
                containerColor = CrispWhite,
                contentColor = PitchBlack,
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Card")
            }
        }
    ) { padding ->
        if (cards.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.CreditCard,
                        contentDescription = null,
                        tint = BorderGray,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No cards stored in offline vault", color = MutedWhite)
                }
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(22.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(
                    items = cards,
                    key = { it.id },
                    contentType = { "bank_card_3d" }
                ) { card ->
                    UltraLuxury3DBankCard(
                        card = card,
                        onCopy = { label, value ->
                            copyToClipboard(context, label, value)
                            scope.launch {
                                snackbarHostState.showSnackbar("$label copied to clipboard")
                            }
                        },
                        onEdit = {
                            cardBeingEdited = card
                            showCardDialog = true
                        },
                        onDelete = { onDeleteCard(card) }
                    )
                }
            }
        }

        if (showCardDialog) {
            CardFormDialog(
                existingCard = cardBeingEdited,
                onDismiss = {
                    showCardDialog = false
                    cardBeingEdited = null
                },
                onSave = { savedCard ->
                    onSaveOrUpdateCard(savedCard)
                    val msg = if (cardBeingEdited != null) "Card updated successfully" else "Card added to vault"
                    showCardDialog = false
                    cardBeingEdited = null
                    scope.launch {
                        snackbarHostState.showSnackbar(msg)
                    }
                }
            )
        }
    }
}
