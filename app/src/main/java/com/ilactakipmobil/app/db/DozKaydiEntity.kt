package com.ilactakipmobil.app.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "doz_kayitlari")
data class DozKaydiEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val ilacId: Int,
    val ilacAdi: String,
    val dozaj: String,
    val planlananTarih: String, // "yyyy-MM-dd"
    val planlananSaat: String,  // "HH:mm"
    var durum: String = DURUM_BEKLIYOR, // BEKLIYOR, ALINDI, ATLANDI, ERTELENDI, SURESI_GECTI
    var islemTarihi: String? = null, // Gerçek onaylandığı / atlandığı zaman: "yyyy-MM-dd HH:mm:ss"
    var ertelendiSaat: String? = null
) {
    companion object {
        const val DURUM_BEKLIYOR = "BEKLIYOR"
        const val DURUM_ALINDI = "ALINDI"
        const val DURUM_ATLANDI = "ATLANDI"
        const val DURUM_ERTELENDI = "ERTELENDI"
        const val DURUM_SURESI_GECTI = "SURESI_GECTI"
    }
}
