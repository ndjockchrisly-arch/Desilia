package com.example.data

import com.example.R
import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object AfriBabaRepository {

    val availableCountries = listOf(
        CountryFilter("ALL", "Tous les pays", "🌍", "+000"),
        CountryFilter("SN", "Sénégal", "🇸🇳", "+221"),
        CountryFilter("CI", "Côte d'Ivoire", "🇨🇮", "+225"),
        CountryFilter("CM", "Cameroun", "🇨🇲", "+237"),
        CountryFilter("NG", "Nigéria", "🇳🇬", "+234"),
        CountryFilter("KE", "Kenya", "🇰🇪", "+254"),
        CountryFilter("GH", "Ghana", "🇬🇭", "+233")
    )

    val availableRelayPoints = listOf(
        RelayPoint(
            id = "RP-DAK-01",
            name = "Relais Boutique Al-Amine",
            city = "Dakar",
            district = "Marché Sandaga",
            landmark = "Face pharmacie Guigon, Allées Delmas",
            openingHours = "08h00 - 19h30 (Lun-Sam)",
            contactPhone = "+221 77 452 89 12"
        ),
        RelayPoint(
            id = "RP-ABJ-02",
            name = "Relais Station Total Treichville",
            city = "Abidjan",
            district = "Boulevard VGE",
            landmark = "Près du Carrefour Solibra",
            openingHours = "24h/24 & 7j/7",
            contactPhone = "+225 07 88 12 34 56"
        ),
        RelayPoint(
            id = "RP-DLA-03",
            name = "Hub Logistique Akwa Express",
            city = "Douala",
            district = "Akwa Nord",
            landmark = "Rue de la Joie, à 50m de l'agence Camtel",
            openingHours = "08h30 - 18h00",
            contactPhone = "+237 699 30 11 22"
        ),
        RelayPoint(
            id = "RP-NBO-04",
            name = "Gikomba Wholesale Cargo Pickup",
            city = "Nairobi",
            district = "Kamukunji",
            landmark = "Near Pumwani Maternity, Gate 3",
            openingHours = "07h00 - 18h00",
            contactPhone = "+254 712 345 678"
        )
    )

    private val initialProducts = listOf(
        Product(
            id = "PROD-001",
            title = "Lot Rouleaux Tissus Wax Véritable Hollandais 6 Yards (Qualité Super)",
            category = "Tissus & Textiles",
            originCountry = "Côte d'Ivoire",
            originCountryFlag = "🇨🇮",
            supplierName = "Établissements Bamba & Frères - Grand Marché d'Adjamé",
            isVerifiedSupplier = true,
            rccmNumber = "CI-ABJ-2018-B-14529 (RCCM Vérifié)",
            whatsappNumber = "2250708091011",
            vendorPhone = "+225 07 08 09 10 11",
            rating = 4.9,
            totalOrders = 342,
            minOrderQty = 5,
            inStockQty = 1850,
            tiers = listOf(
                PriceTier(5, 19, 13500),
                PriceTier(20, 49, 11200),
                PriceTier(50, null, 9400)
            ),
            shippingModes = listOf(
                ShippingTransitMode.INTERSTATE_ROAD,
                ShippingTransitMode.AIR_FREIGHT,
                ShippingTransitMode.SEA_FREIGHT
            ),
            groupBuy = GroupBuyCampaign(
                campaignId = "GB-WAX-88",
                targetQty = 100,
                currentQty = 76,
                discountPercent = 25,
                hoursRemaining = 14
            ),
            description = "Tissu Wax 100% coton peigné haute densité, teintures végétales inaltérables au lavage. Échantillons expédiables par moto sous 24h. Idéal pour ateliers de couture, boutiques de prêt-à-porter et revendeurs grossistes sous-régionaux.",
            specs = mapOf(
                "Matière" to "100% Coton peigné",
                "Longueur pièce" to "6 Yards (~5.48 mètres)",
                "Poids au yard" to "180 g/m²",
                "Conditionnement" to "Balles cerclées de 20 pièces étanches"
            ),
            drawableResId = R.drawable.hero_trade_banner_1789597858056,
            badge = "🔥 Achat Groupé Actif"
        ),
        Product(
            id = "PROD-002",
            title = "Kit Solaire Hybride 5kVA Onduleur Pur Sinus + 4 Batteries Lithium LiFePO4",
            category = "Énergie & Solaire",
            originCountry = "Sénégal",
            originCountryFlag = "🇸🇳",
            supplierName = "AfriqSun Tech Industrial - Zone Franche de Diamniadio",
            isVerifiedSupplier = true,
            rccmNumber = "SN-DKR-2020-B-87102 (RCCM Vérifié)",
            whatsappNumber = "221776543210",
            vendorPhone = "+221 77 654 32 10",
            rating = 4.8,
            totalOrders = 129,
            minOrderQty = 1,
            inStockQty = 65,
            tiers = listOf(
                PriceTier(1, 2, 1450000),
                PriceTier(3, 9, 1280000),
                PriceTier(10, null, 1150000)
            ),
            shippingModes = listOf(
                ShippingTransitMode.INTERSTATE_ROAD,
                ShippingTransitMode.SEA_FREIGHT
            ),
            groupBuy = null,
            description = "Solution robuste anti-coupures et délestage pour PME, boulangeries, centres de santé et résidences. Onduleur tout-en-un avec régulateur MPPT 80A intégré, garanti 3 ans constructeur.",
            specs = mapOf(
                "Puissance nominale" to "5000W / 48V",
                "Technologie batterie" to "Lithium LiFePO4 6000 cycles",
                "Régulateur" to "MPPT 120-450VDC",
                "Certification" to "CE, RoHS, IEC 62109"
            ),
            drawableResId = R.drawable.group_buy_banner_1789597870087,
            badge = "⚡ Sécurisé Escrow"
        ),
        Product(
            id = "PROD-003",
            title = "Sacs de Cacao Fèves Premium Grade 1 Fermentées & Séchées (Sac 65kg)",
            category = "Agroalimentaire & Matières Premières",
            originCountry = "Cameroun",
            originCountryFlag = "🇨🇲",
            supplierName = "Coopérative Agro-Pastorale du Nkam & Moungo",
            isVerifiedSupplier = true,
            rccmNumber = "RC/DLA/2019/B/5541 (Coopérative Agréée)",
            whatsappNumber = "237677112233",
            vendorPhone = "+237 677 11 22 33",
            rating = 4.95,
            totalOrders = 512,
            minOrderQty = 10,
            inStockQty = 1400,
            tiers = listOf(
                PriceTier(10, 49, 145000),
                PriceTier(50, 199, 132000),
                PriceTier(200, null, 121000)
            ),
            shippingModes = listOf(
                ShippingTransitMode.INTERSTATE_ROAD,
                ShippingTransitMode.SEA_FREIGHT
            ),
            groupBuy = GroupBuyCampaign(
                campaignId = "GB-CACAO-03",
                targetQty = 200,
                currentQty = 168,
                discountPercent = 18,
                hoursRemaining = 28
            ),
            description = "Cacao de terroir camerounais, fèves rouges bien fermentées, taux d'humidité inférieur à 7.5%, zéro moisissure. Certificat phytosanitaire et traçabilité coopérative fournis.",
            specs = mapOf(
                "Humidité" to "≤ 7.5%",
                "Taux de fèves défectueuses" to "< 3% (Grade 1)",
                "Conditionnement" to "Sacs en toile de jute naturelle 65kg",
                "Origine" to "Bassin de Kumba & Mbanga"
            ),
            drawableResId = R.drawable.hero_trade_banner_1789597858056,
            badge = "🌱 Direct Coopérative"
        ),
        Product(
            id = "PROD-004",
            title = "Carton de 50 Smartphones Dual SIM 4G Android Go Edition (Batterie 5000mAh)",
            category = "Électronique & Télécom",
            originCountry = "Nigéria",
            originCountryFlag = "🇳🇬",
            supplierName = "Computer Village Wholesale Hub Ikeja - Lagos",
            isVerifiedSupplier = true,
            rccmNumber = "RC-998823-NG (Vérifié CAC Nigéria)",
            whatsappNumber = "2348031234567",
            vendorPhone = "+234 803 123 4567",
            rating = 4.7,
            totalOrders = 210,
            minOrderQty = 2, // Cartons
            inStockQty = 180,
            tiers = listOf(
                PriceTier(2, 4, 1850000), // ~37,000 FCFA par tel
                PriceTier(5, 14, 1650000), // ~33,000 FCFA
                PriceTier(15, null, 1450000) // ~29,000 FCFA
            ),
            shippingModes = listOf(
                ShippingTransitMode.AIR_FREIGHT,
                ShippingTransitMode.INTERSTATE_ROAD
            ),
            groupBuy = null,
            description = "Téléphones d'entrée de gamme optimisés pour le Mobile Money, WhatsApp et les zones rurales à faible réseau. Livré avec film de protection et coque antichoc déjà installés.",
            specs = mapOf(
                "Écran" to "6.5 pouces HD+ Waterdrop",
                "Batterie" to "5000 mAh autonomie 3 jours",
                "Réseau" to "4G LTE Dual SIM + MicroSD",
                "Garantie" to "12 mois échange standard sous 48h"
            ),
            drawableResId = R.drawable.group_buy_banner_1789597870087,
            badge = "📱 Best-Seller PME"
        ),
        Product(
            id = "PROD-005",
            title = "Balles 45kg Vêtements Friperie Crème 1er Choix (Robes & Chemises Hommes)",
            category = "Mode & Friperie en Gros",
            originCountry = "Kenya",
            originCountryFlag = "🇰🇪",
            supplierName = "Mombasa Port Logistics & Textile Depot",
            isVerifiedSupplier = true,
            rccmNumber = "KE-MOM-441098 (Certifié Agréé)",
            whatsappNumber = "254722998877",
            vendorPhone = "+254 722 99 88 77",
            rating = 4.85,
            totalOrders = 670,
            minOrderQty = 3,
            inStockQty = 340,
            tiers = listOf(
                PriceTier(3, 9, 88000),
                PriceTier(10, 24, 76000),
                PriceTier(25, null, 65000)
            ),
            shippingModes = listOf(
                ShippingTransitMode.SEA_FREIGHT,
                ShippingTransitMode.INTERSTATE_ROAD
            ),
            groupBuy = GroupBuyCampaign(
                campaignId = "GB-FRIP-09",
                targetQty = 50,
                currentQty = 42,
                discountPercent = 20,
                hoursRemaining = 8
            ),
            description = "Balles originales étiquetées Crème (zéro déchet, pièces modernes sans taches ni trous). Rentabilité prouvée pour marchands ambulants et boutiques de quartier.",
            specs = mapOf(
                "Poids par balle" to "45 kg scellée",
                "Nombre approximatif de pièces" to "160 - 200 pièces / balle",
                "Origine tri" to "Europe du Nord / Canada",
                "Tri qualité" to "Grade A Extra (Crème)"
            ),
            drawableResId = R.drawable.hero_trade_banner_1789597858056,
            badge = "⭐ Super Rentabilité"
        )
    )

    private val initialOrders = listOf(
        EscrowOrder(
            orderId = "CMD-AFR-8902",
            product = initialProducts[0],
            quantity = 25,
            unitPriceXof = 11200,
            shippingCostXof = 4800,
            totalAmountXof = (25 * 11200) + 4800L,
            paymentProvider = PaymentProvider.WAVE,
            phoneNumber = "+221 77 123 45 67",
            escrowStatus = EscrowStatus.ARRIVED_RELAY,
            deliveryType = DeliveryType.RELAY_POINT,
            relayPoint = availableRelayPoints[0],
            directAddressLandmark = null,
            escrowOtpCode = "8942",
            createdAt = "Hier, à 14h20",
            trackingSteps = listOf(
                TrackingStep("Paiement Sécurisé sous Séquestre", "284,800 FCFA retenus sur le compte séquestre AfriBaba", "Hier 14:21", true, false),
                TrackingStep("Commande Expédiée par le Vendeur", "Colis remis au transporteur routier inter-états", "Hier 18:30", true, false),
                TrackingStep("Arrivé au Point Relais", "Disponible au Relais Boutique Al-Amine (Marché Sandaga)", "Aujourd'hui 10:15", true, true),
                TrackingStep("Inspection et Déblocage Séquestre", "Inspectez le wax avant de donner votre OTP 8942 au relais", "En attente", false, false)
            )
        ),
        EscrowOrder(
            orderId = "CMD-AFR-8744",
            product = initialProducts[2],
            quantity = 15,
            unitPriceXof = 145000,
            shippingCostXof = 12000,
            totalAmountXof = (15 * 145000) + 12000L,
            paymentProvider = PaymentProvider.MTN_MOMO,
            phoneNumber = "+237 690 99 88 77",
            escrowStatus = EscrowStatus.IN_TRANSIT,
            deliveryType = DeliveryType.MOTO_DIRECT,
            relayPoint = null,
            directAddressLandmark = "Akwa Douala, face Boulangerie Zepol, demander boutique Paul",
            escrowOtpCode = "3109",
            createdAt = "15 Sep 2026",
            trackingSteps = listOf(
                TrackingStep("Paiement Sécurisé sous Séquestre", "Montant bloqué sur compte séquestre garanti", "15 Sep 09:12", true, false),
                TrackingStep("Colis Pris en Charge", "Convoyeur routier en route vers Douala", "15 Sep 16:00", true, true),
                TrackingStep("Remise au Livreur Moto", "Dispatch local pour livraison directe", "En attente", false, false),
                TrackingStep("Livraison & Validation OTP", "Paiement libéré après pesée des fèves", "En attente", false, false)
            )
        )
    )

    private val _products = MutableStateFlow(initialProducts)
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _orders = MutableStateFlow(initialOrders)
    val orders: StateFlow<List<EscrowOrder>> = _orders.asStateFlow()

    private val _currentCurrency = MutableStateFlow(AppCurrency.XOF)
    val currentCurrency: StateFlow<AppCurrency> = _currentCurrency.asStateFlow()

    private val _selectedCountryFilter = MutableStateFlow("ALL")
    val selectedCountryFilter: StateFlow<String> = _selectedCountryFilter.asStateFlow()

    private val _isLowDataMode = MutableStateFlow(false)
    val isLowDataMode: StateFlow<Boolean> = _isLowDataMode.asStateFlow()

    fun setCurrency(currency: AppCurrency) {
        _currentCurrency.value = currency
    }

    fun setCountryFilter(countryCode: String) {
        _selectedCountryFilter.value = countryCode
    }

    fun toggleLowDataMode() {
        _isLowDataMode.value = !_isLowDataMode.value
    }

    fun getProductById(id: String): Product? {
        return _products.value.find { it.id == id }
    }

    fun createOrder(
        product: Product,
        quantity: Int,
        shippingMode: ShippingTransitMode,
        paymentProvider: PaymentProvider,
        phoneNumber: String,
        deliveryType: DeliveryType,
        relayPoint: RelayPoint?,
        directAddress: String?
    ): EscrowOrder {
        val tier = product.getTierForQty(quantity)
        val unitPrice = tier.unitPriceXof
        val shippingCost = shippingMode.basePriceXof
        val total = (unitPrice * quantity) + shippingCost

        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val currentTime = sdf.format(Date())
        val generatedOtp = ((Math.random() * 9000).toInt() + 1000).toString()

        val newOrder = EscrowOrder(
            orderId = "CMD-AFR-${(1000..9999).random()}",
            product = product,
            quantity = quantity,
            unitPriceXof = unitPrice,
            shippingCostXof = shippingCost,
            totalAmountXof = total,
            paymentProvider = paymentProvider,
            phoneNumber = phoneNumber,
            escrowStatus = EscrowStatus.ESCROW_LOCKED,
            deliveryType = deliveryType,
            relayPoint = relayPoint,
            directAddressLandmark = directAddress,
            escrowOtpCode = generatedOtp,
            createdAt = "Aujourd'hui $currentTime",
            trackingSteps = listOf(
                TrackingStep("Paiement Reçu & Séquestre Bloqué", "Les fonds sont sécurisés par AfriBaba. Le vendeur ne sera payé qu'après validation de votre commande.", "Aujourd'hui $currentTime", true, true),
                TrackingStep("Préparation Fournisseur", "Emballage et étiquetage sécurisé au dépôt", "À venir", false, false),
                TrackingStep("Acheminement (${shippingMode.title})", "Délai estimé : ${shippingMode.delayText}", "À venir", false, false),
                TrackingStep("Livraison & Déblocage Séquestre", "Donnez le code OTP $generatedOtp uniquement après avoir vérifié la marchandise !", "À venir", false, false)
            )
        )

        _orders.value = listOf(newOrder) + _orders.value
        return newOrder
    }

    fun releaseEscrowByOtp(orderId: String, otpInput: String): Boolean {
        val currentOrders = _orders.value.toMutableList()
        val index = currentOrders.indexOfFirst { it.orderId == orderId }
        if (index != -1) {
            val order = currentOrders[index]
            if (order.escrowOtpCode == otpInput.trim()) {
                val updatedSteps = order.trackingSteps.map {
                    it.copy(isCompleted = true, isCurrent = false)
                } + listOf(
                    TrackingStep("Séquestre Débloqué avec Succès", "Marchandise conforme inspectée. Fonds virés instantanément sur le compte Mobile Money du vendeur !", "À l'instant", true, true)
                )

                currentOrders[index] = order.copy(
                    escrowStatus = EscrowStatus.RELEASED_TO_VENDOR,
                    trackingSteps = updatedSteps
                )
                _orders.value = currentOrders
                return true
            }
        }
        return false
    }

    fun joinGroupBuy(productId: String, addedUnits: Int) {
        val updated = _products.value.map { prod ->
            if (prod.id == productId && prod.groupBuy != null) {
                val newQty = (prod.groupBuy.currentQty + addedUnits).coerceAtMost(prod.groupBuy.targetQty)
                prod.copy(groupBuy = prod.groupBuy.copy(currentQty = newQty))
            } else prod
        }
        _products.value = updated
    }

    fun addVendorProduct(
        title: String,
        category: String,
        basePriceXof: Long,
        minOrderQty: Int,
        stockQty: Int,
        country: String,
        phone: String
    ) {
        val newProd = Product(
            id = "PROD-${(600..999).random()}",
            title = title,
            category = category,
            originCountry = country,
            originCountryFlag = availableCountries.find { it.name == country }?.flagEmoji ?: "🌍",
            supplierName = "Mon Entreprise Vendeur Certifiée",
            isVerifiedSupplier = true,
            rccmNumber = "RCCM-EN-COURS-2026",
            whatsappNumber = phone.replace("+", "").replace(" ", ""),
            vendorPhone = phone,
            rating = 5.0,
            totalOrders = 0,
            minOrderQty = minOrderQty,
            inStockQty = stockQty,
            tiers = listOf(
                PriceTier(minOrderQty, minOrderQty * 4, basePriceXof),
                PriceTier(minOrderQty * 4 + 1, null, (basePriceXof * 0.85).toLong())
            ),
            shippingModes = listOf(
                ShippingTransitMode.INTERSTATE_ROAD,
                ShippingTransitMode.AIR_FREIGHT
            ),
            groupBuy = null,
            description = "Produit ajouté depuis l'application mobile vendeur AfriBaba.",
            specs = mapOf("Origine" to country, "Stock initial" to "$stockQty unités"),
            drawableResId = R.drawable.hero_trade_banner_1789597858056,
            badge = "✨ Nouveau Vendeur"
        )
        _products.value = listOf(newProd) + _products.value
    }
}
