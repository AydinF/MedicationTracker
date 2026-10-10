package com.ilactakipmobil.app.adapter

import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ilactakipmobil.app.R
import com.ilactakipmobil.app.databinding.ItemTakvimGunCellBinding
import com.ilactakipmobil.app.model.TakvimGunModel

class TakvimGridAdapter(
    private var gunler: MutableList<TakvimGunModel>,
    private val onGunSecildi: (TakvimGunModel) -> Unit
) : RecyclerView.Adapter<TakvimGridAdapter.GunViewHolder>() {

    class GunViewHolder(val binding: ItemTakvimGunCellBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GunViewHolder {
        val binding = ItemTakvimGunCellBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return GunViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GunViewHolder, position: Int) {
        val gun = gunler[position]
        with(holder.binding) {
            tvGunSayisi.text = gun.gunSayisi

            if (gun.seciliMi) {
                layoutSelectionBg.setBackgroundResource(R.drawable.bg_selected_day_ring)
                tvGunSayisi.setTextColor(Color.WHITE)
            } else {
                layoutSelectionBg.background = null
                when {
                    gun.bugunMu -> {
                        tvGunSayisi.setTextColor(Color.parseColor("#1976D2"))
                        tvGunSayisi.setTypeface(null, Typeface.BOLD)
                    }
                    !gun.buAydaMi -> {
                        tvGunSayisi.setTextColor(Color.parseColor("#BDBDBD"))
                        tvGunSayisi.setTypeface(null, Typeface.NORMAL)
                    }
                    else -> {
                        tvGunSayisi.setTextColor(Color.parseColor("#212121"))
                        tvGunSayisi.setTypeface(null, Typeface.NORMAL)
                    }
                }
            }

            when (gun.durum) {
                "ALINDI" -> {
                    viewStatusDot.visibility = View.VISIBLE
                    viewStatusDot.setBackgroundResource(R.drawable.bg_dot_green)
                }
                "ATLANDI" -> {
                    viewStatusDot.visibility = View.VISIBLE
                    viewStatusDot.setBackgroundResource(R.drawable.bg_dot_red)
                }
                "BEKLIYOR" -> {
                    viewStatusDot.visibility = View.VISIBLE
                    viewStatusDot.setBackgroundResource(R.drawable.bg_dot_yellow)
                }
                else -> {
                    viewStatusDot.visibility = View.INVISIBLE
                }
            }

            root.setOnClickListener {
                if (gun.buAydaMi) {
                    gunler.forEach { it.seciliMi = false }
                    gun.seciliMi = true
                    notifyDataSetChanged()
                    onGunSecildi(gun)
                }
            }
        }
    }

    override fun getItemCount(): Int = gunler.size

    fun guncelle(yeniGunler: List<TakvimGunModel>) {
        gunler.clear()
        gunler.addAll(yeniGunler)
        notifyDataSetChanged()
    }
}
