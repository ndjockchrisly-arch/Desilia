import React, { useState } from 'react';
import { 
  ShoppingBag, Users, Truck, Store, Search, ShieldCheck, 
  MapPin, Phone, MessageSquare, CheckCircle, Clock, 
  ChevronRight, ArrowLeft, Globe, WifiOff, AlertTriangle, Package
} from 'lucide-react';

interface PriceTier {
  minQty: number;
  maxQty: number | null;
  unitPriceXOF: number;
}

interface Product {
  id: string;
  title: string;
  category: string;
  originCountry: string;
  originCountryFlag: string;
  supplierName: string;
  isVerifiedSupplier: boolean;
  rccmNumber: string;
  whatsappNumber: string;
  vendorPhone: string;
  rating: number;
  totalOrders: number;
  minOrderQty: number;
  inStockQty: number;
  tiers: PriceTier[];
  shippingModes: string[];
  description: string;
  specs: Record<string, string>;
  groupBuy?: {
    campaignId: string;
    targetQty: number;
    currentQty: number;
    discountPercent: number;
    hoursRemaining: number;
  };
}

const PRODUCTS: Product[] = [
  {
    id: 'PROD-001',
    title: 'Lot Rouleaux Tissus Wax Véritable Hollandais 6 Yards (Qualité Super)',
    category: 'Tissus & Textiles',
    originCountry: "Côte d'Ivoire",
    originCountryFlag: '🇨🇮',
    supplierName: "Établissements Bamba & Frères - Grand Marché d'Adjamé",
    isVerifiedSupplier: true,
    rccmNumber: 'CI-ABJ-2018-B-14529 (RCCM Vérifié)',
    whatsappNumber: '2250708091011',
    vendorPhone: '+225 07 08 09 10 11',
    rating: 4.9,
    totalOrders: 342,
    minOrderQty: 5,
    inStockQty: 1850,
    tiers: [
      { minQty: 5, maxQty: 19, unitPriceXOF: 13500 },
      { minQty: 20, maxQty: 49, unitPriceXOF: 11200 },
      { minQty: 50, maxQty: null, unitPriceXOF: 9400 },
    ],
    shippingModes: ['Routier Sous-Régional (Inter-États)', 'Fret Aérien Express (24h-48h)', 'Fret Maritime Groupé (LCL)'],
    description: 'Tissu Wax 100% coton peigné haute densité, teintures végétales inaltérables au lavage. Échantillons expédiables sous 24h. Idéal pour ateliers et revendeurs grossistes sous-régionaux.',
    specs: {
      'Matière': '100% Coton peigné',
      'Longueur pièce': '6 Yards (~5.48 mètres)',
      'Poids au yard': '180 g/m²',
    },
    groupBuy: {
      campaignId: 'GB-WAX-88',
      targetQty: 100,
      currentQty: 76,
      discountPercent: 25,
      hoursRemaining: 14,
    }
  },
  {
    id: 'PROD-002',
    title: 'Générateurs Solaires Hybrides 1000W + 2 Panneaux Monocristallins (B2B)',
    category: 'Énergie & Solaire',
    originCountry: 'Sénégal',
    originCountryFlag: '🇸🇳',
    supplierName: 'Sahel Green Energy SARL - Zone Industrielle de Dakar',
    isVerifiedSupplier: true,
    rccmNumber: 'SN-DKR-2020-B-8831',
    whatsappNumber: '221774567890',
    vendorPhone: '+221 77 456 78 90',
    rating: 4.8,
    totalOrders: 189,
    minOrderQty: 2,
    inStockQty: 140,
    tiers: [
      { minQty: 2, maxQty: 5, unitPriceXOF: 245000 },
      { minQty: 6, maxQty: 15, unitPriceXOF: 215000 },
      { minQty: 16, maxQty: null, unitPriceXOF: 189000 },
    ],
    shippingModes: ['Routier Sous-Régional (Inter-États)', 'Fret Maritime Groupé (LCL)'],
    description: 'Station d’énergie tout-en-un avec batterie LiFePO4 1280Wh. Idéal pour alimenter commerces, officines et cliniques en zone à coupures récurrentes.',
    specs: {
      'Capacité batterie': '1280Wh (LiFePO4)',
      'Onduleur': '1000W Pur Sinus',
      'Garantie constructeur': '2 ans avec SAV local',
    },
    groupBuy: {
      campaignId: 'GB-SOLAR-02',
      targetQty: 20,
      currentQty: 14,
      discountPercent: 18,
      hoursRemaining: 36,
    }
  },
  {
    id: 'PROD-003',
    title: 'Kits Équipements Médicaux & Tensiomètres Électroniques Homologués (Lots Pro)',
    category: 'Santé & Équipements',
    originCountry: 'Cameroun',
    originCountryFlag: '🇨🇲',
    supplierName: 'PharmaDispo 237 & Médical Logistics Douala',
    isVerifiedSupplier: true,
    rccmNumber: 'RC-DLA-2021-B-3104',
    whatsappNumber: '237699301122',
    vendorPhone: '+237 699 30 11 22',
    rating: 4.95,
    totalOrders: 512,
    minOrderQty: 10,
    inStockQty: 3200,
    tiers: [
      { minQty: 10, maxQty: 29, unitPriceXOF: 14500 },
      { minQty: 30, maxQty: 99, unitPriceXOF: 11800 },
      { minQty: 100, maxQty: null, unitPriceXOF: 9200 },
    ],
    shippingModes: ['Routier Sous-Régional (Inter-États)', 'Fret Aérien Express (24h-48h)'],
    description: 'Matériel certifié CE/ISO pour officines, pharmacies et centres de santé. Normes CEMAC respectées, calibrage professionnel et support technique direct.',
    specs: {
      'Précision mesure': '±3 mmHg',
      'Alimentation': 'USB-C & Piles AA',
      'Certification': 'ISO 13485 & CE Médical',
    }
  }
];

