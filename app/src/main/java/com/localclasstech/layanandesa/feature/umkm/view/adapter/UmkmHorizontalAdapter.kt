package com.localclasstech.layanandesa.feature.umkm.view.adapter

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.localclasstech.layanandesa.R
import com.localclasstech.layanandesa.databinding.LayoutUmkmHorizontalBinding

import com.localclasstech.layanandesa.feature.umkm.data.Umkm
import com.localclasstech.layanandesa.settings.utils.UrlConstant
import java.text.NumberFormat
import java.util.*

class UmkmHorizontalAdapter(
    private var umkmList: List<Umkm> = emptyList(),
    private val onItemClick: (Umkm) -> Unit
) : RecyclerView.Adapter<UmkmHorizontalAdapter.UmkmHorizontalViewHolder>() {

    fun updateData(newList: List<Umkm>) {
        umkmList = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UmkmHorizontalViewHolder {
        val binding = LayoutUmkmHorizontalBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return UmkmHorizontalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UmkmHorizontalViewHolder, position: Int) {
        holder.bind(umkmList[position])
    }

    override fun getItemCount(): Int = umkmList.size

    inner class UmkmHorizontalViewHolder(
        private val binding: LayoutUmkmHorizontalBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(umkm: Umkm) {
            with(binding) {
                // Set data
                tvNamaUsaha.text = umkm.nama_usaha
                tvNamaProduk.text = umkm.nama_produk
                tvKategori.text = umkm.kategori_label
                tvDeskripsi.text = umkm.deskripsi_produk

                // Format harga
                if (umkm.harga_double != null && umkm.harga_double!! > 0) {
                    tvHargaProduk.text = formatCurrency(umkm.harga_double!!)
                } else {
                    tvHargaProduk.text = "Harga: Hubungi"
                }

                // Load image
                val imageUrl = UrlConstant.getValidImageUrl(umkm.foto_produk, true)
                Glide.with(itemView.context)
                    .load(imageUrl)
                    .placeholder(R.drawable.default_umkm)
                    .error(R.drawable.default_umkm)
                    .centerCrop()
                    .into(ivFotoProduk)

                // Set kategori background color
                setKategoriBackground(umkm.kategori)

                // Click listener
                root.setOnClickListener {
                    onItemClick(umkm)
                }
            }
        }

        private fun setKategoriBackground(kategori: String) {
            val colorRes = when (kategori) {
                "makanan" -> R.color.kategori_makanan
                "jasa" -> R.color.kategori_jasa
                "kerajinan" -> R.color.kategori_kerajinan
                "pertanian" -> R.color.kategori_pertanian
                "perdagangan" -> R.color.kategori_perdagangan
                else -> R.color.kategori_lainnya
            }

            val drawable = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                setColor(ContextCompat.getColor(itemView.context, colorRes))
                cornerRadius = 12.dpToPx() // Convert dp to pixels
            }

            binding.tvKategori.background = drawable
        }

        // Extension function untuk convert dp ke px
        private fun Int.dpToPx(): Float {
            return this * itemView.context.resources.displayMetrics.density
        }

        private fun formatCurrency(amount: Double): String {
            val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
            return format.format(amount).replace("IDR", "Rp")
        }
    }
}