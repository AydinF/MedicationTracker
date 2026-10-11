# 💊 Medication Tracker (İlaç Takip Mobil)

![Android](https://img.shields.io/badge/Android-Native-green?logo=android)
![Kotlin](https://img.shields.io/badge/Kotlin-2.0+-purple?logo=kotlin)
![Language](https://img.shields.io/badge/Language-TR%20%7C%20EN-blue)
![Architecture](https://img.shields.io/badge/Architecture-MVVM%20%2B%20Repository-blue)
![Database](https://img.shields.io/badge/Database-Room%20SQLite-orange)
![AI/ML](https://img.shields.io/badge/AI-Google%20ML%20Kit%20OCR-yellow)
![License](https://img.shields.io/badge/License-MIT-lightgrey)

**Medication Tracker (İlaç Takip Mobil)**, kullanıcıların günlük ilaçlarını düzenli olarak takip etmelerini, kamera ile ilaç kutularını tarayarak otomatik bilgi doldurmalarını, sesli ve ertelemeli alarmlarla dozlarını kaçırmamalarını sağlayan ve **🇹🇷 Türkçe & 🇬🇧 İngilizce** çift dil desteği sunan modern bir Android uygulamasıdır.

---

## ✨ Key Features / Temel Özellikler

### 1. 🌐 Multi-Language Support / Çift Dilli Destek
- Uygulama içi tek tıkla canlı dil geçişi: **🇹🇷 TR (🇬🇧 EN)**.
- Tüm menüler, takvim ay/gün isimleri, özet kartları ve ilaç kullanım birimleri (*1 Doz ➔ 1 Dose*, *Her Gün ➔ Everyday*) anında seçili dile çevrilir.

### 2. 📷 ML Kit AI Text Recognition / İlaç Kutusundan Metin Okuma (OCR)
- Kamera veya galeri üzerinden ilaç kutusunun fotoğrafı taranır.
- **Google ML Kit Text Recognition** altyapısı ile ilaç adı, ürün gücü (ör. *500 mg*) ve tablet sayısı (ör. *20 Tablet*) otomatik olarak tespit edilip form alanlarına yazılır.
- Kamera izni verilmediğinde otomatik olarak galeri seçeneği sunulur.

### 3. 📅 In-App Calendar / Uygulama İçi Görsel Aylık Takvim
- Harici takvim uygulamalarına ihtiyaç duymadan uygulama içinde 7 sütunlu (Pzt - Paz) tam aylık takvim matrisi.
- Günlük kullanım durumunu gösteren renkli rozetler:
  - **🟢 Yeşil Nokta**: O günün tüm ilaçları alındı.
  - **🔴 Kırmızı Nokta**: O gün en az bir ilaç atlandı veya zamanı geçti.
  - **🟡 Sarı Nokta**: Bugün için yanıt bekleyen aktif doz var.
- Herhangi bir güne dokunulduğunda o tarihin ilaç dozları saat sırasıyla listelenir.
- Seçilen özel tarihe doğrudan yeni ilaç tanımlama imkanı (`➕ Bu Tarihe İlaç Ekle`).

### 4. 💊 Medicine & Dose Management / İlaç ve Doz Yönetimi
- İlaç adı, dozaj bilgisi, saat ve kullanım günleri (`Her Gün` veya özel çip seçimi: `Pzt`, `Sal`, `Çar`...) tanımlanabilir.
- Her doz için 3 durum seçeneği: **✅ Aldım / Taken**, **❌ Atladım / Skipped**, **⏰ Ertele / Snooze (10 dk, 15 dk, 30 dk, 1 saat)**.
- Yanıt verilmeyen geçmiş tarihteki dozlar otomatik olarak `⚠️ Süresi Geçti` / `Expired` kategorisine alınır.

### 5. 📜 Usage History & Filtering / Kullanım Geçmişi ve Filtreleme
- Günlük, haftalık ve tüm zamanlar bazında kullanım kayıtları görüntülenir.
- İlaç adına göre dinamik açılır menü (Spinner) ile filtreleme yapılabilir.
- Kullanıcının ilacı onayladığı gerçek işlem zamanı (`islemTarihi`) ayrı saklanır.

### 6. ⏰ Exact Alarms & Notifications / Kesin Zamanlı Sesli Alarm ve Bildirimler
- `AlarmManager` (`setAlarmClock`) ile cihaz uyku modunda olsa dahi tam vaktinde yüksek öncelikli sesli ve titreşimli alarm tetiklenir.
- Bildirim üzerinden uygulamaya girmeden doğrudan **✅ Aldım**, **❌ Atladım**, **⏰ 10 Dk Ertele** ve **🔕 Sustur** eylemleri gerçekleştirilebilir.

### 7. 🗄️ Persistent Database / Kalıcı Veritabanı (Room SQLite)
- Tüm ilaçlar ve doz geçmişi cihaz üzerinde Room SQLite veritabanında saklanır. Uygulama kapatılıp açıldığında veya cihaz yeniden başlatıldığında veriler korunur.

---

## 🛠️ Tech Stack / Teknolojiler ve Kütüphaneler

| Component | Library / Framework |
| :--- | :--- |
| **Language** | Kotlin (Built-in AGP 9.0+ Kotlin Support) |
| **UI** | Material Design 3, ViewBinding, CoordinatorLayout |
| **Database** | Room Database (SQLite) v2.8.5 |
| **AI / OCR** | Google ML Kit Text Recognition v16.0.1 |
| **Network / API** | Retrofit 2 & OkHttp 3 & Gson |
| **Concurrency** | Kotlin Coroutines & Lifecycle ViewModel |
| **Localization** | Custom LocaleHelper & Per-App Language API |
| **Scheduler** | AlarmManager & BroadcastReceiver & FileProvider |

---

## 🚀 Installation & Setup / Projeyi Bilgisayarınızda Çalıştırma

### Prerequisites / Gereksinimler
- **Android Studio**: Ladybug / 2024.2+ veya üzeri
- **JDK**: OpenJDK 11 veya 17
- **Android SDK**: Compile SDK 37 (Min SDK 26 - Android 8.0+)

### Step-by-Step Setup / Adım Adım Kurulum

1. **Clone Repository / Projeyi Klonlayın:**
   ```bash
   git clone https://github.com/cem-oral/MedicationTracker.git
   cd MedicationTracker
   ```

2. **Sync Dependencies / Bağımlılıkları Yükleyin:**
   Android Studio'da projeyi açın ve Gradle Senkronizasyonunun (`Sync Project with Gradle Files`) tamamlanmasını bekleyin.

3. **Run App / Çalıştırın:**
   Bir Android emülatör veya USB ile bağlı gerçek Android cihaz seçip **Run ▶** (Shift + F10) butonuna basın.

---

## 📱 UI Layout / Arayüz Düzeni

```
+--------------------------+  +--------------------------+  +--------------------------+
| 💊 My Medicines  [🇹🇷/🇬🇧] |  | 📅 Calendar      [🇹🇷/🇬🇧] |  | 📜 Usage History [🇹🇷/🇬🇧] |
|                          |  |                          |  |                          |
|  Parol 500 mg            |  |  ◀   October 2026   ▶    |  |  Filter: [All Medicines▾]|
|  ⏰ 09:50 (Everyday) [ON]|  |  Mon Tue Wed Thu Fri Sat |  |  [Today] [Week] [All]    |
|                          |  |   1   2   3   4   5  🟢  |  |                          |
|                          |  |   7   8  🔴  10  11  12  |  |  Parol 500 mg            |
|                          |  |  13  14  15  16  17  18  |  |  Planned: 09:50          |
|  [➕ Add Medicine]       |  |                          |  |  Action: ✅ Taken (09:52)|
+--------------------------+  +--------------------------+  +--------------------------+
```

---

## 🔐 Security & Privacy / Güvenlik ve Gizlilik
Uygulama taranan ilaç kutusu fotoğraflarını hiçbir harici sunucuya göndermez. Tüm OCR İşlemleri tamamen cihaz üzerinde (On-Device ML Kit) gerçekleşir.

---

## 📄 License / Lisans
Bu proje [MIT Lisansı](LICENSE) ile lisanslanmıştır.
