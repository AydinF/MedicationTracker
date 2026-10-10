# 💊 İlaç Takip Mobil (Medication Tracker App)

![Android](https://img.shields.io/badge/Android-Native-green?logo=android)
![Kotlin](https://img.shields.io/badge/Kotlin-2.0+-purple?logo=kotlin)
![Architecture](https://img.shields.io/badge/Architecture-MVVM%20%2B%20Repository-blue)
![Database](https://img.shields.io/badge/Database-Room%20SQLite-orange)
![AI/ML](https://img.shields.io/badge/AI-Google%20ML%20Kit%20OCR-yellow)
![License](https://img.shields.io/badge/License-MIT-lightgrey)

**İlaç Takip Mobil**, kullanıcıların günlük ilaçlarını düzenli olarak takip etmelerini, kamera ile ilaç kutularını tarayarak otomatik bilgi doldurmalarını, sesli ve ertelemeli alarmlarla dozlarını kaçırmamalarını sağlayan modern bir Android uygulamasıdır.

---

## ✨ Temel Özellikler

### 1. 📷 ML Kit ile İlaç Kutusundan Metin Okuma (OCR)
- Kamera veya galeri üzerinden ilaç kutusunun fotoğrafı taranır.
- **Google ML Kit Text Recognition** altyapısı ile ilaç adı, ürün gücü (ör. *500 mg*) ve tablet sayısı (ör. *20 Tablet*) otomatik olarak tespit edilip form alanlarına yazılır.
- Kamera izni verilmediğinde otomatik olarak galeri seçeneği sunulur.

### 2. 📅 Uygulama İçi Görsel Aylık Takvim
- Harici takvim uygulamalarına ihtiyaç duymadan uygulama içinde 7 sütunlu (Pzt - Paz) tam aylık takvim matrisi.
- Günlük kullanım durumunu gösteren renkli rozetler:
  - **🟢 Yeşil Nokta**: O günün tüm ilaçları alındı.
  - **🔴 Kırmızı Nokta**: O gün en az bir ilaç atlandı veya zamanı geçti.
  - **🟡 Sarı Nokta**: Bugün için yanıt bekleyen aktif doz var.
- Herhangi bir güne dokunulduğunda o tarihin ilaç dozları saat sırasıyla listelenir.
- Seçilen özel tarihe doğrudan yeni ilaç tanımlama imkanı.

### 3. 💊 İlaç ve Doz Yönetimi
- İlaç adı, dozaj bilgisi, saat ve kullanım günleri (`Her Gün` veya özel çip seçimi: `Pzt`, `Sal`, `Çar`...) tanımlanabilir.
- Her doz için 3 durum seçeneği: **✅ Aldım**, **❌ Atladım**, **⏰ Ertele (10 dk, 15 dk, 30 dk, 1 saat)**.
- Yanıt verilmeyen geçmiş tarihteki dozlar otomatik olarak `⚠️ Süresi Geçti` kategorisine alınır.

### 4. 📜 Kullanım Geçmişi ve Filtreleme
- Günlük, haftalık ve tüm zamanlar bazında kullanım kayıtları görüntülenir.
- İlaç adına göre dinamik açılır menü (Spinner) ile filtreleme yapılabilir.
- Kullanıcının ilacı onayladığı gerçek işlem zamanı (`islemTarihi`) ayrı saklanır.

### 5. ⏰ Kesin Zamanlı Sesli Alarm ve Bildirimler
- `AlarmManager` (`setAlarmClock`) ile cihaz uyku modunda olsa dahi tam vaktinde yüksek öncelikli sesli ve titreşimli alarm tetiklenir.
- Bildirim üzerinden uygulamaya girmeden doğrudan **✅ Aldım**, **❌ Atladım**, **⏰ 10 Dk Ertele** ve **🔕 Sustur** eylemleri gerçekleştirilebilir.

### 6. 🗄️ Kalıcı Veritabanı (Room SQLite)
- Tüm ilaçlar ve doz geçmişi cihaz üzerinde Room SQLite veritabanında saklanır. Uygulama kapatılıp açıldığında veya cihaz yeniden başlatıldığında veriler korunur.

---

## 🛠️ Kullanılan Teknolojiler ve Kütüphaneler

| Bileşen | Kullanılan Kütüphane / Yapı |
| :--- | :--- |
| **Dil** | Kotlin (Built-in AGP 9.0+ Kotlin Support) |
| **Arayüz (UI)** | Material Design 3, ViewBinding, CoordinatorLayout |
| **Veritabanı** | Room Database (SQLite) v2.8.5 |
| **Yapay Zeka (OCR)** | Google ML Kit Text Recognition v16.0.1 |
| **Ağ / API** | Retrofit 2 & OkHttp 3 & Gson |
| **Arka Plan & Asenkron** | Kotlin Coroutines & Lifecycle ViewModel |
| **Zamanlayıcı** | AlarmManager & BroadcastReceiver & FileProvider |

---

## 🚀 Projeyi Bilgisayarınızda Çalıştırma

### Gereksinimler
- **Android Studio**: Ladybug / 2024.2+ veya üzeri
- **JDK**: OpenJDK 11 veya 17
- **Android SDK**: Compile SDK 37 (Min SDK 26 - Android 8.0+)

### Adım Adım Kurulum

1. **Projeyi Klonlayın:**
   ```bash
   git clone https://github.com/KULLANICI_ADI/IlacTakipMobil.git
   cd IlacTakipMobil
   ```

2. **Proje Bağımlılıklarını Yükleyin:**
   Android Studio'da projeyi açın ve Gradle Senkronizasyonunun (`Sync Project with Gradle Files`) tamamlanmasını bekleyin.

3. **Çalıştırın:**
   Bir Android emülatör veya USB ile bağlı gerçek Android cihaz seçip **Run ▶** (Shift + F10) butonuna basın.

---

## 📱 Ekran Görüntüleri ve Arayüz Düzeni

```
+--------------------------+  +--------------------------+  +--------------------------+
| 💊 İlaçlarım             |  | 📅 İlaç Takvimi          |  | 📜 Kullanım Geçmişi      |
|                          |  |                          |  |                          |
|  Parol 500 mg            |  |  ◀   Mart 2026   ▶       |  |  İlaç Filtre: [Tüm ▾]    |
|  ⏰ 09:50 (Her Gün) [ON] |  |  Pzt Sal Çar Per Cum Cmt |  |  [Bugün] [Hafta] [Tüm]   |
|                          |  |   1   2   3   4   5  🟢  |  |                          |
|                          |  |   7   8  🔴  10  11  12  |  |  Parol 500 mg            |
|                          |  |  13  14  15  16  17  18  |  |  Planlanan: 09:50        |
|  [➕ İlaç Ekle]          |  |                          |  |  Durum: ✅ Aldı (09:52) |
+--------------------------+  +--------------------------+  +--------------------------+
```

---

## 🔐 Güvenlik ve Gizlilik
Uygulama taranan ilaç kutusu fotoğraflarını hiçbir harici sunucuya göndermez. Tüm OCR işlemleri cihaz üzerinde (On-Device ML Kit) gerçekleşir.

---

## 📄 Lisans
Bu proje [MIT Lisansı](LICENSE) ile lisanslanmıştır.
