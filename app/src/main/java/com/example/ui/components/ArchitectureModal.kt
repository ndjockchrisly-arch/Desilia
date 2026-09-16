package com.example.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.*

@Composable
fun ArchitectureAndSpecsDialog(onDismiss: () -> Unit) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("1. Stack Réseau Faible", "2. Schéma BDD SQL", "3. Workflow MoMo & Webhook")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .padding(vertical = 12.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DarkSlate)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Hub,
                        contentDescription = null,
                        tint = SaffronGold,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "AfriBaba Architecture & Specs",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "Dossier Technique & Modélisation Afrique Subsaharienne",
                            color = SaffronGold,
                            fontSize = 10.sp
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Fermer", tint = Color.White)
                    }
                }

                // Tab Row
                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color(0xFF1E293B),
                    contentColor = Color.White,
                    edgePadding = 8.dp,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = SaffronGold
                        )
                    }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 12.sp,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTab == index) SaffronGold else Color(0xFF94A3B8)
                                )
                            }
                        )
                    }
                }

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    when (selectedTab) {
                        0 -> ArchitectureStackView()
                        1 -> DatabaseSchemaView()
                        2 -> MobileMoneyWorkflowView()
                    }
                }
            }
        }
    }
}

@Composable
private fun ArchitectureStackView() {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        SpecCard(
            title = "A. Mobile-First & Réseaux Faibles (2G / 3G / Edge)",
            content = """
• Client Mobile : Kotlin Jetpack Compose (ou Flutter/React Native) compilé en binaire natif optimisé (<15 MB APK).
• Mode Hors-Ligne & Cache : Room Database (SQLite locale chiffrée SQLCipher) avec synchronisation différentielle (Delta Sync) lors de la reconnexion.
• Optimisation Médias & Données :
  - WebP haute compression adaptative selon la qualité réseau (Bande passante détectée via NetworkCapabilities).
  - Mode Éco Données activable à 1 clic : désactivation des vignettes lourdes, chargement des fiches produits en JSON ultra-léger (<5 KB par produit).
  - Pré-chargement des fiches de contacts WhatsApp et adresses de relais hors-ligne.
            """.trimIndent()
        )

        SpecCard(
            title = "B. Backend & Scalabilité",
            content = """
• Runtime & Langage : Node.js (NestJS / TypeScript) ou Go (Golang) pour un débit élevé et une faible consommation mémoire.
• Caching & File d'attente : Redis Cluster pour le verrouillage distribué des stocks et la rétention temporaire des états de séquestre (TTL).
• Message Broker : RabbitMQ / Apache Kafka pour le traitement asynchrone des notifications Push USSD et des webhooks opérateurs.
• Hébergement & CDN : Edge Workers Cloudflare avec serveurs de cache régionaux (Lagos, Nairobi, Johannesburg, Abidjan).
            """.trimIndent()
        )

        SpecCard(
            title = "C. Canaux de Communication Hybrides",
            content = """
• WhatsApp Business Cloud API : Envoi automatisé des reçus de commande, géolocalisation de points relais et déclenchement d'échanges acheteur-vendeur.
• SMS & USSD Fallback : Passerelle SMS locale (Infobip / Africa's Talking) pour délivrer le code OTP d'inspection même sans connexion Internet mobile.
            """.trimIndent()
        )
    }
}

