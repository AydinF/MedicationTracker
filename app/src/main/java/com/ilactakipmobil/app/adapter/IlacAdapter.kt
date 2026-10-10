package com.ilactakipmobil.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ilactakipmobil.app.databinding.ItemIlacBinding
import com.ilactakipmobil.app.model.Ilac

class IlacAdapter(
    private var ilaclar: MutableList<Ilac>,
    private val onToggleDurum: (Ilac) -> Unit,
    private val onSil: (Ilac) -> Unit
) : RecyclerView.Adapter<IlacAdapter.IlacViewHolder>() {

    inner class IlacViewHolder(val binding: ItemIlacBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IlacViewHolder {
        val binding = ItemIlacBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return IlacViewHolder(binding)
    }

    override fun onBindViewHolder(holder: IlacViewHolder, position: Int) {
        val ilac = ilaclar[position]
        with(holder.binding) {
            tvIlacAdi.text = ilac.ilacAdi
            val gunlerText = if (ilac.gunler.isNotEmpty()) " (${ilac.gunler.joinToString(", ")})" else ""
            tvDozaj.text = "${ilac.dozaj}$gunlerText"
            tvSaat.text = "⏰ ${ilac.saat}"

            // Switch dinleyicisini geçici olarak kaldırıp durumu set ediyoruz
            switchAktif.setOnCheckedChangeListener(null)
            switchAktif.isChecked = ilac.aktifMi

            switchAktif.setOnCheckedChangeListener { _, isChecked ->
                ilac.aktifMi = isChecked
                onToggleDurum(ilac)
            }

            btnSil.setOnClickListener {
                onSil(ilac)
            }
        }
    }

    override fun getItemCount(): Int = ilaclar.size

    fun guncelle(yeniListe: List<Ilac>) {
        ilaclar.clear()
        ilaclar.addAll(yeniListe)
        notifyDataSetChanged()
    }
}

