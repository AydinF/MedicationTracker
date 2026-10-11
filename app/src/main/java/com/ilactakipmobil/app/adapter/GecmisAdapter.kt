package com.ilactakipmobil.app.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ilactakipmobil.app.databinding.ItemGecmisDozBinding
import com.ilactakipmobil.app.db.DozKaydiEntity

class GecmisAdapter(
    private var dozlar: MutableList<DozKaydiEntity>
) : RecyclerView.Adapter<GecmisAdapter.GecmisViewHolder>() {

    class GecmisViewHolder(val binding: ItemGecmisDozBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GecmisViewHolder {
        val binding = ItemGecmisDozBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return GecmisViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GecmisViewHolder, position: Int) {
        val doz = dozlar[position]
        val context = holder.itemView.context

        with(holder.binding) {
            tvIlacAdi.text = doz.ilacAdi
            val dozajYerel = com.ilactakipmobil.app.util.LocaleHelper.formatDozajToCurrentLocale(doz.dozaj, context)
            tvDozaj.text = context.getString(com.ilactakipmobil.app.R.string.dosage_prefix, dozajYerel)
            tvPlanlananZaman.text = context.getString(com.ilactakipmobil.app.R.string.planned_prefix, doz.planlananTarih, doz.planlananSaat)

            when (doz.durum) {
                DozKaydiEntity.DURUM_ALINDI -> {
                    tvDurum.text = context.getString(com.ilactakipmobil.app.R.string.status_taken)
                    tvDurum.setBackgroundColor(Color.parseColor("#E8F5E9"))
                    tvDurum.setTextColor(Color.parseColor("#2E7D32"))
                    tvIslemZamani.text = context.getString(com.ilactakipmobil.app.R.string.action_prefix, doz.islemTarihi ?: "-")
                    tvIslemZamani.setTextColor(Color.parseColor("#2E7D32"))
                }
                DozKaydiEntity.DURUM_ATLANDI -> {
                    tvDurum.text = context.getString(com.ilactakipmobil.app.R.string.status_skipped)
                    tvDurum.setBackgroundColor(Color.parseColor("#FFEBEE"))
                    tvDurum.setTextColor(Color.parseColor("#C62828"))
                    tvIslemZamani.text = context.getString(com.ilactakipmobil.app.R.string.action_prefix, doz.islemTarihi ?: "-")
                    tvIslemZamani.setTextColor(Color.parseColor("#C62828"))
                }
                DozKaydiEntity.DURUM_ERTELENDI -> {
                    tvDurum.text = context.getString(com.ilactakipmobil.app.R.string.status_snoozed, doz.ertelendiSaat ?: "")
                    tvDurum.setBackgroundColor(Color.parseColor("#FFF3E0"))
                    tvDurum.setTextColor(Color.parseColor("#EF6C00"))
                    tvIslemZamani.text = context.getString(com.ilactakipmobil.app.R.string.snoozed_prefix, doz.ertelendiSaat ?: "-")
                    tvIslemZamani.setTextColor(Color.parseColor("#EF6C00"))
                }
                DozKaydiEntity.DURUM_SURESI_GECTI -> {
                    tvDurum.text = context.getString(com.ilactakipmobil.app.R.string.status_expired)
                    tvDurum.setBackgroundColor(Color.parseColor("#FFEBEE"))
                    tvDurum.setTextColor(Color.parseColor("#C62828"))
                    tvIslemZamani.text = context.getString(com.ilactakipmobil.app.R.string.retroactive_hint)
                    tvIslemZamani.setTextColor(Color.parseColor("#C62828"))
                }
                else -> { // DURUM_BEKLIYOR
                    tvDurum.text = context.getString(com.ilactakipmobil.app.R.string.status_pending)
                    tvDurum.setBackgroundColor(Color.parseColor("#F5F5F5"))
                    tvDurum.setTextColor(Color.parseColor("#757575"))
                    tvIslemZamani.text = context.getString(com.ilactakipmobil.app.R.string.awaiting_response)
                    tvIslemZamani.setTextColor(Color.parseColor("#757575"))
                }
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
