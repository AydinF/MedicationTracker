package com.ilactakipmobil.app.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ilaclar")
data class IlacEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val ilacAdi: String,
    val dozaj: String,
    val saat: String, // "09:00" veya "08:00,20:00"
    val gunler: String = "Her Gün", // "Her Gün"
    val aktifMi: Boolean = true
)
