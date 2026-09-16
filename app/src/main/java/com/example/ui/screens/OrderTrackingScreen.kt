package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AfriBabaRepository
import com.example.model.*
import com.example.ui.theme.*

@Composable
fun OrderTrackingScreen(
    currentCurrency: AppCurrency,
    selectedOrderId: String? = null,
    onBack: () -> Unit = {}
) {
    val orders by AfriBabaRepository.orders.collectAsState()
    var activeOrderId by remember { mutableStateOf(selectedOrderId ?: orders.firstOrNull()?.orderId) }

    val activeOrder = orders.find { it.orderId == activeOrderId } ?: orders.firstOrNull()
    var otpInput by remember { mutableStateOf("") }
    var releaseMessage by remember { mutableStateOf<String?>(null) }
    var isSuccessMessage by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp, bottom = 90.dp)
    ) {
        item {
            Text(
                text = "Suivi Logistique & Séquestre",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Traçabilité temps réel et libération des fonds par OTP",
                fontSize = 11.5.sp,
                color = MutedGray
            )
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Active Order Selector if multiple
        if (orders.size > 1) {
            item {
                Text(
                    text = "Mes Commandes Récents :",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = CharcoalGray
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    orders.forEach { order ->
                        val isSelected = order.orderId == activeOrder?.orderId
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, if (isSelected) TerracottaPrimary else CardBorder),
                            color = if (isSelected) TerracottaPrimary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface,
                            modifier = Modifier
                                .clickable {
                                    activeOrderId = order.orderId
                                    releaseMessage = null
                                }
                                .testTag("order_tab_${order.orderId}")
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(
                                    text = order.orderId,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = if (isSelected) TerracottaPrimary else CharcoalGray
                                )
                                Text(
                                    text = order.product.title.take(20) + "...",
                                    fontSize = 10.sp,
                                    color = MutedGray
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }
        }

        if (activeOrder == null) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Aucune commande pour le moment.", color = MutedGray)
                }
            }
        } else {
            // Order Header Card
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, CardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "N° ${activeOrder.orderId}",
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(activeOrder.escrowStatus.badgeColorHex).copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = activeOrder.escrowStatus.label,
                                    color = Color(activeOrder.escrowStatus.badgeColorHex),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = activeOrder.product.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.5.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Quantité : ${activeOrder.quantity} pcs",
                                fontSize = 11.5.sp,
                                color = CharcoalGray
                            )
                            Text(
                                text = "Total Séquestré : ${currentCurrency.format(activeOrder.totalAmountXof)}",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = TerracottaPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Règlement : ${activeOrder.paymentProvider.displayName} (${activeOrder.phoneNumber})",
                            fontSize = 10.5.sp,
                            color = MutedGray
                        )

                        if (activeOrder.relayPoint != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Point Relais : ${activeOrder.relayPoint.name} - ${activeOrder.relayPoint.landmark}",
                                fontSize = 10.5.sp,
                                color = EscrowGreenDark,
                                fontWeight = FontWeight.SemiBold
                            )
                        } else if (activeOrder.directAddressLandmark != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Livreur Moto vers : ${activeOrder.directAddressLandmark}",
                                fontSize = 10.5.sp,
                                color = TerracottaPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Buyer's Escrow Inspection & OTP Unlock Card
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = EscrowGreenLight,
                    border = BorderStroke(1.5.dp, EscrowGreen.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Key, contentDescription = null, tint = EscrowGreen, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Code OTP d'Inspection & Déblocage",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = EscrowGreenDark
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Votre code secret d'inspection est :",
                            fontSize = 11.sp,
                            color = CharcoalGray
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color.White,
                                border = BorderStroke(1.dp, EscrowGreen)
                            ) {
                                Text(
                                    text = activeOrder.escrowOtpCode,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 6.sp,
                                    color = EscrowGreenDark,
                                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 6.dp)
                                )
                            }
                        }

                        Text(
                            text = "⚠️ RÈGLE DE SÉCURITÉ : Ne donnez ce code au livreur ou au point relais qu'APRÈS avoir vérifié l'intégrité de vos colis.",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TerracottaDark,
                            lineHeight = 13.sp
                        )

                        if (activeOrder.escrowStatus != EscrowStatus.RELEASED_TO_VENDOR) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = otpInput,
                                    onValueChange = { if (it.length <= 4) otpInput = it },
                                    placeholder = { Text("Tester déblocage OTP (ex: ${activeOrder.escrowOtpCode})") },
                                    singleLine = true,
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("otp_input_field"),
                                    shape = RoundedCornerShape(8.dp)
                                )

                                Button(
                                    onClick = {
                                        val success = AfriBabaRepository.releaseEscrowByOtp(activeOrder.orderId, otpInput)
                                        if (success) {
                                            isSuccessMessage = true
                                            releaseMessage = "Paiement débloqué avec succès ! Le fournisseur a reçu les fonds sur son compte Mobile Money."
                                            otpInput = ""
                                        } else {
                                            isSuccessMessage = false
                                            releaseMessage = "Code OTP incorrect. Veuillez vérifier le code à 4 chiffres."
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = EscrowGreen),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.testTag("submit_otp_button")
                                ) {
                                    Text(text = "Valider", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        releaseMessage?.let { msg ->
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = msg,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSuccessMessage) EscrowGreenDark else Color.Red
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Timeline Steps
            item {
                Text(
                    text = "Étapes d'Acheminement",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            items(activeOrder.trackingSteps) { step ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(
                                    if (step.isCompleted) EscrowGreen
                                    else if (step.isCurrent) TerracottaPrimary
                                    else Color(0xFFCBD5E1)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (step.isCompleted) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            } else {
                                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color.White))
                            }
                        }
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(36.dp)
                                .background(if (step.isCompleted) EscrowGreen else Color(0xFFCBD5E1))
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = step.title,
                                fontWeight = if (step.isCurrent || step.isCompleted) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.5.sp,
                                color = if (step.isCurrent) TerracottaPrimary else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = step.time,
                                fontSize = 10.sp,
                                color = MutedGray
                            )
                        }
                        Text(
                            text = step.subtitle,
                            fontSize = 11.sp,
                            color = CharcoalGray,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }
    }
}
