package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AfriBabaRepository
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.*

enum class AppNavigationTab(val label: String, val icon: ImageVector) {
    HOME("Accueil", Icons.Default.Home),
    GROUP_BUY("Groupes", Icons.Default.Groups),
    TRACKING("Suivi Colis", Icons.Default.LocalShipping),
    VENDOR("Vendeur", Icons.Default.Storefront)
}

sealed class ScreenDestination {
    object MainTabs : ScreenDestination()
    data class ProductDetail(val product: Product) : ScreenDestination()
    data class Checkout(
        val product: Product,
        val quantity: Int,
        val shippingMode: ShippingTransitMode
    ) : ScreenDestination()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                AfriBabaApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AfriBabaApp() {
    val currentCurrency by AfriBabaRepository.currentCurrency.collectAsState()
    val selectedCountry by AfriBabaRepository.selectedCountryFilter.collectAsState()
    val isLowDataMode by AfriBabaRepository.isLowDataMode.collectAsState()

    var currentTab by remember { mutableStateOf(AppNavigationTab.HOME) }
    var currentDestination by remember { mutableStateOf<ScreenDestination>(ScreenDestination.MainTabs) }
    var targetedOrderId by remember { mutableStateOf<String?>(null) }

    // Global Modals
    var showCountryDialog by remember { mutableStateOf(false) }
    var showCurrencyDialog by remember { mutableStateOf(false) }
    var showSearchDialog by remember { mutableStateOf(false) }
    var showArchitectureDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (currentDestination is ScreenDestination.MainTabs) {
                AfriBabaHeader(
                    currentCurrency = currentCurrency,
                    selectedCountryCode = selectedCountry,
                    isLowDataMode = isLowDataMode,
                    onCurrencyClick = { showCurrencyDialog = true },
                    onCountryClick = { showCountryDialog = true },
                    onToggleLowData = { AfriBabaRepository.toggleLowDataMode() },
                    onOpenSearch = { showSearchDialog = true },
                    onOpenArchitecture = { showArchitectureDialog = true }
                )
            }
        },
        bottomBar = {
            if (currentDestination is ScreenDestination.MainTabs) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    windowInsets = NavigationBarDefaults.windowInsets
                ) {
                    AppNavigationTab.entries.forEach { tab ->
                        val isSelected = currentTab == tab
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { currentTab = tab },
                            icon = {
                                Icon(
                                    imageVector = tab.icon,
                                    contentDescription = tab.label
                                )
                            },
                            label = {
                                Text(
                                    text = tab.label,
                                    fontSize = 10.5.sp
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.White,
                                selectedTextColor = TerracottaPrimary,
                                indicatorColor = TerracottaPrimary,
                                unselectedIconColor = CharcoalGray,
                                unselectedTextColor = CharcoalGray
                            ),
                            modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val dest = currentDestination) {
                is ScreenDestination.MainTabs -> {
                    when (currentTab) {
                        AppNavigationTab.HOME -> {
                            HomeScreen(
                                currentCurrency = currentCurrency,
                                selectedCountry = selectedCountry,
                                isLowDataMode = isLowDataMode,
                                onProductClick = { product ->
                                    currentDestination = ScreenDestination.ProductDetail(product)
                                },
                                onOpenGroupBuy = { product ->
                                    currentDestination = ScreenDestination.ProductDetail(product)
                                },
                                onOpenVendorSpace = {
                                    currentTab = AppNavigationTab.VENDOR
                                }
                            )
                        }
                        AppNavigationTab.GROUP_BUY -> {
                            // Dedicated Group Buying Feed
                            val products by AfriBabaRepository.products.collectAsState()
                            val groupBuys = products.filter { it.groupBuy != null }

                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.background)
                                    .padding(16.dp)
                            ) {
                                GroupBuySectionHeader()
                                Spacer(modifier = Modifier.height(14.dp))
                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    groupBuys.forEach { product ->
                                        GroupBuyCard(
                                            product = product,
                                            currency = currentCurrency,
                                            isLowDataMode = isLowDataMode,
                                            onClick = {
                                                currentDestination = ScreenDestination.ProductDetail(product)
                                            }
                                        )
                                    }
                                }
                            }
                        }
                        AppNavigationTab.TRACKING -> {
                            OrderTrackingScreen(
                                currentCurrency = currentCurrency,
                                selectedOrderId = targetedOrderId
                            )
                        }
                        AppNavigationTab.VENDOR -> {
                            VendorDashboardScreen(
                                currentCurrency = currentCurrency,
                                onNavigateToProduct = { product ->
                                    currentDestination = ScreenDestination.ProductDetail(product)
                                }
                            )
                        }
                    }
                }
                is ScreenDestination.ProductDetail -> {
                    ProductDetailScreen(
                        product = dest.product,
                        currentCurrency = currentCurrency,
                        isLowDataMode = isLowDataMode,
                        onBack = { currentDestination = ScreenDestination.MainTabs },
                        onProceedToCheckout = { product, quantity, shippingMode ->
                            currentDestination = ScreenDestination.Checkout(product, quantity, shippingMode)
                        }
                    )
                }
                is ScreenDestination.Checkout -> {
                    CheckoutScreen(
                        product = dest.product,
                        quantity = dest.quantity,
                        shippingMode = dest.shippingMode,
                        currentCurrency = currentCurrency,
                        onBack = { currentDestination = ScreenDestination.ProductDetail(dest.product) },
                        onOrderSuccess = { order ->
                            targetedOrderId = order.orderId
                            currentDestination = ScreenDestination.MainTabs
                            currentTab = AppNavigationTab.TRACKING
                        }
                    )
                }
            }
        }
    }

    // Dialogs
    if (showCountryDialog) {
        CountrySelectionDialog(
            selectedCode = selectedCountry,
            onDismiss = { showCountryDialog = false },
            onSelectCountry = { AfriBabaRepository.setCountryFilter(it) }
        )
    }

    if (showCurrencyDialog) {
        CurrencySelectionDialog(
            currentCurrency = currentCurrency,
            onDismiss = { showCurrencyDialog = false },
            onSelectCurrency = { AfriBabaRepository.setCurrency(it) }
        )
    }

    if (showSearchDialog) {
        MultimodalSearchDialog(
            onDismiss = { showSearchDialog = false },
            onProductSelected = { product ->
                currentDestination = ScreenDestination.ProductDetail(product)
            }
        )
    }

    if (showArchitectureDialog) {
        ArchitectureAndSpecsDialog(
            onDismiss = { showArchitectureDialog = false }
        )
    }
}
