package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.AfriBabaRepository
import com.example.model.*
import com.example.ui.components.EscrowBadgeExplanation
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    product: Product,
    quantity: Int,
    shippingMode: ShippingTransitMode,
    currentCurrency: AppCurrency,
    onBack: () -> Unit,
    onOrderSuccess: (EscrowOrder) -> Unit
) {
    val tier = product.getTierForQty(quantity)
    val unitPrice = tier.unitPriceXof
    val shippingCost = shippingMode.basePriceXof
    val totalAmount = (unitPrice * quantity) + shippingCost

    var deliveryType by remember { mutableStateOf(DeliveryType.RELAY_POINT) }
    var selectedRelayPoint by remember { mutableStateOf(AfriBabaRepository.availableRelayPoints.first()) }
    var landmarkAddress by remember { mutableStateOf("Douala Akwa, face Pharmacie du Centre, porte bleue") }

    var selectedPaymentProvider by remember { mutableStateOf(PaymentProvider.WAVE) }
    var phoneNumber by remember { mutableStateOf("+221 77 890 12 34") }
    var isEscrowEnabled by remember { mutableStateOf(true) }

    // Simulation states
    var isProcessingPayment by remember { mutableStateOf(false) }
    var paymentStepText by remember { mutableStateOf("") }
    var showUssdSimDialog by remember { mutableStateOf(false) }
    var enteredPin by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Commande & Séquestre Mobile Money",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
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
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Total à bloquer sous séquestre :", fontSize = 11.sp, color = MutedGray)
                            Text(
                                text = currentCurrency.format(totalAmount),
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Black,
                                color = TerracottaPrimary
                            )
                        }

                        Button(
                            onClick = {
                                if (selectedPaymentProvider == PaymentProvider.CASH_ON_DELIVERY) {
                                    val order = AfriBabaRepository.createOrder(
                                        product = product,
                                        quantity = quantity,
                                        shippingMode = shippingMode,
                                        paymentProvider = selectedPaymentProvider,
                                        phoneNumber = phoneNumber,
                                        deliveryType = deliveryType,
                                        relayPoint = if (deliveryType == DeliveryType.RELAY_POINT) selectedRelayPoint else null,
                                        directAddress = if (deliveryType == DeliveryType.MOTO_DIRECT) landmarkAddress else null
                                    )
                                    onOrderSuccess(order)
                                } else {
                                    showUssdSimDialog = true
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EscrowGreen),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .height(48.dp)
                                .testTag("validate_escrow_payment_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Payer & Séquestrer",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 13.sp
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
                .padding(16.dp)
        ) {
            // Summary Card
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, CardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Récapitulatif de la Commande",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = product.title,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Quantité : $quantity unités x ${currentCurrency.format(unitPrice)}", fontSize = 11.5.sp, color = CharcoalGray)
                        Text(text = currentCurrency.format(unitPrice * quantity), fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Transport (${shippingMode.title}) :", fontSize = 11.5.sp, color = CharcoalGray)
                        Text(text = currentCurrency.format(shippingCost), fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 1. Choix du mode de livraison adapté aux réalités africaines
            Text(
                text = "1. Mode de Réception & Géolocalisation",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Relay Point Option
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(
                        1.5.dp,
                        if (deliveryType == DeliveryType.RELAY_POINT) TerracottaPrimary else CardBorder
                    ),
                    color = if (deliveryType == DeliveryType.RELAY_POINT) TerracottaPrimary.copy(alpha = 0.06f) else MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { deliveryType = DeliveryType.RELAY_POINT }
                        .testTag("delivery_type_relay")
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Store, contentDescription = null, tint = TerracottaPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Point Relais", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Text(text = "Boutique / Station partenaire sécurisée", fontSize = 10.sp, color = MutedGray)
                    }
                }

                // Moto Delivery Option
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(
                        1.5.dp,
                        if (deliveryType == DeliveryType.MOTO_DIRECT) TerracottaPrimary else CardBorder
                    ),
                    color = if (deliveryType == DeliveryType.MOTO_DIRECT) TerracottaPrimary.copy(alpha = 0.06f) else MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { deliveryType = DeliveryType.MOTO_DIRECT }
                        .testTag("delivery_type_moto")
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.TwoWheeler, contentDescription = null, tint = TerracottaPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Livreur Moto", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Text(text = "Repère GPS / WhatsApp à domicile", fontSize = 10.sp, color = MutedGray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Sub-form based on delivery choice
            if (deliveryType == DeliveryType.RELAY_POINT) {
                Text(text = "Sélectionnez votre Point Relais de retrait :", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(4.dp))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    AfriBabaRepository.availableRelayPoints.forEach { point ->
                        val isSelected = selectedRelayPoint == point
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, if (isSelected) TerracottaPrimary else CardBorder),
                            color = if (isSelected) TerracottaPrimary.copy(alpha = 0.04f) else Color.White,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedRelayPoint = point }
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { selectedRelayPoint = point },
                                    colors = RadioButtonDefaults.colors(selectedColor = TerracottaPrimary)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "${point.name} (${point.city})", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Text(text = "📍 ${point.landmark}", fontSize = 10.5.sp, color = CharcoalGray)
                                    Text(text = "⏰ ${point.openingHours} • Tél: ${point.contactPhone}", fontSize = 9.5.sp, color = MutedGray)
                                }
                            }
                        }
                    }
                }
            } else {
                Text(text = "Description précise du lieu (Faute d'adresse postale) :", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = landmarkAddress,
                    onValueChange = { landmarkAddress = it },
                    placeholder = { Text("Ex: Douala Akwa, en face de la pharmacie, portail vert...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("landmark_input_field"),
                    shape = RoundedCornerShape(10.dp),
                    leadingIcon = {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = TerracottaPrimary)
                    }
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ShareLocation, contentDescription = null, tint = EscrowGreen, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Position GPS & Épingle WhatsApp partagée au coursier automatiquement.",
                        fontSize = 10.sp,
                        color = EscrowGreenDark
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 2. Choix du Mode de Paiement Localisé
            Text(
                text = "2. Paiement Localisé & Séquestre",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(6.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PaymentProvider.entries.forEach { provider ->
                    val isSelected = selectedPaymentProvider == provider
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(
                            1.5.dp,
                            if (isSelected) Color(provider.brandColorHex) else CardBorder
                        ),
                        color = if (isSelected) Color(provider.brandColorHex).copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedPaymentProvider = provider }
                            .testTag("payment_provider_${provider.name}")
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(Color(provider.brandColorHex)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = provider.operatorName.take(2).uppercase(),
                                    color = if (provider == PaymentProvider.MTN_MOMO) DarkSlate else Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = provider.displayName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.5.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = provider.ussdInstruction,
                                    fontSize = 10.sp,
                                    color = MutedGray,
                                    lineHeight = 13.sp
                                )
                            }
                            RadioButton(
                                selected = isSelected,
                                onClick = { selectedPaymentProvider = provider },
                                colors = RadioButtonDefaults.colors(selectedColor = Color(provider.brandColorHex))
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Phone input for Mobile Money
            if (selectedPaymentProvider != PaymentProvider.CASH_ON_DELIVERY) {
                Text(
                    text = "Numéro de compte Mobile Money pour le débit :",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("momo_phone_input"),
                    shape = RoundedCornerShape(10.dp),
                    leadingIcon = {
                        Icon(Icons.Default.PhoneIphone, contentDescription = null, tint = TerracottaPrimary)
                    },
                    trailingIcon = {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = EscrowGreenLight,
                            modifier = Modifier.padding(end = 6.dp)
                        ) {
                            Text(
                                text = "Détection Auto",
                                color = EscrowGreenDark,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Escrow protection toggle & summary
            EscrowBadgeExplanation()
        }
    }

    // USSD / STK Push Simulation Modal
    if (showUssdSimDialog) {
        Dialog(onDismissRequest = { if (!isProcessingPayment) showUssdSimDialog = false }) {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(Color(selectedPaymentProvider.brandColorHex)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhoneAndroid,
                            contentDescription = null,
                            tint = if (selectedPaymentProvider == PaymentProvider.MTN_MOMO) DarkSlate else Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Simulation USSD ${selectedPaymentProvider.operatorName}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Paiement de ${currentCurrency.format(totalAmount)} vers le Séquestre AfriBaba pour la commande de $quantity unités de '${product.title.take(30)}...'",
                        fontSize = 11.5.sp,
                        color = CharcoalGray,
                        textAlign = TextAlign.Center,
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    if (!isProcessingPayment) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFF1F5F9),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Entrez votre code secret PIN Mobile Money (4 chiffres) :",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = DarkSlate
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                OutlinedTextField(
                                    value = enteredPin,
                                    onValueChange = { if (it.length <= 4) enteredPin = it },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                                    placeholder = { Text("****") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("ussd_pin_input"),
                                    shape = RoundedCornerShape(8.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                isProcessingPayment = true
                                paymentStepText = "Envoi de la requête au serveur Telco..."
                                coroutineScope.launch {
                                    delay(1200)
                                    paymentStepText = "Réception du webhook opérateur : transaction validée..."
                                    delay(1200)
                                    paymentStepText = "Verrouillage des fonds sous Séquestre garanti !"
                                    delay(1000)
                                    showUssdSimDialog = false
                                    isProcessingPayment = false
                                    val order = AfriBabaRepository.createOrder(
                                        product = product,
                                        quantity = quantity,
                                        shippingMode = shippingMode,
                                        paymentProvider = selectedPaymentProvider,
                                        phoneNumber = phoneNumber,
                                        deliveryType = deliveryType,
                                        relayPoint = if (deliveryType == DeliveryType.RELAY_POINT) selectedRelayPoint else null,
                                        directAddress = if (deliveryType == DeliveryType.MOTO_DIRECT) landmarkAddress else null
                                    )
                                    onOrderSuccess(order)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EscrowGreen),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("confirm_ussd_pin_button")
                        ) {
                            Text(text = "Valider le Paiement Sécurisé", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    } else {
                        // Progress state
                        CircularProgressIndicator(color = EscrowGreen, modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = paymentStepText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = EscrowGreenDark,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
