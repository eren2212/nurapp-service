# Ürün Brief'i — Müslümanlar için Manevi Companion Uygulaması

*(Çalışma adı: "Nur" / "Sabit" / "Rida" — isim bölümüne bakın)*
*Versiyon 1 — MVP planı. (b) fazında companion + döngü mekaniği detaylandırılacak.*

---

## 1. Tek cümlelik özet

Finch'in sevimli **"gerçek hayatta iyi bir şey yap → companion'ın büyüsün"** motorunu, Müslümanların günlük manevi pratiğine uygulayan; **gizlilik-öncelikli, sade, güzel ve adil fiyatlı** bir alışkanlık companion'ı.

## 2. Neden bu, neden şimdi

- **Kanıtlanmış model:** Finch (yıllık ~30M$+, bootstrapped) ve Hallow (Katolikler için, yıllık ~51M$) aynı formülü farklı kitlelerde çalıştırdı: günlük manevi alışkanlık + companion/oyunlaştırma + abonelik.
- **Güven boşluğu:** Sektörün en büyük oyuncuları (Muslim Pro, Salaat First) kullanıcı konum verisini sattıkları için topluluğun güvenini kaybetti. "Verin telefonunda kalır" vaadi = en güçlü pazarlama kancası.
- **Doğru boşluk:** Vakit araçları ruhsuz; Sabr gibi içerik uygulamaları pahalı ve fazla kilitli. Kimse **oyunlaştırılmış günlük döngü + tasarım + güven** üçlüsünü yakalamadı.
- **Mevsimsel roket:** Ramazan, Hallow'un Lent'i gibi — yılda bir kez ülkece manevi yoğunluk ve indirme iştahının zirvesi. Bir sonraki Ramazan ~Şubat 2027 (teyit et), bu da ~6 aylık net bir geliştirme penceresi demek.

## 3. Hedef kitle

- **Birincil:** 18-35 yaş, dünya genelinde pratiklerini düzenli tutmak isteyen ama "kuru" bir vakit uygulamasından fazlasını arayan Müslümanlar.
- **Coğrafya:** Global. İç pazar (Türkiye) düşük-ARPU olduğu için gelir hedefi **global + diaspora** üzerinden kurulur (Batı Avrupa, Kuzey Amerika, Körfez + geniş Müslüman dünya).
- **Dil:** Çok dilli baştan planlanır (İngilizce, Arapça, Türkçe, Endonezce, Urduca, Malayca, Fransızca öncelikli).

## 4. Konumlandırma (rakiplerden ayrışma)

| Rakip tipi | Onlar | Biz |
|---|---|---|
| Vakit araçları (Muslim Pro) | İşlevsel ama ruhsuz, reklamlı, güven yarası | Sıcak companion, reklamsız, gizlilik-öncelikli |
| Gizlilik araçları (Pillars) | Doğru değer ama küçük/ücretsiz, companion yok | Aynı güven + companion + sürdürülebilir gelir |
| Premium içerik (Sabr) | Pahalı, aşırı kilitli, "hutbe" gibi, aylık plan yok | Cömert ücretsiz çekirdek, adil fiyat, oyunlaştırılmış |

**Bizim tek cümlelik farkımız:** *"Namaz vakti uygulaması değil — pratiklerini sürdürmene yardım eden, güvenebileceğin bir arkadaş."*

## 5. Companion konsepti (üst düzey — detayı (b)'de)

