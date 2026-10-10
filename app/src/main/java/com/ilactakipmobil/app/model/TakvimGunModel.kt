package com.ilactakipmobil.app.model

data class TakvimGunModel(
    val tarihStr: String,
    val gunSayisi: String,
    val buAydaMi: Boolean,
    val bugunMu: Boolean,
    var seciliMi: Boolean = false,
    var durum: String = "BOS" // ALINDI (🟢), ATLANDI (🔴), BEKLIYOR (🟡), BOS (⚪)
)
