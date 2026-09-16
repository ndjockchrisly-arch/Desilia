package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AfriBabaRepository
import com.example.model.*
import com.example.ui.components.EscrowBadgeExplanation
import com.example.ui.components.WhatsAppNegotiationDialog
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    product: Product,
    currentCurrency: AppCurrency,
    isLowDataMode: Boolean,
    onBack: () -> Unit,
    onProceedToCheckout: (Product, Int, ShippingTransitMode) -> Unit
) {
    var quantity by remember { mutableIntStateOf(product.minOrderQty) }
    var selectedShippingMode by remember { mutableStateOf(product.shippingModes.first()) }
    var showWhatsAppDialog by remember { mutableStateOf(false) }

    val activeTier = product.getTierForQty(quantity)
    val unitPrice = activeTier.unitPriceXof
    val totalProductPrice = unitPrice * quantity
    val shippingCost = selectedShippingMode.basePriceXof
    val grandTotal = totalProductPrice + shippingCost

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Fiche Grossiste B2B",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("detail_back_button")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour"
                        )
                    }
                },
                actions = {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = EscrowGreenLight,
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = EscrowGreen,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "Garantie Escrow",
                                color = EscrowGreenDark,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                border = BorderStroke(1.dp, CardBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Total Estimé (avec transport) :",
                                fontSize = 11.sp,
                                color = MutedGray
                            )
                            Text(
                                text = currentCurrency.format(grandTotal),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = TerracottaPrimary
                            )
                        }

                        Button(
                            onClick = {
                                onProceedToCheckout(product, quantity, selectedShippingMode)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EscrowGreen),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .height(46.dp)
                                .testTag("proceed_to_checkout_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Commander (Séquestre)",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // 1. Hero Image / Low-Data Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(Color(0xFF0F172A))
            ) {
                if (!isLowDataMode && product.drawableResId != null) {
                    Image(
                        painter = painterResource(id = product.drawableResId),
                        contentDescription = product.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${product.originCountryFlag} ${product.title}",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(20.dp)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xCC000000),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp)
                ) {
                    Text(
                        text = "Origine : ${product.originCountryFlag} ${product.originCountry}",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                // Title
                Text(
                    text = product.title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Supplier Card & Verified Badge
                SupplierVerificationCard(
                    product = product,
                    onNegotiateWhatsApp = { showWhatsAppDialog = true }
                )

                Spacer(modifier = Modifier.height(14.dp))

                // 2. Tarification Dégressive selon Quantité (Tiered Pricing)
                Text(
                    text = "Tarifs Dégressifs Grossiste (Prix FOB)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    product.tiers.forEach { tier ->
                        val isTierActive = quantity >= tier.minQty && (tier.maxQty == null || quantity <= tier.maxQty)
                        val qtyLabel = if (tier.maxQty != null) "${tier.minQty}-${tier.maxQty} pcs" else "≥ ${tier.minQty} pcs"

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(
                                1.5.dp,
                                if (isTierActive) TerracottaPrimary else CardBorder
                            ),
                            color = if (isTierActive) TerracottaPrimary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface,
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = qtyLabel,
                                    fontSize = 10.5.sp,
                                    color = MutedGray,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = currentCurrency.format(tier.unitPriceXof),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.5.sp,
                                    color = if (isTierActive) TerracottaPrimary else MaterialTheme.colorScheme.onSurface
                                )
                                if (isTierActive) {
                                    Text(
                                        text = "Palier actif",
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TerracottaDark
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Interactive Quantity Selector
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, CardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Quantité à commander :",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Min. requis: ${product.minOrderQty} pcs • Dispo: ${product.inStockQty}",
                                fontSize = 10.sp,
                                color = MutedGray
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = {
                                    if (quantity > product.minOrderQty) quantity--
                                },
                                enabled = quantity > product.minOrderQty,
                                modifier = Modifier.testTag("qty_minus_button")
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "Moins")
                            }

                            Text(
                                text = "$quantity",
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )

                            IconButton(
                                onClick = {
                                    if (quantity < product.inStockQty) quantity++
                                },
                                modifier = Modifier.testTag("qty_plus_button")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Plus")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 3. Boutons d'Action Directe : Négocier sur WhatsApp
                Button(
                    onClick = { showWhatsAppDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("negotiate_on_whatsapp_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Chat,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Négocier sur WhatsApp avec le Fournisseur",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 4. Calculateur Automatique Frais de Livraison (Logistique)
                Text(
                    text = "Mode de Transport & Estimation Logistique",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Calculateur automatique selon le corridor de transit",
                    fontSize = 11.sp,
                    color = MutedGray
                )
                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    product.shippingModes.forEach { mode ->
                        val isSelected = selectedShippingMode == mode
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(
                                1.5.dp,
                                if (isSelected) TerracottaPrimary else CardBorder
                            ),
                            color = if (isSelected) TerracottaPrimary.copy(alpha = 0.05f) else MaterialTheme.colorScheme.surface,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedShippingMode = mode }
                                .testTag("shipping_mode_${mode.name}")
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { selectedShippingMode = mode },
                                    colors = RadioButtonDefaults.colors(selectedColor = TerracottaPrimary)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = mode.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${mode.delayText} • ${mode.description}",
                                        fontSize = 11.sp,
                                        color = MutedGray,
                                        lineHeight = 14.sp
                                    )
                                }
                                Text(
                                    text = currentCurrency.format(mode.basePriceXof),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = TerracottaPrimary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 5. Escrow Security Badge & Details
                EscrowBadgeExplanation()

                Spacer(modifier = Modifier.height(16.dp))

                // 6. Description & Spécifications Techniques
                Text(
                    text = "Description du Fournisseur",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = product.description,
                    fontSize = 12.sp,
                    color = CharcoalGray,
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Fiche Technique & Conditionnement",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                product.specs.forEach { (key, value) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = key, fontSize = 11.5.sp, color = MutedGray)
                        Text(text = value, fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold, color = CharcoalGray)
                    }
                }
            }
        }
    }

    if (showWhatsAppDialog) {
        WhatsAppNegotiationDialog(
            product = product,
            selectedQuantity = quantity,
            onDismiss = { showWhatsAppDialog = false }
        )
    }
}

@Composable
fun SupplierVerificationCard(
    product: Product,
    onNegotiateWhatsApp: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF8FAFC),
        border = BorderStroke(1.dp, CardBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(TerracottaPrimary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Storefront,
                    contentDescription = null,
                    tint = TerracottaPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = product.supplierName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.5.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )
                }
                Text(
                    text = product.rccmNumber,
                    fontSize = 10.5.sp,
                    color = EscrowGreenDark,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Score Fournisseur : ${product.rating} ★ (${product.totalOrders} commandes livrées)",
                    fontSize = 10.sp,
                    color = MutedGray
                )
            }

            IconButton(
                onClick = onNegotiateWhatsApp,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF25D366))
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Chat,
                    contentDescription = "WhatsApp",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