@Composable
private fun DatabaseSchemaView() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Schéma Relationnel PostgreSQL / PostGIS",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface
        )

        CodeBlockView(
            code = """
-- 1. Table Utilisateurs (Acheteurs, Grossistes, Livreurs)
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    full_name VARCHAR(150) NOT NULL,
    phone_number VARCHAR(25) UNIQUE NOT NULL, -- Ex: +2250708091011
    whatsapp_number VARCHAR(25),
    role VARCHAR(20) CHECK (role IN ('BUYER', 'VENDOR', 'RIDER', 'ADMIN')),
    country_code CHAR(2) NOT NULL, -- SN, CI, CM, NG, KE
    currency_pref VARCHAR(5) DEFAULT 'XOF',
    is_verified BOOLEAN DEFAULT FALSE,
    rccm_number VARCHAR(50), -- Registre du commerce pour grossistes
    rating NUMERIC(3, 2) DEFAULT 5.00,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 2. Table Produits & Paliers Grossistes (B2B Tiers)
CREATE TABLE products (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    vendor_id UUID REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    category VARCHAR(80) NOT NULL,
    description TEXT,
    origin_country CHAR(2) NOT NULL,
    base_price_xof BIGINT NOT NULL,
    min_order_qty INT DEFAULT 1,
    in_stock_qty INT NOT NULL,
    price_tiers_json JSONB NOT NULL, -- [{"min": 5, "max": 19, "price": 13500}, {"min": 20, "price": 11200}]
    shipping_modes_json JSONB NOT NULL, -- ['ROAD', 'AIR', 'SEA']
    is_group_buy_eligible BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 3. Table Groupement d'Achats (Achats Groupés PME)
CREATE TABLE group_buys (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id UUID REFERENCES products(id),
    target_qty INT NOT NULL,
    current_qty INT DEFAULT 0,
    discount_percent INT NOT NULL,
    deadline TIMESTAMP WITH TIME ZONE NOT NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'UNLOCKED', 'EXPIRED'))
);

-- 4. Table Commandes & Séquestre (Escrow)
CREATE TABLE orders (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_code VARCHAR(30) UNIQUE NOT NULL, -- CMD-AFR-XXXX
    buyer_id UUID REFERENCES users(id),
    vendor_id UUID REFERENCES users(id),
    total_amount_xof BIGINT NOT NULL,
    shipping_cost_xof BIGINT NOT NULL,
    escrow_status VARCHAR(30) DEFAULT 'ESCROW_LOCKED' 
        CHECK (escrow_status IN ('INITIATED', 'ESCROW_LOCKED', 'IN_TRANSIT', 'ARRIVED_RELAY', 'RELEASED_TO_VENDOR', 'DISPUTED')),
    delivery_type VARCHAR(20) CHECK (delivery_type IN ('RELAY_POINT', 'MOTO_DIRECT')),
    relay_point_id VARCHAR(50),
    direct_address_landmark TEXT,
    escrow_otp_hash VARCHAR(100) NOT NULL, -- Code OTP à 4 chiffres haché
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 5. Table Transactions Mobile Money & Webhooks
CREATE TABLE mobile_money_transactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id UUID REFERENCES orders(id),
    provider VARCHAR(20) CHECK (provider IN ('MTN_MOMO', 'ORANGE_MONEY', 'WAVE', 'MPESA', 'CASH')),
    payer_phone VARCHAR(25) NOT NULL,
    amount_xof BIGINT NOT NULL,
    operator_reference VARCHAR(100),
    status VARCHAR(30) DEFAULT 'PENDING_USER_PIN' 
        CHECK (status IN ('PENDING_USER_PIN', 'SUCCESS_ESCROW_LOCKED', 'RELEASED_TO_SELLER', 'FAILED', 'REFUNDED')),
    webhook_payload JSONB,
    idempotency_key VARCHAR(100) UNIQUE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 6. Table Points Relais & Géolocalisation
CREATE TABLE relay_points (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    city VARCHAR(80) NOT NULL,
    district VARCHAR(100) NOT NULL,
    landmark TEXT NOT NULL,
    contact_phone VARCHAR(25) NOT NULL,
    location GEOGRAPHY(Point, 4326), -- PostGIS GPS Point
    opening_hours VARCHAR(100)
);
            """.trimIndent()
        )
    }
}

