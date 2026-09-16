package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.AfriBabaRepository
import com.example.model.*
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun VendorDashboardScreen(
    currentCurrency: AppCurrency,
    onNavigateToProduct: (Product) -> Unit = {}
) {
    val products by AfriBabaRepository.products.collectAsState()
    val orders by AfriBabaRepository.orders.collectAsState()

    var showAddProductDialog by remember { mutableStateOf(false) }
    var showWithdrawDialog by remember { mutableStateOf(false) }
    var withdrawalSuccessMessage by remember { mutableStateOf<String?>(null) }

    // Calculated vendor figures
    val totalVolumeXof = orders.sumOf { it.totalAmountXof }
    val releasedAmountXof = orders.filter { it.escrowStatus == EscrowStatus.RELEASED_TO_VENDOR }.sumOf { it.totalAmountXof }
    val escrowPendingAmountXof = orders.filter { it.escrowStatus != EscrowStatus.RELEASED_TO_VENDOR }.sumOf { it.totalAmountXof }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp, bottom = 90.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Espace Grossiste & Fournisseur",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Gestion Mobile-First de votre commerce B2B",
                        fontSize = 11.5.sp,
                        color = MutedGray
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = EscrowGreenLight
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Verified, contentDescription = null, tint = EscrowGreen, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "RCCM Vérifié", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EscrowGreenDark)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
        }

        // Financial Metrics Cards
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSlate),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Solde Disponible au Retrait Instantané :",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                    Text(
                        text = currentCurrency.format(releasedAmountXof),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = SaffronGold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "Fonds en Séquestre :", fontSize = 10.sp, color = Color(0xFF94A3B8))
                            Text(
                                text = currentCurrency.format(escrowPendingAmountXof),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = EscrowGreen
                            )
                        }

                        Column {
                            Text(text = "Chiffre d'Affaires Total :", fontSize = 10.sp, color = Color(0xFF94A3B8))
                            Text(
                                text = currentCurrency.format(totalVolumeXof),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { showWithdrawDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = SaffronGold),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                            .testTag("vendor_withdraw_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBalanceWallet,
                            contentDescription = null,
                            tint = DarkSlate,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Retrait Mobile Money Direct (Wave / Orange / MTN)",
                            color = DarkSlate,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.5.sp
                        )
                    }
                }
            }

            withdrawalSuccessMessage?.let { msg ->
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = EscrowGreenLight,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = msg,
                        fontSize = 11.5.sp,
                        color = EscrowGreenDark,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
        }

        // Action Row: Add Product to Catalog
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Mon Catalogue Produits (${products.size})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                Button(
                    onClick = { showAddProductDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = TerracottaPrimary),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("add_product_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Ajouter Produit", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Product Catalog Items
        items(products) { product ->
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, CardBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { onNavigateToProduct(product) }
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFE2E8F0)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = product.originCountryFlag, fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = product.title,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Stock : ${product.inStockQty} pcs • Prix : ${currentCurrency.format(product.tiers.first().unitPriceXof)}",
                            fontSize = 10.5.sp,
                            color = CharcoalGray
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFFF1F5F9)
                    ) {
                        Text(
                            text = "En Vente",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = EscrowGreenDark,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }

    // Modal: Retrait Mobile Money
    if (showWithdrawDialog) {
        var withdrawAmount by remember { mutableStateOf("250000") }
        var withdrawPhone by remember { mutableStateOf("+221 77 123 45 67") }
        var selectedWithdrawOperator by remember { mutableStateOf(PaymentProvider.WAVE) }
        var isSubmittingWithdrawal by remember { mutableStateOf(false) }
        val scope = rememberCoroutineScope()

        Dialog(onDismissRequest = { if (!isSubmittingWithdrawal) showWithdrawDialog = false }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Retrait Vers Mobile Money",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Virement instantané de vos gains séquestre débloqués",
                        fontSize = 11.sp,
                        color = MutedGray
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(text = "Opérateur de réception :", fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(PaymentProvider.WAVE, PaymentProvider.ORANGE_MONEY, PaymentProvider.MTN_MOMO).forEach { prov ->
                            val isSelected = selectedWithdrawOperator == prov
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, if (isSelected) Color(prov.brandColorHex) else CardBorder),
                                color = if (isSelected) Color(prov.brandColorHex).copy(alpha = 0.12f) else Color.White,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedWithdrawOperator = prov }
                            ) {
                                Text(
                                    text = prov.operatorName,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DarkSlate,
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(text = "Montant à retirer (FCFA) :", fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold)
                    OutlinedTextField(
                        value = withdrawAmount,
                        onValueChange = { withdrawAmount = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(text = "Numéro Mobile Money du titulaire :", fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold)
                    OutlinedTextField(
                        value = withdrawPhone,
                        onValueChange = { withdrawPhone = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            isSubmittingWithdrawal = true
                            scope.launch {
                                delay(1200)
                                isSubmittingWithdrawal = false
                                showWithdrawDialog = false
                                withdrawalSuccessMessage = "Virement de $withdrawAmount FCFA envoyé avec succès vers $withdrawPhone (${selectedWithdrawOperator.operatorName}) !"
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EscrowGreen),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("confirm_withdrawal_button")
                    ) {
                        if (isSubmittingWithdrawal) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp))
                        } else {
                            Text(text = "Confirmer le Virement Instantané", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }
    }

    // Modal: Add Product to Catalog
    if (showAddProductDialog) {
        var newTitle by remember { mutableStateOf("") }
        var newCategory by remember { mutableStateOf("Tissus & Textiles") }
        var newPrice by remember { mutableStateOf("15000") }
        var newMinOrder by remember { mutableStateOf("10") }
        var newStock by remember { mutableStateOf("500") }
        var newCountry by remember { mutableStateOf("Côte d'Ivoire") }
        var newPhone by remember { mutableStateOf("+225 07 08 09 10 11") }

        Dialog(onDismissRequest = { showAddProductDialog = false }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(18.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "Ajout Rapide de Produit Grossiste",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(text = "Titre du lot / produit :", fontSize = 11.5.sp)
                    OutlinedTextField(
                        value = newTitle,
                        onValueChange = { newTitle = it },
                        placeholder = { Text("Ex: Sacs de riz parfumé 50kg") },
                        modifier = Modifier.fillMaxWidth().testTag("new_product_title"),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(text = "Catégorie :", fontSize = 11.5.sp)
                    OutlinedTextField(
                        value = newCategory,
                        onValueChange = { newCategory = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Prix Unitaire (XOF) :", fontSize = 11.sp)
                            OutlinedTextField(
                                value = newPrice,
                                onValueChange = { newPrice = it },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth().testTag("new_product_price"),
                                shape = RoundedCornerShape(8.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Min. Commande (MOQ) :", fontSize = 11.sp)
                            OutlinedTextField(
                                value = newMinOrder,
                                onValueChange = { newMinOrder = it },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth().testTag("new_product_moq"),
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(text = "Stock disponible :", fontSize = 11.5.sp)
                    OutlinedTextField(
                        value = newStock,
                        onValueChange = { newStock = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            if (newTitle.isNotBlank()) {
                                AfriBabaRepository.addVendorProduct(
                                    title = newTitle,
                                    category = newCategory,
                                    basePriceXof = newPrice.toLongOrNull() ?: 10000L,
                                    minOrderQty = newMinOrder.toIntOrNull() ?: 5,
                                    stockQty = newStock.toIntOrNull() ?: 100,
                                    country = newCountry,
                                    phone = newPhone
                                )
                                showAddProductDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TerracottaPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("submit_new_product_button")
                    ) {
                        Text(text = "Publier au Catalogue AfriBaba", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}