export default function App() {
  const [activeTab, setActiveTab] = useState<'home' | 'groups' | 'tracking' | 'vendor'>('home');
  const [selectedProduct, setSelectedProduct] = useState<Product | null>(null);
  const [isCheckout, setIsCheckout] = useState(false);
  const [quantity, setQuantity] = useState(10);
  const [selectedPayment, setSelectedPayment] = useState('ORANGE_MONEY');
  const [lowDataMode, setLowDataMode] = useState(false);
  const [orderSuccess, setOrderSuccess] = useState(false);
  const [searchQuery, setSearchQuery] = useState('');

  const filteredProducts = PRODUCTS.filter(p => 
    p.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
    p.category.toLowerCase().includes(searchQuery.toLowerCase()) ||
    p.supplierName.toLowerCase().includes(searchQuery.toLowerCase())
  );

  const formatPrice = (price: number) => {
    return price.toLocaleString('fr-FR') + ' FCFA';
  };

  const calculateUnitPrice = (product: Product, qty: number) => {
    for (const tier of product.tiers) {
      if (qty >= tier.minQty && (tier.maxQty === null || qty <= tier.maxQty)) {
        return tier.unitPriceXOF;
      }
    }
    return product.tiers[0].unitPriceXOF;
  };

  return (
    <div style={{ maxWidth: 1024, margin: '0 auto', minHeight: '100vh', display: 'flex', flexDirection: 'column', backgroundColor: '#ffffff' }}>
      {/* Header */}
      <header style={{ backgroundColor: '#0f766e', color: '#ffffff', padding: '12px 16px', position: 'sticky', top: 0, zIndex: 50, boxShadow: '0 2px 4px rgba(0,0,0,0.1)' }}>
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 8 }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
            <span style={{ fontSize: 24, fontWeight: 800, letterSpacing: -0.5 }}>AfriBaba / PharmaDispo 237</span>
            <span style={{ backgroundColor: '#14b8a6', color: '#ffffff', fontSize: 10, padding: '2px 8px', borderRadius: 12, fontWeight: 700 }}>B2B & B2C</span>
          </div>
          <button 
            onClick={() => setLowDataMode(!lowDataMode)}
            style={{ display: 'flex', alignItems: 'center', gap: 4, backgroundColor: lowDataMode ? '#f59e0b' : 'rgba(255,255,255,0.2)', padding: '4px 10px', borderRadius: 16, color: '#fff', fontSize: 12, fontWeight: 600 }}
          >
            {lowDataMode ? <WifiOff size={14} /> : <Globe size={14} />}
            {lowDataMode ? 'Mode Éco (2G/3G)' : 'Standard'}
          </button>
        </div>

        {/* Search */}
        <div style={{ position: 'relative' }}>
          <input 
            type="text" 
            placeholder="Rechercher par produit, catégorie, grossiste..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            style={{ width: '100%', padding: '8px 12px 8px 36px', borderRadius: 8, border: 'none', outline: 'none', fontSize: 14, color: '#0f172a' }}
          />
          <Search size={18} style={{ position: 'absolute', left: 10, top: 9, color: '#64748b' }} />
        </div>
      </header>

      {/* Main Content Area */}
      <main style={{ flex: 1, padding: '16px', paddingBottom: '80px' }}>
        {/* Product Detail Modal */}
        {selectedProduct && !isCheckout && (
          <div style={{ backgroundColor: '#f8fafc', borderRadius: 12, border: '1px solid #e2e8f0', padding: 16, marginBottom: 16 }}>
            <button 
              onClick={() => setSelectedProduct(null)}
              style={{ display: 'flex', alignItems: 'center', gap: 6, color: '#0f766e', fontWeight: 600, fontSize: 14, marginBottom: 12 }}
            >
              <ArrowLeft size={16} /> Retour au catalogue
            </button>
            <div style={{ display: 'flex', gap: 8, alignItems: 'center', marginBottom: 6 }}>
              <span>{selectedProduct.originCountryFlag}</span>
              <span style={{ fontSize: 12, color: '#64748b', fontWeight: 600 }}>{selectedProduct.category}</span>
              {selectedProduct.isVerifiedSupplier && (
                <span style={{ display: 'flex', alignItems: 'center', gap: 4, fontSize: 11, backgroundColor: '#dcfce7', color: '#166534', padding: '2px 6px', borderRadius: 4, fontWeight: 700 }}>
                  <ShieldCheck size={12} /> {selectedProduct.rccmNumber}
                </span>
              )}
            </div>
            <h2 style={{ fontSize: 20, fontWeight: 700, marginBottom: 8 }}>{selectedProduct.title}</h2>
            <p style={{ fontSize: 14, color: '#475569', marginBottom: 16 }}>{selectedProduct.description}</p>

            {/* B2B Price Tiers */}
            <div style={{ backgroundColor: '#ffffff', borderRadius: 8, padding: 12, border: '1px solid #cbd5e1', marginBottom: 16 }}>
              <div style={{ fontSize: 13, fontWeight: 700, color: '#0f766e', marginBottom: 8 }}>Paliers de Prix Dégressifs Grossiste (XOF) :</div>
              <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(140px, 1fr))', gap: 8 }}>
                {selectedProduct.tiers.map((t, idx) => (
                  <div key={idx} style={{ backgroundColor: '#f1f5f9', padding: 8, borderRadius: 6, textAlign: 'center' }}>
                    <div style={{ fontSize: 11, color: '#64748b' }}>{t.maxQty ? `${t.minQty} - ${t.maxQty} pièces` : `${t.minQty}+ pièces`}</div>
                    <div style={{ fontSize: 15, fontWeight: 800, color: '#0f766e' }}>{formatPrice(t.unitPriceXOF)}</div>
                  </div>
                ))}
              </div>
            </div>

            {/* Quantity Selector */}
            <div style={{ display: 'flex', alignItems: 'center', gap: 12, marginBottom: 16 }}>
              <span style={{ fontSize: 14, fontWeight: 600 }}>Quantité :</span>
              <button onClick={() => setQuantity(Math.max(selectedProduct.minOrderQty, quantity - 5))} style={{ width: 32, height: 32, borderRadius: 6, backgroundColor: '#e2e8f0', fontWeight: 700 }}>-</button>
              <span style={{ fontSize: 16, fontWeight: 700, minWidth: 40, textAlign: 'center' }}>{quantity}</span>
              <button onClick={() => setQuantity(quantity + 5)} style={{ width: 32, height: 32, borderRadius: 6, backgroundColor: '#e2e8f0', fontWeight: 700 }}>+</button>
              <div style={{ marginLeft: 'auto', textAlign: 'right' }}>
                <div style={{ fontSize: 12, color: '#64748b' }}>Total Estimé :</div>
                <div style={{ fontSize: 18, fontWeight: 800, color: '#0f766e' }}>
                  {formatPrice(calculateUnitPrice(selectedProduct, quantity) * quantity)}
                </div>
              </div>
            </div>

            {/* Actions */}
            <div style={{ display: 'flex', gap: 8, flexWrap: 'wrap' }}>
              <button 
                onClick={() => setIsCheckout(true)}
                style={{ flex: 1, minWidth: 200, backgroundColor: '#0f766e', color: '#ffffff', padding: '12px 20px', borderRadius: 8, fontWeight: 700, display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 8 }}
              >
                <ShoppingBag size={18} /> Commander avec Séquestre (Escrow)
              </button>
              <a 
                href={`https://wa.me/${selectedProduct.whatsappNumber}?text=Bonjour%20je%20suis%20intéressé%20par%20votre%20lot%20${encodeURIComponent(selectedProduct.title)}`}
                target="_blank" 
                rel="noreferrer"
                style={{ display: 'flex', alignItems: 'center', gap: 6, backgroundColor: '#22c55e', color: '#ffffff', padding: '12px 16px', borderRadius: 8, fontWeight: 600, textDecoration: 'none', fontSize: 14 }}
              >
                <MessageSquare size={16} /> Négocier sur WhatsApp
              </a>
            </div>
          </div>
        )}

        {/* Checkout Modal */}
        {selectedProduct && isCheckout && (
          <div style={{ backgroundColor: '#ffffff', borderRadius: 12, border: '1px solid #cbd5e1', padding: 16 }}>
            <button 
              onClick={() => setIsCheckout(false)}
              style={{ display: 'flex', alignItems: 'center', gap: 6, color: '#0f766e', fontWeight: 600, fontSize: 14, marginBottom: 12 }}
            >
              <ArrowLeft size={16} /> Retour au produit
            </button>
            <h2 style={{ fontSize: 18, fontWeight: 700, marginBottom: 12 }}>Finalisation de la commande sécurisée</h2>

            {/* Escrow Guarantee Badge */}
            <div style={{ display: 'flex', alignItems: 'center', gap: 8, backgroundColor: '#f0fdf4', border: '1px solid #bbf7d0', padding: 12, borderRadius: 8, marginBottom: 16 }}>
              <ShieldCheck size={24} style={{ color: '#16a34a', flexShrink: 0 }} />
              <div style={{ fontSize: 13, color: '#166534' }}>
                <strong>Garantie Séquestre (Escrow) :</strong> Vos fonds restent bloqués en lieu sûr. Le vendeur n'est payé qu'après validation de la réception au point relais.
              </div>
            </div>

            {/* Payment Method */}
            <div style={{ marginBottom: 16 }}>
              <label style={{ display: 'block', fontSize: 14, fontWeight: 700, marginBottom: 8 }}>Moyen de paiement sécurisé :</label>
              <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(140px, 1fr))', gap: 8 }}>
                {[
                  { id: 'ORANGE_MONEY', name: 'Orange Money', color: '#ea580c' },
                  { id: 'MTN_MOMO', name: 'MTN MoMo', color: '#eab308' },
                  { id: 'WAVE', name: 'Wave Mobile', color: '#0284c7' },
                  { id: 'CASH_ESCROW', name: 'Paiement au Relais', color: '#16a34a' }
                ].map(method => (
                  <button
                    key={method.id}
                    onClick={() => setSelectedPayment(method.id)}
                    style={{
                      padding: 10,
                      borderRadius: 8,
                      border: selectedPayment === method.id ? `2px solid ${method.color}` : '1px solid #e2e8f0',
                      backgroundColor: selectedPayment === method.id ? '#f8fafc' : '#ffffff',
                      fontWeight: 600,
                      fontSize: 13,
                      textAlign: 'center'
                    }}
                  >
                    {method.name}
                  </button>
                ))}
              </div>
            </div>

            {/* Landmark address */}
            <div style={{ marginBottom: 16 }}>
              <label style={{ display: 'block', fontSize: 14, fontWeight: 700, marginBottom: 4 }}>Indication point de repère livraison :</label>
              <input 
                type="text" 
                defaultValue="Douala Akwa, face Pharmacie du Centre, porte bleue"
                style={{ width: '100%', padding: 8, borderRadius: 6, border: '1px solid #cbd5e1', fontSize: 13 }}
              />
            </div>

            {orderSuccess ? (
              <div style={{ textAlign: 'center', padding: 24, backgroundColor: '#f0fdf4', borderRadius: 8 }}>
                <CheckCircle size={48} style={{ color: '#16a34a', margin: '0 auto 12px' }} />
                <h3 style={{ fontSize: 18, fontWeight: 700, color: '#166534', marginBottom: 6 }}>Commande #AF-8921 Enregistrée !</h3>
                <p style={{ fontSize: 13, color: '#166534' }}>Fonds sécurisés en séquestre. Vous recevrez un SMS et un code OTP lors de l'arrivée au point relais.</p>
              </div>
            ) : (
              <button 
                onClick={() => setOrderSuccess(true)}
                style={{ width: '100%', backgroundColor: '#0f766e', color: '#ffffff', padding: '14px', borderRadius: 8, fontWeight: 700, fontSize: 15 }}
              >
                Payer {formatPrice(calculateUnitPrice(selectedProduct, quantity) * quantity)} & Bloquer en Séquestre
              </button>
            )}
          </div>
        )}

        {/* Tab 1: Catalogue / Home */}
        {activeTab === 'home' && !selectedProduct && (
          <div>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 12 }}>
              <h1 style={{ fontSize: 18, fontWeight: 800, color: '#0f172a' }}>Catalogue Produits B2B & Grossistes</h1>
              <span style={{ fontSize: 12, color: '#64748b' }}>{filteredProducts.length} lots disponibles</span>
            </div>

            <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
              {filteredProducts.map(product => {
                const basePrice = product.tiers[0].unitPriceXOF;
                const bestPrice = product.tiers[product.tiers.length - 1].unitPriceXOF;

                return (
                  <div 
                    key={product.id}
                    onClick={() => { setSelectedProduct(product); setQuantity(product.minOrderQty); setIsCheckout(false); setOrderSuccess(false); }}
                    style={{ backgroundColor: '#ffffff', borderRadius: 10, border: '1px solid #e2e8f0', padding: 14, cursor: 'pointer', transition: 'all 0.2s', boxShadow: '0 1px 3px rgba(0,0,0,0.05)' }}
                  >
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: 6 }}>
                      <div style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
                        <span>{product.originCountryFlag}</span>
                        <span style={{ fontSize: 11, color: '#64748b', fontWeight: 600 }}>{product.category}</span>
                      </div>
                      {product.isVerifiedSupplier && (
                        <span style={{ fontSize: 10, backgroundColor: '#dcfce7', color: '#166534', padding: '2px 6px', borderRadius: 4, fontWeight: 700 }}>
                          Vérifié
                        </span>
                      )}
                    </div>
                    <h3 style={{ fontSize: 15, fontWeight: 700, color: '#0f172a', marginBottom: 4 }}>{product.title}</h3>
                    <div style={{ fontSize: 12, color: '#475569', marginBottom: 8 }}>{product.supplierName}</div>
                    
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-end' }}>
                      <div>
                        <div style={{ fontSize: 11, color: '#64748b' }}>Min. {product.minOrderQty} pcs</div>
                        <div style={{ fontSize: 16, fontWeight: 800, color: '#0f766e' }}>
                          {formatPrice(bestPrice)} <span style={{ fontSize: 11, fontWeight: 400, color: '#64748b' }}>à {formatPrice(basePrice)}/u</span>
                        </div>
                      </div>
                      <span style={{ display: 'flex', alignItems: 'center', gap: 4, fontSize: 13, fontWeight: 600, color: '#0f766e' }}>
                        Détails <ChevronRight size={14} />
                      </span>
                    </div>
                  </div>
                );
              })}
            </div>
          </div>
        )}

        {/* Tab 2: Group Buying */}
        {activeTab === 'groups' && (
          <div>
            <h2 style={{ fontSize: 18, fontWeight: 800, marginBottom: 4 }}>Commandes Groupées (Group Buying)</h2>
            <p style={{ fontSize: 13, color: '#64748b', marginBottom: 16 }}>Regroupez vos volumes avec d'autres acheteurs pour débloquer le tarif usine maximal.</p>

            <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
              {PRODUCTS.filter(p => p.groupBuy).map(p => {
                const gb = p.groupBuy!;
                const percent = Math.round((gb.currentQty / gb.targetQty) * 100);

                return (
                  <div key={p.id} style={{ backgroundColor: '#ffffff', borderRadius: 10, border: '1px solid #e2e8f0', padding: 14 }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 6 }}>
                      <span style={{ fontSize: 12, fontWeight: 700, color: '#0f766e' }}>Campagne #{gb.campaignId}</span>
                      <span style={{ display: 'flex', alignItems: 'center', gap: 4, fontSize: 12, color: '#ea580c', fontWeight: 700 }}>
                        <Clock size={12} /> {gb.hoursRemaining}h restantes
                      </span>
                    </div>
                    <h3 style={{ fontSize: 15, fontWeight: 700, marginBottom: 4 }}>{p.title}</h3>
                    <div style={{ fontSize: 13, color: '#16a34a', fontWeight: 700, marginBottom: 8 }}>
                      -{gb.discountPercent}% sur le prix public
                    </div>

                    <div style={{ marginBottom: 8 }}>
                      <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: 11, color: '#64748b', marginBottom: 4 }}>
                        <span>Progression du lot : {gb.currentQty}/{gb.targetQty} pièces</span>
                        <span>{percent}%</span>
                      </div>
                      <div style={{ height: 8, backgroundColor: '#e2e8f0', borderRadius: 4, overflow: 'hidden' }}>
                        <div style={{ height: '100%', width: `${percent}%`, backgroundColor: '#0f766e', borderRadius: 4 }}></div>
                      </div>
                    </div>

                    <button 
                      onClick={() => { setSelectedProduct(p); setQuantity(5); setIsCheckout(true); }}
                      style={{ width: '100%', backgroundColor: '#0f766e', color: '#fff', padding: 10, borderRadius: 6, fontWeight: 600, fontSize: 13 }}
                    >
                      Rejoindre le groupe d'achat
                    </button>
                  </div>
                );
              })}
            </div>
          </div>
        )}

        {/* Tab 3: Tracking */}
        {activeTab === 'tracking' && (
          <div>
            <h2 style={{ fontSize: 18, fontWeight: 800, marginBottom: 12 }}>Suivi des Colis & Séquestre</h2>
            
            <div style={{ backgroundColor: '#ffffff', borderRadius: 10, border: '1px solid #e2e8f0', padding: 16, marginBottom: 12 }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 8 }}>
                <span style={{ fontSize: 14, fontWeight: 700 }}>Colis #AFR-2026-9041</span>
                <span style={{ fontSize: 12, color: '#0284c7', backgroundColor: '#e0f2fe', padding: '2px 8px', borderRadius: 12, fontWeight: 700 }}>En Transit Relais</span>
              </div>
              <div style={{ fontSize: 13, color: '#475569', marginBottom: 12 }}>
                Destination : <strong>Douala Akwa Express Hub (Face Camtel)</strong>
              </div>

              {/* Steps */}
              <div style={{ display: 'flex', flexDirection: 'column', gap: 10, borderLeft: '2px solid #cbd5e1', paddingLeft: 12, marginLeft: 6 }}>
                <div>
                  <div style={{ fontSize: 12, fontWeight: 700, color: '#16a34a' }}>✓ 1. Commande & Fonds Sécurisés en Séquestre</div>
                  <div style={{ fontSize: 11, color: '#64748b' }}>Paiement Orange Money validé</div>
                </div>
                <div>
                  <div style={{ fontSize: 12, fontWeight: 700, color: '#16a34a' }}>✓ 2. Expédition par le fournisseur</div>
                  <div style={{ fontSize: 11, color: '#64748b' }}>Prise en charge transporteur routier</div>
                </div>
                <div>
                  <div style={{ fontSize: 12, fontWeight: 700, color: '#0284c7' }}>● 3. En cours d'acheminement vers le Relais</div>
                  <div style={{ fontSize: 11, color: '#64748b' }}>Arrivée prévue sous 24h</div>
                </div>
                <div>
                  <div style={{ fontSize: 12, fontWeight: 700, color: '#94a3b8' }}>○ 4. Retrait & Libération des Fonds</div>
                  <div style={{ fontSize: 11, color: '#64748b' }}>Code OTP exigé pour débloquer le paiement au vendeur</div>
                </div>
              </div>
            </div>
          </div>
        )}

        {/* Tab 4: Vendor Dashboard */}
        {activeTab === 'vendor' && (
          <div>
            <h2 style={{ fontSize: 18, fontWeight: 800, marginBottom: 12 }}>Espace Vendeur & Grossiste</h2>

            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(130px, 1fr))', gap: 8, marginBottom: 16 }}>
              <div style={{ backgroundColor: '#ffffff', padding: 12, borderRadius: 8, border: '1px solid #e2e8f0', textAlign: 'center' }}>
                <div style={{ fontSize: 11, color: '#64748b' }}>Fonds en Séquestre</div>
                <div style={{ fontSize: 16, fontWeight: 800, color: '#0f766e' }}>845 000 F</div>
              </div>
              <div style={{ backgroundColor: '#ffffff', padding: 12, borderRadius: 8, border: '1px solid #e2e8f0', textAlign: 'center' }}>
                <div style={{ fontSize: 11, color: '#64748b' }}>Commandes actives</div>
                <div style={{ fontSize: 16, fontWeight: 800, color: '#0284c7' }}>18</div>
              </div>
              <div style={{ backgroundColor: '#ffffff', padding: 12, borderRadius: 8, border: '1px solid #e2e8f0', textAlign: 'center' }}>
                <div style={{ fontSize: 11, color: '#64748b' }}>Retrait Mobile Money</div>
                <div style={{ fontSize: 13, fontWeight: 700, color: '#16a34a', marginTop: 2 }}>Instant Wave/MoMo</div>
              </div>
            </div>
          </div>
        )}
      </main>

      {/* Bottom Navigation */}
      <nav style={{ position: 'fixed', bottom: 0, left: 0, right: 0, backgroundColor: '#ffffff', borderTop: '1px solid #e2e8f0', display: 'flex', justifyContent: 'space-around', padding: '8px 0', zIndex: 40 }}>
        <button 
          onClick={() => { setActiveTab('home'); setSelectedProduct(null); setIsCheckout(false); }}
          style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 2, color: activeTab === 'home' ? '#0f766e' : '#64748b', fontSize: 11, fontWeight: 600 }}
        >
          <ShoppingBag size={18} />
          <span>Accueil</span>
        </button>

        <button 
          onClick={() => { setActiveTab('groups'); setSelectedProduct(null); setIsCheckout(false); }}
          style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 2, color: activeTab === 'groups' ? '#0f766e' : '#64748b', fontSize: 11, fontWeight: 600 }}
        >
          <Users size={18} />
          <span>Groupes</span>
        </button>

        <button 
          onClick={() => { setActiveTab('tracking'); setSelectedProduct(null); setIsCheckout(false); }}
          style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 2, color: activeTab === 'tracking' ? '#0f766e' : '#64748b', fontSize: 11, fontWeight: 600 }}
        >
          <Truck size={18} />
          <span>Suivi Colis</span>
        </button>

        <button 
          onClick={() => { setActiveTab('vendor'); setSelectedProduct(null); setIsCheckout(false); }}
          style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 2, color: activeTab === 'vendor' ? '#0f766e' : '#64748b', fontSize: 11, fontWeight: 600 }}
        >
          <Store size={18} />
          <span>Vendeur</span>
        </button>
      </nav>
    </div>
  );
}