@Composable
private fun MobileMoneyWorkflowView() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SpecCard(
            title = "Cycle de Vie du Paiement Séquestre (Escrow Logic)",
            content = """
1. INITIATION DE PAIEMENT (Client) :
   • Le client saisit son numéro Mobile Money (Wave, Orange, MTN, M-Pesa).
   • Le backend génère une clé d'idempotence unique et appelle l'API de l'agrégateur (ou direct Telco API) :
     - MTN MoMo : Collection RequestToPay POST /collection/v1_0/requesttopay
     - Orange Money : Web Payment API ou API Merchant Payment Push
     - Wave : API Checkout Session / Instant Push Notification
     - M-Pesa : Daraja STK Push (Lipa na M-Pesa Online)

2. PROMPT USSD & DEBIT TÉLÉPHONE :
   • Une invite USSD ou push s'affiche sur le téléphone de l'acheteur.
   • L'acheteur entre son code PIN secret. L'opérateur valide la transaction.

3. WEBHOOK ASYNCHRONE DE CONFIRMATION (Opérateur -> AfriBaba) :
   • Le serveur Telco envoie une requête POST sécurisée par signature HMAC-SHA256 à notre endpoint webhook.
   • AfriBaba valide l'idempotence, déduit la commission plateforme (1.5%), et place les fonds sur le compte Séquestre Tiers (Escrow).
   • Un code OTP unique à 4 chiffres est envoyé par SMS à l'acheteur.

4. EXPÉDITION & SUIVI MULTI-ÉTAPES :
   • Le vendeur prépare la marchandise et la dépose au Point Relais ou la remet au livreur moto indépendant.
   • La commande passe au statut 'IN_TRANSIT'.

5. INSPECTION PHYSIQUE & DÉBLOCAGE :
   • Le client se rend au Point Relais ou accueille le livreur moto.
   • Il ouvre le colis et inspecte la marchandise (qualité des rouleaux de wax, fèves de cacao, équipement solaire).
   • SI CONFORME : L'acheteur fournit son code OTP. Le système débloque instantanément les fonds vers le Mobile Money du vendeur !
   • SI NON-CONFORME : Ouverture d'un litige. Les fonds restent sécurisés jusqu'à arbitrage ou retour marchandise avec remboursement intégral.
            """.trimIndent()
        )

        CodeBlockView(
            code = """
// EXEMPLE DE HANDLER WEBHOOK (Node.js / Express / TypeScript)
app.post('/api/v1/payments/webhook', async (req, res) => {
  const signature = req.headers['x-operator-signature'];
  const { event_type, transaction_ref, operator_tx_id, status, amount } = req.body;

  // 1. Vérification de la signature cryptographique HMAC
  if (!verifySignature(req.rawBody, signature, OPERATOR_SECRET)) {
    return res.status(401).send({ error: 'Signature invalide' });
  }

  // 2. Gestion de l'idempotence (éviter les doubles crédits)
  const existingTx = await db.mobile_money_transactions.findOne({
    where: { operator_reference: transaction_ref }
  });
  if (existingTx && existingTx.status === 'SUCCESS_ESCROW_LOCKED') {
    return res.status(200).send({ message: 'Déjà traité' });
  }

  if (status === 'SUCCESSFUL') {
    // 3. Verrouillage des fonds sous Séquestre (Escrow)
    await db.orders.update({
      where: { id: existingTx.order_id },
      data: { escrow_status: 'ESCROW_LOCKED' }
    });

    // 4. Génération OTP et envoi SMS sécurisé à l'acheteur
    const otp = generateSecureOtp();
    await sendSms(existingTx.payer_phone, `AfriBaba: Paiement de ${'$'}{amount} FCFA sécurisé sous séquestre. Votre code OTP d'inspection est: ${'$'}{otp}. Ne le communiquez qu'après vérification du colis !`);

    // 5. Notification au Vendeur pour expédier
    await notifyVendorOrderReady(existingTx.order_id);
  }

  return res.status(200).send({ received: true });
});
            """.trimIndent()
        )
    }
}

@Composable
private fun SpecCard(title: String, content: String) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF8FAFC),
        border = BorderStroke(1.dp, CardBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = TerracottaPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = content,
                fontSize = 11.5.sp,
                color = CharcoalGray,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
private fun CodeBlockView(code: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFF0F172A),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = code,
            color = Color(0xFFE2E8F0),
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier
                .padding(12.dp)
                .horizontalScroll(rememberScrollState()),
            lineHeight = 14.sp
        )
    }
}
