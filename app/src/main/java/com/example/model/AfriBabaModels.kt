package com.example.model

enum class AppCurrency(
    val code: String,
    val symbol: String,
    val label: String,
    val rateFromXof: Double // 1 XOF = rate * target currency
) {
    XOF("XOF", "FCFA", "Franc CFA (UEMOA)", 1.0),
    XAF("XAF", "FCFA", "Franc CFA (CEMAC)", 1.0),
    NGN("NGN", "₦", "Naira Nigérian", 2.45),
    KES("KES", "KSh", "Shilling Kényan", 0.22),
    GHS("GHS", "GH₵", "Cedi Ghanéen", 0.026),
    USD("USD", "$", "Dollar US", 0.00165);

    fun format(amountInXof: Long): String {
        val converted = amountInXof * rateFromXof
        return when (this) {
            XOF, XAF -> "${"%,d".format(converted.toLong())} $symbol"
            NGN -> "$symbol${"%,d".format(converted.toLong())}"
            KES -> "$symbol ${"%,d".format(converted.toLong())}"
            GHS -> "$symbol ${"%.2f".format(converted)}"
            USD -> "$symbol${"%.2f".format(converted)}"
        }
    }
}

data class CountryFilter(
    val code: String,
    val name: String,
    val flagEmoji: String,
    val dialCode: String
)

data class PriceTier(
    val minQty: Int,
    val maxQty: Int?, // null = and more
    val unitPriceXof: Long
)

enum class ShippingTransitMode(
    val title: String,
    val delayText: String,
    val basePriceXof: Long,
    val description: String
) {
    AIR_FREIGHT("Fret Aérien Express", "3 - 5 jours", 14500, "Idéal petits colis urgents, sécurisé & tracé par avion"),
    INTERSTATE_ROAD("Réseau Routier Inter-États", "7 - 12 jours", 4800, "Couloirs logistiques CEDEAO / CEMAC par camion"),
    SEA_FREIGHT("Fret Maritime Groupé", "20 - 35 jours", 1900, "Le plus économique pour conteneurs et gros volumes")
}

data class GroupBuyCampaign(
    val campaignId: String,
    val targetQty: Int,
    val currentQty: Int,
    val discountPercent: Int,
    val hoursRemaining: Int
) {
    val progress: Float get() = (currentQty.toFloat() / targetQty.toFloat()).coerceIn(0f, 1f)
    val isUnlocked: Boolean get() = currentQty >= targetQty
}

data class Product(
    val id: String,
    val title: String,
    val category: String,
    val originCountry: String,
    val originCountryFlag: String,
    val supplierName: String,
    val isVerifiedSupplier: Boolean,
    val rccmNumber: String,
    val whatsappNumber: String,
    val vendorPhone: String,
    val rating: Double,
    val totalOrders: Int,
    val minOrderQty: Int,
    val inStockQty: Int,
    val tiers: List<PriceTier>,
    val shippingModes: List<ShippingTransitMode>,
    val groupBuy: GroupBuyCampaign?,
    val description: String,
    val specs: Map<String, String>,
    val drawableResId: Int?,
    val badge: String? = null
) {
    fun getTierForQty(qty: Int): PriceTier {
        return tiers.lastOrNull { qty >= it.minQty } ?: tiers.first()
    }
}

enum class PaymentProvider(
    val displayName: String,
    val operatorName: String,
    val brandColorHex: Long,
    val ussdInstruction: String
) {
    MTN_MOMO("MTN Mobile Money", "MTN MoMo", 0xFFFFCC00, "Tapez *126# ou validez l'invite USSD sur votre mobile"),
    ORANGE_MONEY("Orange Money", "Orange Money", 0xFFFF7900, "Composez #144# ou confirmez dans votre application Orange Money"),
    WAVE("Wave Mobile Money", "Wave", 0xFF1BA0E2, "Scannez le QR Code Wave ou validez la notification push instantanée (0% de frais)"),
    MPESA("M-Pesa Safaricom", "M-Pesa", 0xFF00A859, "Un STK Push Safaricom s'affiche sur votre écran : entrez votre PIN"),
    CASH_ON_DELIVERY("Paiement à la Livraison", "Cash / MoMo sur place", 0xFF334155, "Réglez directement au livreur partenaire après inspection")
}

enum class EscrowStatus(val label: String, val badgeColorHex: Long) {
    ESCROW_LOCKED("Fonds Sécurisés sous Séquestre", 0xFF0D8A56),
    IN_TRANSIT("Colis en Acheminement", 0xFF0284C7),
    ARRIVED_RELAY("Arrivé au Point Relais", 0xFFE5A118),
    RELEASED_TO_VENDOR("Paiement Débloqué au Vendeur", 0xFF16A34A),
    DISPUTED("Litige Ouvert / Médiation", 0xFFDC2626)
}

enum class DeliveryType(val label: String) {
    RELAY_POINT("Point Relais Partenaire"),
    MOTO_DIRECT("Livreur Indépendant Moto (Domicile / Boutique)")
}

data class RelayPoint(
    val id: String,
    val name: String,
    val city: String,
    val district: String,
    val landmark: String,
    val openingHours: String,
    val contactPhone: String
)

data class TrackingStep(
    val title: String,
    val subtitle: String,
    val time: String,
    val isCompleted: Boolean,
    val isCurrent: Boolean
)

data class EscrowOrder(
    val orderId: String,
    val product: Product,
    val quantity: Int,
    val unitPriceXof: Long,
    val shippingCostXof: Long,
    val totalAmountXof: Long,
    val paymentProvider: PaymentProvider,
    val phoneNumber: String,
    val escrowStatus: EscrowStatus,
    val deliveryType: DeliveryType,
    val relayPoint: RelayPoint?,
    val directAddressLandmark: String?,
    val escrowOtpCode: String,
    val createdAt: String,
    val trackingSteps: List<TrackingStep>
)
