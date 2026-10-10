package com.ilactakipmobil.app.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ilactakipmobil.app.databinding.ItemTakvimDozBinding
import com.ilactakipmobil.app.db.DozKaydiEntity

class TakvimAdapter(
    private var dozlar: MutableList<DozKaydiEntity>,
    private val onAldim: (DozKaydiEntity) -> Unit,
    private val onAtladim: (DozKaydiEntity) -> Unit,
    private val onErtele: (DozKaydiEntity) -> Unit
) : RecyclerView.Adapter<TakvimAdapter.TakvimViewHolder>() {

    inner class TakvimViewHolder(val binding: ItemTakvimDozBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TakvimViewHolder {
        val binding = ItemTakvimDozBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return TakvimViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TakvimViewHolder, position: Int) {
        val doz = dozlar[position]
        val simdiTarihStr = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())

        with(holder.binding) {
            tvIlacAdi.text = doz.ilacAdi
            tvDozaj.text = doz.dozaj
            tvSaat.text = doz.planlananSaat

            val isGecmisTarih = doz.planlananTarih < simdiTarihStr
            val isGelecekTarih = doz.planlananTarih > simdiTarihStr

            if (doz.durum == DozKaydiEntity.DURUM_ALINDI) {
                tvDurum.text = "🟢 Aldım"
                tvDurum.setBackgroundColor(Color.parseColor("#E8F5E9"))
                tvDurum.setTextColor(Color.parseColor("#2E7D32"))
                tvIslemZamani.visibility = View.VISIBLE
                tvIslemZamani.text = "Onay Saati: ${doz.islemTarihi ?: ""}"
                layoutAksiyonlar.visibility = View.GONE
            } else if (doz.durum == DozKaydiEntity.DURUM_ATLANDI) {
                tvDurum.text = "🔴 Atladım"
                tvDurum.setBackgroundColor(Color.parseColor("#FFEBEE"))
                tvDurum.setTextColor(Color.parseColor("#C62828"))
                tvIslemZamani.visibility = View.VISIBLE
                tvIslemZamani.text = "Atlama Saati: ${doz.islemTarihi ?: ""}"
                layoutAksiyonlar.visibility = View.GONE
            } else if (doz.durum == DozKaydiEntity.DURUM_ERTELENDI) {
                tvDurum.text = "🟡 Ertelendi (${doz.ertelendiSaat ?: ""})"
                tvDurum.setBackgroundColor(Color.parseColor("#FFF3E0"))
                tvDurum.setTextColor(Color.parseColor("#EF6C00"))
                tvIslemZamani.visibility = View.GONE
                layoutAksiyonlar.visibility = View.VISIBLE
            } else if (isGecmisTarih || doz.durum == DozKaydiEntity.DURUM_SURESI_GECTI) {
                tvDurum.text = "⚠️ Süresi Geçti"
                tvDurum.setBackgroundColor(Color.parseColor("#FFEBEE"))
                tvDurum.setTextColor(Color.parseColor("#C62828"))
                tvIslemZamani.visibility = View.VISIBLE
                tvIslemZamani.text = "Yanıt verilmedi (Geriye dönük işaretleyebilirsiniz)"
                layoutAksiyonlar.visibility = View.VISIBLE
            } else if (isGelecekTarih) {
                tvDurum.text = "⏰ Planlandı"
                tvDurum.setBackgroundColor(Color.parseColor("#E3F2FD"))
                tvDurum.setTextColor(Color.parseColor("#1565C0"))
                tvIslemZamani.visibility = View.GONE
                layoutAksiyonlar.visibility = View.VISIBLE
            } else { // Bugün & BEKLIYOR
                tvDurum.text = "⏳ Bekliyor"
                tvDurum.setBackgroundColor(Color.parseColor("#FFF3E0"))
                tvDurum.setTextColor(Color.parseColor("#EF6C00"))
                tvIslemZamani.visibility = View.GONE
                layoutAksiyonlar.visibility = View.VISIBLE
            }

            btnAldim.setOnClickListener {
                btnAldim.isEnabled = false // Mükerrer basmayı engelle
                onAldim(doz)
            }

            btnAtladim.setOnClickListener {
                btnAtladim.isEnabled = false // Mükerrer basmayı engelle
                onAtladim(doz)
            }

            btnErtele.setOnClickListener {
                onErtele(doz)
            }
        }
    }

    override fun getItemCount(): Int = dozlar.size

    fun guncelle(yeniDozlar: List<DozKaydiEntity>) {
        dozlar.clear()
        dozlar.addAll(yeniDozlar)
        notifyDataSetChanged()
    }
}
