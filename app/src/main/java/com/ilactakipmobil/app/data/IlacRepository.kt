package com.ilactakipmobil.app.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ilactakipmobil.app.api.ApiClient
import com.ilactakipmobil.app.db.AppDatabase
import com.ilactakipmobil.app.db.DozKaydiEntity
import com.ilactakipmobil.app.db.IlacEntity
import com.ilactakipmobil.app.model.Ilac
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class IlacRepository(context: Context) {

    private val sharedPreferences = context.getSharedPreferences("ilac_takip_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val db = AppDatabase.getDatabase(context)
    private val ilacDao = db.ilacDao()
    private val dozKaydiDao = db.dozKaydiDao()

    /**
     * İlk çalıştırmada SharedPreferences'taki verileri Room veritabanına taşır.
     */
    suspend fun verileriGocEtEgerGerekirse() = withContext(Dispatchers.IO) {
        val mevcutRoomIlaclar = ilacDao.getTumIlaclar()
        if (mevcutRoomIlaclar.isEmpty()) {
            val json = sharedPreferences.getString("key_ilac_listesi", null)
            if (!json.isNullOrEmpty()) {
                val type = object : TypeToken<MutableList<Ilac>>() {}.type
                val prefListe: List<Ilac>? = try { gson.fromJson(json, type) } catch (e: Exception) { null }
                prefListe?.forEach { ilac ->
                    ilacDao.insertIlac(
                        IlacEntity(
                            id = ilac.id,
                            ilacAdi = ilac.ilacAdi,
                            dozaj = ilac.dozaj,
                            saat = ilac.saat,
                            gunler = ilac.gunler.joinToString(","),
                            aktifMi = ilac.aktifMi
                        )
                    )
                }
            }
        }
    }

    /**
     * Yerel depodan kayıtlı ilaçları okur (Room + SharedPreferences uyumlu).
     */
    fun getLocalIlaclar(): MutableList<Ilac> {
        val json = sharedPreferences.getString("key_ilac_listesi", null) ?: return mutableListOf()
        val type = object : TypeToken<MutableList<Ilac>>() {}.type
        return try {
            gson.fromJson(json, type) ?: mutableListOf()
        } catch (e: Exception) {
            mutableListOf()
        }
    }

    /**
     * Room veritabanından tüm ilaçları okur.
     */
    suspend fun getTumIlaclarFromDb(): List<Ilac> = withContext(Dispatchers.IO) {
        verileriGocEtEgerGerekirse()
        val entities = ilacDao.getTumIlaclar()
        entities.map { entity ->
            Ilac(
                id = entity.id,
                ilacAdi = entity.ilacAdi,
                dozaj = entity.dozaj,
                saat = entity.saat,
                gunler = entity.gunler.split(","),
                aktifMi = entity.aktifMi
            )
        }
    }

    /**
     * Yerel depoya ve Room'a ilaç listesini kaydeder.
     */
    fun saveLocalIlaclar(liste: List<Ilac>) {
        val json = gson.toJson(liste)
        sharedPreferences.edit().putString("key_ilac_listesi", json).apply()
    }

    /**
     * Sunucudan ilaçları çeker. Sunucu erişilemezse yerel veriyi döndürür.
     */
    suspend fun getIlaclar(): List<Ilac> = withContext(Dispatchers.IO) {
        verileriGocEtEgerGerekirse()
        try {
            val response = ApiClient.apiService.getIlaclar()
            if (response.isSuccessful && response.body() != null) {
                val apiListe = response.body()!!
                if (apiListe.isNotEmpty()) {
                    saveLocalIlaclar(apiListe)
                    apiListe.forEach { ilac ->
                        ilacDao.insertIlac(
                            IlacEntity(
                                id = ilac.id,
                                ilacAdi = ilac.ilacAdi,
                                dozaj = ilac.dozaj,
                                saat = ilac.saat,
                                gunler = ilac.gunler.joinToString(","),
                                aktifMi = ilac.aktifMi
                            )
                        )
                    }
                    return@withContext apiListe
                }
            }
        } catch (e: Exception) {
            // Sunucuya ulaşılamazsa sessizce yerel veritabanını kullan
        }
        return@withContext getTumIlaclarFromDb()
    }

    /**
     * Yeni ilaç ekler.
     */
    suspend fun addIlac(ilac: Ilac): Ilac = withContext(Dispatchers.IO) {
        val yeniId = if (ilac.id == 0) (System.currentTimeMillis() % 100000).toInt() else ilac.id
        val kaydedilecekIlac = ilac.copy(id = yeniId)

        // Room'a ekle
        ilacDao.insertIlac(
            IlacEntity(
                id = kaydedilecekIlac.id,
                ilacAdi = kaydedilecekIlac.ilacAdi,
                dozaj = kaydedilecekIlac.dozaj,
                saat = kaydedilecekIlac.saat,
                gunler = kaydedilecekIlac.gunler.joinToString(","),
                aktifMi = kaydedilecekIlac.aktifMi
            )
        )

        // SharedPreferences'e ekle
        val yerelListe = getLocalIlaclar()
        yerelListe.add(kaydedilecekIlac)
        saveLocalIlaclar(yerelListe)

        // Bugün için doz kaydı oluştur
        val bugunTarih = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        ensureDozKaydiForIlacAndDate(kaydedilecekIlac, bugunTarih)

        try {
            val response = ApiClient.apiService.addIlac(kaydedilecekIlac)
            if (response.isSuccessful && response.body() != null) {
                val sunucuIlac = response.body()!!
                ilacDao.insertIlac(
                    IlacEntity(
                        id = sunucuIlac.id,
                        ilacAdi = sunucuIlac.ilacAdi,
                        dozaj = sunucuIlac.dozaj,
                        saat = sunucuIlac.saat,
                        gunler = sunucuIlac.gunler.joinToString(","),
                        aktifMi = sunucuIlac.aktifMi
                    )
                )
                return@withContext sunucuIlac
            }
        } catch (e: Exception) {
            // Sunucu hatasında yerel ilaç geçerlidir
        }

        return@withContext kaydedilecekIlac
    }

    /**
     * İlaç durumunu açıp/kapatır.
     */
    suspend fun toggleDurum(ilac: Ilac): Ilac = withContext(Dispatchers.IO) {
        val yeniAktif = !ilac.aktifMi
        val guncelIlac = ilac.copy(aktifMi = yeniAktif)

        ilacDao.updateIlac(
            IlacEntity(
                id = guncelIlac.id,
                ilacAdi = guncelIlac.ilacAdi,
                dozaj = guncelIlac.dozaj,
                saat = guncelIlac.saat,
                gunler = guncelIlac.gunler.joinToString(","),
                aktifMi = guncelIlac.aktifMi
            )
        )

        val yerelListe = getLocalIlaclar()
        val idx = yerelListe.indexOfFirst { it.id == ilac.id }
        if (idx != -1) {
            yerelListe[idx] = guncelIlac
            saveLocalIlaclar(yerelListe)
        }

        try {
            ApiClient.apiService.toggleDurum(ilac.id)
        } catch (e: Exception) {
            // Sunucu hatası
        }

        return@withContext guncelIlac
    }

    /**
     * İlacı siler.
     */
    suspend fun deleteIlac(ilacId: Int) = withContext(Dispatchers.IO) {
        ilacDao.deleteIlacById(ilacId)
        dozKaydiDao.deleteDozlarByIlacId(ilacId)

        val yerelListe = getLocalIlaclar()
        yerelListe.removeAll { it.id == ilacId }
        saveLocalIlaclar(yerelListe)

        try {
            ApiClient.apiService.deleteIlac(ilacId)
        } catch (e: Exception) {
            // Sunucu hatası
        }
    }

    /**
     * Belirli bir tarih için planlanan tüm doz kayıtlarını getirir.
     * Eğer ilgili tarihte ilacın doz kaydı henüz oluşturulmadıysa otomatik oluşturur.
     * Geçmiş tarihteki yanıtsız kalan dozların durumunu "SURESI_GECTI" olarak günceller.
     */
    suspend fun getDozlarForDate(tarihStr: String): List<DozKaydiEntity> = withContext(Dispatchers.IO) {
        val aktifIlaclar = getTumIlaclarFromDb().filter { it.aktifMi }
        val bugunTarih = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        for (ilac in aktifIlaclar) {
            ensureDozKaydiForIlacAndDate(ilac, tarihStr)
        }

        val dozlar = dozKaydiDao.getDozlarByTarih(tarihStr)

        // Geçmiş günlerde yanıt verilmemiş dozları "SURESI_GECTI" yap
        if (tarihStr < bugunTarih) {
            dozlar.forEach { doz ->
                if (doz.durum == DozKaydiEntity.DURUM_BEKLIYOR) {
                    doz.durum = DozKaydiEntity.DURUM_SURESI_GECTI
                    dozKaydiDao.updateDozKaydi(doz)
                }
            }
        }

        return@withContext dozKaydiDao.getDozlarByTarih(tarihStr)
    }

    private suspend fun ensureDozKaydiForIlacAndDate(ilac: Ilac, tarihStr: String) {
        if (!ilacGunUygunMu(ilac, tarihStr)) return

        val saatler = ilac.saat.split(",").map { it.trim() }
        val bugunTarih = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        for (saat in saatler) {
            if (saat.isEmpty()) continue
            val mevcutDoz = dozKaydiDao.getDozByIlacAndTarihAndSaat(ilac.id, tarihStr, saat)
            if (mevcutDoz == null) {
                val baslangicDurum = if (tarihStr < bugunTarih) DozKaydiEntity.DURUM_SURESI_GECTI else DozKaydiEntity.DURUM_BEKLIYOR
                dozKaydiDao.insertDozKaydi(
                    DozKaydiEntity(
                        ilacId = ilac.id,
                        ilacAdi = ilac.ilacAdi,
                        dozaj = ilac.dozaj,
                        planlananTarih = tarihStr,
                        planlananSaat = saat,
                        durum = baslangicDurum
                    )
                )
            }
        }
    }

    private fun ilacGunUygunMu(ilac: Ilac, tarihStr: String): Boolean {
        if (ilac.gunler.isEmpty() || ilac.gunler.contains("Her Gün")) return true

        val gunListesi = ilac.gunler.flatMap { it.split(",") }.map { it.trim() }
        if (gunListesi.contains("Her Gün")) return true

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = try { sdf.parse(tarihStr) } catch (_: Exception) { null } ?: return true

        val cal = java.util.Calendar.getInstance().apply { time = date }
        val dayCode = when (cal.get(java.util.Calendar.DAY_OF_WEEK)) {
            java.util.Calendar.MONDAY -> "Pzt"
            java.util.Calendar.TUESDAY -> "Sal"
            java.util.Calendar.WEDNESDAY -> "Çar"
            java.util.Calendar.THURSDAY -> "Per"
            java.util.Calendar.FRIDAY -> "Cum"
            java.util.Calendar.SATURDAY -> "Cmt"
            java.util.Calendar.SUNDAY -> "Paz"
            else -> ""
        }

        return gunListesi.contains(dayCode)
    }

    /**
     * Doz durumunu günceller (Mükerrer kaydı önlemek için aynı dozu günceller).
     */
    suspend fun updateDozDurumu(dozId: Int, yeniDurum: String, ertelendiSaat: String? = null): DozKaydiEntity? = withContext(Dispatchers.IO) {
        val doz = dozKaydiDao.getDozById(dozId) ?: return@withContext null

        val simdiFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        doz.durum = yeniDurum
        doz.islemTarihi = simdiFormat
        if (ertelendiSaat != null) {
            doz.ertelendiSaat = ertelendiSaat
        }

        dozKaydiDao.updateDozKaydi(doz)
        return@withContext doz
    }

    /**
     * İlaç ID ve saat/tarih ile doz güncelleme (Notification Receiver kullanımı için)
     */
    suspend fun updateDozDurumuByIlacAndSaat(
        ilacId: Int,
        tarihStr: String,
        saatStr: String,
        yeniDurum: String,
        ertelendiSaat: String? = null
    ): DozKaydiEntity? = withContext(Dispatchers.IO) {
        var doz = dozKaydiDao.getDozByIlacAndTarihAndSaat(ilacId, tarihStr, saatStr)
        val simdiFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        if (doz == null) {
            val ilacEntity = ilacDao.getIlacById(ilacId)
            doz = DozKaydiEntity(
                ilacId = ilacId,
                ilacAdi = ilacEntity?.ilacAdi ?: "İlaç",
                dozaj = ilacEntity?.dozaj ?: "1 Doz",
                planlananTarih = tarihStr,
                planlananSaat = saatStr,
                durum = yeniDurum,
                islemTarihi = simdiFormat,
                ertelendiSaat = ertelendiSaat
            )
            val insertedId = dozKaydiDao.insertDozKaydi(doz)
            return@withContext doz.copy(id = insertedId.toInt())
        } else {
            doz.durum = yeniDurum
            doz.islemTarihi = simdiFormat
            if (ertelendiSaat != null) {
                doz.ertelendiSaat = ertelendiSaat
            }
            dozKaydiDao.updateDozKaydi(doz)
            return@withContext doz
        }
    }

    /**
     * Doz geçmişini filtreli şekilde getirir.
     */
    suspend fun getDozGecmisi(
        ilacAdiFiltre: String?,
        baslangicTarih: String,
        bitisTarih: String
    ): List<DozKaydiEntity> = withContext(Dispatchers.IO) {
        if (ilacAdiFiltre.isNullOrEmpty() || ilacAdiFiltre == "Tüm İlaçlar" || ilacAdiFiltre == "All Medicines") {
            return@withContext dozKaydiDao.getDozlarByTarihAraligi(baslangicTarih, bitisTarih)
        } else {
            return@withContext dozKaydiDao.getDozlarByIlacAndTarihAraligi(ilacAdiFiltre, baslangicTarih, bitisTarih)
        }
    }

    /**
     * Belirtilen tarih aralığındaki tüm günler için doz kayıtlarını önceden oluşturur ve durum haritası döndürür.
     * ("ALINDI", "ATLANDI", "BEKLIYOR", "BOS")
     */
    suspend fun getAylikDozDurumMap(
        baslangicTarih: String,
        bitisTarih: String
    ): Map<String, String> = withContext(Dispatchers.IO) {
        val aktifIlaclar = ilacDao.getTumIlaclar().filter { it.aktifMi }.map { entity ->
            Ilac(
                id = entity.id,
                ilacAdi = entity.ilacAdi,
                dozaj = entity.dozaj,
                saat = entity.saat,
                gunler = entity.gunler.split(","),
                aktifMi = entity.aktifMi
            )
        }

        val bugunTarih = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val baslangicCal = java.util.Calendar.getInstance().apply {
            time = try { sdf.parse(baslangicTarih) } catch (_: Exception) { Date() } ?: Date()
        }
        val bitisCal = java.util.Calendar.getInstance().apply {
            time = try { sdf.parse(bitisTarih) } catch (_: Exception) { Date() } ?: Date()
        }

        val tempCal = baslangicCal.clone() as java.util.Calendar
        while (!tempCal.after(bitisCal)) {
            val tStr = sdf.format(tempCal.time)
            for (ilac in aktifIlaclar) {
                ensureDozKaydiForIlacAndDate(ilac, tStr)
            }
            tempCal.add(java.util.Calendar.DAY_OF_MONTH, 1)
        }

        val tumDozlar = dozKaydiDao.getDozlarByTarihAraligi(baslangicTarih, bitisTarih)
        val gruplar = tumDozlar.groupBy { it.planlananTarih }
        val durumMap = mutableMapOf<String, String>()

        gruplar.forEach { (tarih, dozlar) ->
            val alinanSayisi = dozlar.count { it.durum == DozKaydiEntity.DURUM_ALINDI }
            val atlananSayisi = dozlar.count { it.durum == DozKaydiEntity.DURUM_ATLANDI || it.durum == DozKaydiEntity.DURUM_SURESI_GECTI || (tarih < bugunTarih && it.durum == DozKaydiEntity.DURUM_BEKLIYOR) }
            val bekleyenSayisi = dozlar.count { tarih >= bugunTarih && (it.durum == DozKaydiEntity.DURUM_BEKLIYOR || it.durum == DozKaydiEntity.DURUM_ERTELENDI) }

            val durumStr = when {
                alinanSayisi == dozlar.size && dozlar.isNotEmpty() -> "ALINDI"
                atlananSayisi > 0 -> "ATLANDI"
                bekleyenSayisi > 0 -> "BEKLIYOR"
                else -> "BOS"
            }
            durumMap[tarih] = durumStr
        }

        return@withContext durumMap
    }
}
