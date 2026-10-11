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

        val context = holder.itemView.context

        with(holder.binding) {
            tvIlacAdi.text = doz.ilacAdi
            tvDozaj.text = com.ilactakipmobil.app.util.LocaleHelper.formatDozajToCurrentLocale(doz.dozaj, context)
            tvSaat.text = doz.planlananSaat

            btnAldim.text = context.getString(com.ilactakipmobil.app.R.string.btn_take)
            btnAtladim.text = context.getString(com.ilactakipmobil.app.R.string.btn_skip)
            btnErtele.text = context.getString(com.ilactakipmobil.app.R.string.btn_snooze)

            val isGecmisTarih = doz.planlananTarih < simdiTarihStr
            val isGelecekTarih = doz.planlananTarih > simdiTarihStr

            if (doz.durum == DozKaydiEntity.DURUM_ALINDI) {
                tvDurum.text = context.getString(com.ilactakipmobil.app.R.string.status_taken)
                tvDurum.setBackgroundColor(Color.parseColor("#E8F5E9"))
                tvDurum.setTextColor(Color.parseColor("#2E7D32"))
                tvIslemZamani.visibility = View.VISIBLE
                tvIslemZamani.text = context.getString(com.ilactakipmobil.app.R.string.confirm_time, doz.islemTarihi ?: "")
                layoutAksiyonlar.visibility = View.GONE
            } else if (doz.durum == DozKaydiEntity.DURUM_ATLANDI) {
                tvDurum.text = context.getString(com.ilactakipmobil.app.R.string.status_skipped)
                tvDurum.setBackgroundColor(Color.parseColor("#FFEBEE"))
                tvDurum.setTextColor(Color.parseColor("#C62828"))
                tvIslemZamani.visibility = View.VISIBLE
                tvIslemZamani.text = context.getString(com.ilactakipmobil.app.R.string.skip_time, doz.islemTarihi ?: "")
                layoutAksiyonlar.visibility = View.GONE
            } else if (doz.durum == DozKaydiEntity.DURUM_ERTELENDI) {
                tvDurum.text = context.getString(com.ilactakipmobil.app.R.string.status_snoozed, doz.ertelendiSaat ?: "")
                tvDurum.setBackgroundColor(Color.parseColor("#FFF3E0"))
                tvDurum.setTextColor(Color.parseColor("#EF6C00"))
                tvIslemZamani.visibility = View.GONE
                layoutAksiyonlar.visibility = View.VISIBLE
            } else if (isGecmisTarih || doz.durum == DozKaydiEntity.DURUM_SURESI_GECTI) {
                tvDurum.text = context.getString(com.ilactakipmobil.app.R.string.status_expired)
                tvDurum.setBackgroundColor(Color.parseColor("#FFEBEE"))
                tvDurum.setTextColor(Color.parseColor("#C62828"))
                tvIslemZamani.visibility = View.VISIBLE
                tvIslemZamani.text = context.getString(com.ilactakipmobil.app.R.string.retroactive_hint)
                layoutAksiyonlar.visibility = View.VISIBLE
            } else if (isGelecekTarih) {
                tvDurum.text = context.getString(com.ilactakipmobil.app.R.string.status_scheduled)
                tvDurum.setBackgroundColor(Color.parseColor("#E3F2FD"))
                tvDurum.setTextColor(Color.parseColor("#1565C0"))
                tvIslemZamani.visibility = View.GONE
                layoutAksiyonlar.visibility = View.VISIBLE
            } else { // Bugün & BEKLIYOR
                tvDurum.text = context.getString(com.ilactakipmobil.app.R.string.status_pending)
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
