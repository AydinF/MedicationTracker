package com.ilactakipmobil.app.model

import com.google.gson.annotations.SerializedName

data class Ilac(
    @SerializedName("id")
    val id: Int = 0,

    @SerializedName("ilacAdi")
    val ilacAdi: String,

    @SerializedName("dozaj")
    val dozaj: String,

    @SerializedName("saat")
    val saat: String, // "09:00" formatında

    @SerializedName("gunler")
    val gunler: List<String> = emptyList(),

    @SerializedName("aktifMi")
    var aktifMi: Boolean = true
)

