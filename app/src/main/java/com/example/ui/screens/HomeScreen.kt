package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.AfriBabaRepository
import com.example.model.*
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    currentCurrency: AppCurrency,
    selectedCountry: String,
    isLowDataMode: Boolean,
    onProductClick: (Product) -> Unit,
    onOpenGroupBuy: (Product) -> Unit,
    onOpenVendorSpace: () -> Unit
) {
    val products by AfriBabaRepository.products.collectAsState()
    var selectedCategory by remember { mutableStateOf("Tous") }

    val categories = listOf(
        "Tous",
        "Tissus & Textiles",
        "Énergie & Solaire",
        "Agroalimentaire",
        "Électronique",
        "Mode & Friperie"
    )

    val filteredProducts = products.filter { product ->
        val matchesCountry = selectedCountry == "ALL" ||
                AfriBabaRepository.availableCountries.find { it.code == selectedCountry }?.name == product.originCountry
        val matchesCategory = selectedCategory == "Tous" || product.category.contains(selectedCategory, ignoreCase = true)
        matchesCountry && matchesCategory
    }

    val groupBuyProducts = products.filter { it.groupBuy != null }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // 1. Promotional lightweight banner
        item {
            PromoHeroBanner(
                isLowDataMode = isLowDataMode,
                onVendorClick = onOpenVendorSpace
            )
        }

        // 2. Category Filter Chips
        item {
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    val isSelected = selectedCategory == category
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = category },
                        label = {
                            Text(
                                text = category,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TerracottaPrimary,
                            selectedLabelColor = Color.White,
                            containerColor = Color.White,
                            labelColor = CharcoalGray
                        ),
                        border = BorderStroke(1.dp, if (isSelected) TerracottaPrimary else CardBorder),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.testTag("category_chip_$category")
                    )
                }
            }
        }

        // 3. Section "Groupement d'Achats" (Achats Groupés pour réduire les frais)
        if (groupBuyProducts.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(14.dp))
                GroupBuySectionHeader()
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(groupBuyProducts) { product ->
                        GroupBuyCard(
                            product = product,
                            currency = currentCurrency,
                            isLowDataMode = isLowDataMode,
                            onClick = { onOpenGroupBuy(product) }
                        )
                    }
                }
            }
        }

        // 4. Section Title for Main Wholesale Catalog
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Catalogue Grossistes & Usines",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "(${filteredProducts.size})",
                        fontSize = 12.sp,
                        color = MutedGray
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = EscrowGreenLight
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = EscrowGreen,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "100% Séquestre",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = EscrowGreenDark
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // 5. Product Grid (2 columns or responsive)
        if (filteredProducts.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = null,
                            tint = MutedGray,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Aucun produit trouvé pour ce pays / catégorie",
                            color = MutedGray,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        } else {
            items(filteredProducts.chunked(2)) { pair ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 5.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ProductGridCard(
                        product = pair[0],
                        currency = currentCurrency,
                        isLowDataMode = isLowDataMode,
                        modifier = Modifier.weight(1f),
                        onClick = { onProductClick(pair[0]) }
                    )
                    if (pair.size > 1) {
                        ProductGridCard(
                            product = pair[1],
                            currency = currentCurrency,
                            isLowDataMode = isLowDataMode,
                            modifier = Modifier.weight(1f),
                            onClick = { onProductClick(pair[1]) }
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun PromoHeroBanner(
    isLowDataMode: Boolean,
    onVendorClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .clip(RoundedCornerShape(16.dp)),
        color = TerracottaDark
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            if (!isLowDataMode) {
                Image(
                    painter = painterResource(id = R.drawable.hero_trade_banner_1789597858056),
                    contentDescription = "Bannière Logistique AfriBaba",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (isLowDataMode) 110.dp else 140.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xEE0F172A),
                                Color(0xAA0F172A),
                                Color(0x44D97706)
                            )
                        )
                    )
                    .padding(14.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Surface(
                            color = TerracottaPrimary,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "COMMERCE INTER-AFRIQUE B2B",
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Achetez en gros au prix usine",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Paiement Mobile Money sécurisé par Séquestre & Négociation WhatsApp",
                            color = Color(0xFFE2E8F0),
                            fontSize = 11.sp,
                            maxLines = 2
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Zéro arnaque • Expédition suivie",
                            color = SaffronGold,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = EscrowGreen,
                            modifier = Modifier
                                .clickable { onVendorClick() }
                                .testTag("banner_vendor_link")
                        ) {
                            Text(
                                text = "Espace Vendeur",
                                color = Color.White,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GroupBuySectionHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(SaffronGold),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Groups,
                contentDescription = null,
                tint = DarkSlate,
                modifier = Modifier.size(16.dp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = "Groupement d'Achats (Achats Groupés PME)",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Commandez à plusieurs pour débloquer le tarif conteneur",
                fontSize = 11.sp,
                color = MutedGray
            )
        }
    }
}

@Composable
fun GroupBuyCard(
    product: Product,
    currency: AppCurrency,
    isLowDataMode: Boolean,
    onClick: () -> Unit
) {
    val groupBuy = product.groupBuy ?: return

    Surface(
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, SaffronGold.copy(alpha = 0.5f)),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .width(260.dp)
            .clickable { onClick() }
            .testTag("group_buy_card_${product.id}")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = TerracottaPrimary
                ) {
                    Text(
                        text = "-${groupBuy.discountPercent}% GROUPE",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = null,
                        tint = TerracottaDark,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "${groupBuy.hoursRemaining}h restantes",
                        fontSize = 10.5.sp,
                        color = TerracottaDark,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = product.title,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Pricing comparison
            val bestTier = product.tiers.last().unitPriceXof
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = currency.format(bestTier),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                    color = EscrowGreenDark
                )
                Text(
                    text = " /unité",
                    fontSize = 10.sp,
                    color = MutedGray
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Progress Bar
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${groupBuy.currentQty} / ${groupBuy.targetQty} réservés",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = CharcoalGray
                    )
                    Text(
                        text = "${(groupBuy.progress * 100).toInt()}%",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = SaffronGold
                    )
                }
                Spacer(modifier = Modifier.height(3.dp))
                LinearProgressIndicator(
                    progress = { groupBuy.progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(CircleShape),
                    color = if (groupBuy.isUnlocked) EscrowGreen else SaffronGold,
                    trackColor = Color(0xFFE2E8F0)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onClick,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SaffronGold),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(34.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = "Rejoindre l'Achat Groupé",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkSlate
                )
            }
        }
    }
}