> **Önemli tasarım notu:** Companion bir hayvan/canlı (Finch'teki kuş gibi) yerine **soyut ve büyüyen bir şey** olmalı — bir **fidan/ağaç**, bir **fener/ışık (nur)**, ya da bir **yıldız/hilal**. Bu hem tasvir hassasiyetini aşar hem de "manevi büyüme" metaforuna daha güzel oturur. Kullanıcı pratiklerini yaptıkça bu şey büyür/parlar; ihmal edilince cezalandırmaz, sadece nazikçe soluklaşır.

Çekirdek döngü: **pratik yap → companion büyür/parlar → küçük ödül/kozmetik → yarın geri gel.**

## 6. MVP — Ekran ekran

### 6.1 Onboarding (ilk açılış)
- 3-4 ekranlık kaydırmalı değer anlatımı (companion + gizlilik vurgusu).
- Konum izni — **gizlilik mesajıyla**: "Konumun vakit hesabı için sadece cihazında kullanılır, sunucuya gitmez."
- Hesaplama yöntemi / mezhep seçimi (aşağıda teknik nota bak).
- Companion'ı "başlat" ve isimlendir (duygusal bağ anı).
- Takip edilecek pratikleri seç: namaz / zikir / Kur'an / şükür (hepsi opsiyonel, sonradan değişebilir).
- Bildirim izni.
- **Hesap opsiyonel** — local-first; kullanıcı istemezse kayıt yok (gizlilik + sürtünmesiz başlangıç).

### 6.2 Ana ekran (Home)
- Companion görseli, o günkü duruma göre (parlak/soluk).
- Bugünün halkası: 5 vakit + tamamlananlar, bir sonraki namaza geri sayım.
- Streak rozeti.
- Hızlı erişim kısayolları: tesbih, tefekkür.

### 6.3 Namaz takibi
- 5 vakit; her biri işaretlenebilir (kıldım / cemaatle / kaza).
- **Affedici tasarım:** kaçırınca companion üzülmez; suçluluk değil, nazik dönüş daveti.
- Günlük ve haftalık görünüm.

### 6.4 Zikir / Tesbih
- Dijital tesbih (33/99 sayaç), haptik geri bildirim.
- Hazır programlar: SubhanAllah, Elhamdülillah, Allahu Ekber, namaz sonrası, sabah/akşam ezkârı.
- Tamamlayınca companion'a küçük ödül.

### 6.5 Tefekkür / Şükür günlüğü
- Günde tek dokunuş: ruh hali + kısa not ("Bugün neye şükrediyorsun?").
- Geçmiş girdiler listesi.
- Kısa, baskısız — Sabr'ın "fazla iş gibi" hissini vermeyecek.

### 6.6 İlerleme / Streak
- Streak takvimi.
- Companion büyüme aşamaları (görsel).
- Kazanılan kozmetik ödüller.
- İstatistik (temel ücretsiz, derin analiz premium).

### 6.7 Ramazan modu (mevsimsel, lansmanın kalbi)
- Oruç takibi (tuttum / mazeretli).
- Terâvih takibi.
- Hatim / Kur'an ilerlemesi (cüz bazlı çubuk).
- Sahur & iftar geri sayımı + bildirim.
- Ramazan'a özel companion teması + özel ödüller.

### 6.8 Ayarlar
- Gizlilik paneli (veriyi görüntüle/sil, local-first vurgusu).
- Hesaplama yöntemi + konum.
- Bildirim ayarları (vakit bazlı özelleştirme).
- Dil seçimi.
- Tema (koyu mod dahil).

### 6.9 Paywall / Premium
- Cömert ücretsiz çekirdek vurgusu ("namaz takibi, tesbih, companion hep ücretsiz").
- Premium: ekstra kozmetik, derin istatistik, Ramazan ekstra içerik, ek companion/temalar.
- **Aylık + yıllık + ömür boyu** birlikte (Sabr'ın eksikleri).
- Bölgesel adil fiyatlandırma (düşük gelirli ülkelerde otomatik ucuz).

## 7. Teknik yığın (React Native)

- **Framework:** React Native (Expo ile hızlı başla; haptik/bildirim için gerekirse bare/dev-client'a geç).
- **Vakit hesabı:** `adhan` (batoulapps/adhan) JS kütüphanesi — tamamen offline, konum cihazda kalır. Hesaplama yöntemleri (MWL, Ümmü'l-Kurâ, Diyanet, ISNA vb.) yerleşik.
- **Kıble:** cihaz manyetometresi/pusula.
- **Bildirimler:** `expo-notifications` veya Notifee (yerel, zamanlı).
- **Veri:** local-first — SQLite / MMKV / AsyncStorage. **Sunucu yok** = hem gizlilik vaadi hem sıfır altyapı maliyeti. (Bulut yedekleme sonraki fazda opsiyonel özellik olabilir.)
- **Abonelik:** RevenueCat — bölgesel fiyat, paywall A/B testi, kolay entegrasyon, iOS+Android tek yer.
- **Companion görseli:** Lottie veya Rive (hafif, animasyonlu, aşamalı büyüme).
- **Analytics:** gizlilik dostu, minimal (mesajınla tutarlı olsun).

## 8. Monetizasyon özeti

- Model: **freemium + abonelik** (soft paywall, Finch/Hallow gibi).
- Ücretsiz: çekirdek pratik takibi + tesbih + temel companion — kalıcı ve cömert.
- Premium: kozmetik + derin istatistik + Ramazan ekstra + ekstra companion/tema.
- Fiyat mantığı: dolar bazlı, bölgesel ayar; aylık/yıllık/ömür boyu üçlüsü.
- İlk hedef: yüksek retention + adil dönüşüm; agresif kilitten kaçın (Sabr'ın hatası).

## 9. 6 aylık geliştirme takvimi

*(Başlangıç ~Temmuz 2026, lansman hedefi ~Ocak 2027, Ramazan ~Şubat 2027)*

**Ay 1 — Tasarım & kurulum**
Companion konsepti ve marka/isim, tüm ekran akışlarının wireframe'i, görsel dil. Teknik iskele: RN projesi, `adhan` entegrasyon POC, yerel bildirim POC.

**Ay 2 — Çekirdek**
Namaz takibi + vakit hesabı + ana ekran + companion temel hali. Local veri katmanı. Onboarding'in ilk hali.

**Ay 3 — Alışkanlık katmanı**
Tesbih + tefekkür günlüğü + streak/ilerleme + companion büyüme aşamaları. Onboarding'i tamamla.

**Ay 4 — Para & altyapı**
RevenueCat + paywall + ayarlar + çok dil altyapısı + koyu mod. Kapalı beta (TestFlight / Play Internal Testing).

**Ay 5 — Ramazan modu & cila**
Ramazan modunu inşa et. Performans, animasyon cilası, erişilebilirlik. Beta geri bildirimini uygula. ASO hazırlığı (ekran görselleri, açıklama, anahtar kelimeler).

**Ay 6 — Lansman rampası**
Beta'yı genişlet, bug temizliği, App Store/Play inceleme süreçleri, pazarlama materyali, topluluk ağı hazırlığı. → Ocak başı yayına al, Ramazan dalgasına biner.

## 10. Lansman / go-to-market

- **Zamanlama:** Ramazan'dan hemen önce yayına gir; ilk büyük dalgayı yakala.
- **Kanallar:** topluluk odaklı — Reddit (r/islam, r/muslim), Müslüman içerik üreticileri, cami/topluluk ağları, LaunchGood tarzı topluluk kampanyaları.
- **Ana mesaj:** gizlilik + güven ("Müslümanlar tarafından, verini satmayan"). Muslim Pro'nun yarası senin kancan.
- **ASO anahtarları:** prayer tracker, muslim habit tracker, ramadan tracker, dhikr counter, salah tracker.
- **Viral kaldıraç:** cömert ücretsiz katman + paylaşılabilir streak/companion anları.

## 11. Riskler ve notlar

- **Dini hassasiyet & doğruluk:** Vakit hesabında yöntem/mezhep farklarını **seçenek** sun, tek doğru dayatma. Küçük bir ilim ehli danışmanla içerik/companion metaforunu kontrol ettir — bu bir farklılaştırıcı, sadece bir onay kutusu değil.
- **Companion'ın tasviri:** hayvan/suret yerine soyut (fidan/ışık/yıldız) tercih et — hem hassasiyet hem metafor açısından daha güçlü.
- **Düşük-ARPU bölgeler:** bölgesel fiyat + cömert ücretsiz katman; bu bölgeleri gelirden çok büyüme/itibar için değerlendir.
- **İçerik moat'una girme:** Sabr ile ses kütüphanesi yarışına girme — döngü + tasarım + güven üzerinden kazan.
- **Gizlilik vaadini gerçekten uygula:** local-first mimari söz değil, mimarî karar olsun; aksi halde en güçlü kancanı kaybedersin.

---

### Sıradaki adım: (b) fazı
Companion'ın kalbi: nasıl büyüyecek, streak nasıl "affedici" olacak, ödül/kozmetik ekonomisi, Ramazan modunun oyunlaştırma detayı ve duygusal ton. Bunu birlikte tasarlayacağız.