@Composable
fun ProductGridCard(
    product: Product,
    currency: AppCurrency,
    isLowDataMode: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, CardBorder),
        modifier = modifier
            .clickable { onClick() }
            .testTag("product_card_${product.id}")
    ) {
        Column {
            // Card visual top
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(Color(0xFFF1F5F9))
            ) {
                if (!isLowDataMode && product.drawableResId != null) {
                    Image(
                        painter = painterResource(id = product.drawableResId),
                        contentDescription = product.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // Ultra-lightweight fallback banner for 2G networks
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFFE2E8F0)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${product.originCountryFlag} ${product.category}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = CharcoalGray
                        )
                    }
                }

                // Origin Country Flag & Verified Badge
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xCC0F172A)
                    ) {
                        Text(
                            text = "${product.originCountryFlag} ${product.originCountry}",
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }

                    if (product.isVerifiedSupplier) {
                        Surface(
                            shape = CircleShape,
                            color = EscrowGreen
                        ) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = "Vérifié RCCM",
                                tint = Color.White,
                                modifier = Modifier
                                    .padding(3.dp)
                                    .size(12.dp)
                            )
                        }
                    }
                }
            }

            // Product Details
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = product.title,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 15.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Price tier display
                val minPrice = product.tiers.last().unitPriceXof
                val maxPrice = product.tiers.first().unitPriceXof

                Text(
                    text = if (minPrice == maxPrice) {
                        currency.format(minPrice)
                    } else {
                        "${currency.format(minPrice)} - ${currency.format(maxPrice)}"
                    },
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 12.sp,
                    color = TerracottaPrimary
                )

                Text(
                    text = "Min: ${product.minOrderQty} pcs • Stock: ${product.inStockQty}",
                    fontSize = 9.5.sp,
                    color = MutedGray
                )

                Spacer(modifier = Modifier.height(6.dp))

                // WhatsApp & Escrow Micro tags
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = SaffronGold,
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${product.rating}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = CharcoalGray
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = EscrowGreenLight
                    ) {
                        Text(
                            text = "MoMo Escrow",
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = EscrowGreenDark,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}
