package com.localclasstech.layanandesa.feature.umkm.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.localclasstech.layanandesa.R
import com.localclasstech.layanandesa.databinding.LayoutItemUmkmBinding
import com.localclasstech.layanandesa.feature.umkm.data.CardUmkm
import com.localclasstech.layanandesa.settings.utils.DateUtils
import com.localclasstech.layanandesa.settings.utils.UrlConstant
import java.text.NumberFormat
import java.util.*

class UmkmAdapter(
    private var umkmList: List<CardUmkm>,
    private val listener: OnAdapterListener
): RecyclerView.Adapter<UmkmAdapter.ViewHolder>() {

    interface OnAdapterListener {
        fun onClick(umkm: CardUmkm)
        fun onEdit(umkm: CardUmkm)
        fun onDelete(umkm: CardUmkm)
    }

    class ViewHolder(private val binding: LayoutItemUmkmBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(umkm: CardUmkm) {
            binding.tvNamaUsaha.text = umkm.namaUsaha
            binding.tvNamaProduk.text = umkm.namaProduk
            binding.tvHarga.text = umkm.harga?.let { "Rp ${formatCurrency(it)}" } ?: "Harga belum diset"
            binding.tvTanggalDaftar.text = DateUtils.formatDate(umkm.tanggalDaftar)
            binding.tvStatus.text = umkm.statusLabel

            // Set status color dan background
            when(umkm.status) {
                "pending" -> {
                    binding.tvStatus.setTextColor(
                        binding.root.context.getColor(R.color.textOnProgress)
                    )
                    binding.tvStatus.setBackgroundResource(R.drawable.status_pending_bg)
                }
                "approved" -> {
                    binding.tvStatus.setTextColor(
                        binding.root.context.getColor(R.color.textSuccess)
                    )
                    binding.tvStatus.setBackgroundResource(R.drawable.status_approved_bg)
                }
                "rejected" -> {
                    binding.tvStatus.setTextColor(
                        binding.root.context.getColor(R.color.textError)
                    )
                    binding.tvStatus.setBackgroundResource(R.drawable.status_rejected_bg)
                }
                else -> {
                    binding.tvStatus.setTextColor(
                        binding.root.context.getColor(R.color.black)
                    )
                    binding.tvStatus.setBackgroundResource(R.drawable.status_background)
                }
            }

            // Load image dengan URL yang sudah diproses
            loadProductImage(umkm.fotoProduk)
        }

        private fun loadProductImage(imagePath: String?) {
            // Gunakan UrlConstant untuk mendapatkan URL yang valid
            val imageUrl = UrlConstant.getValidImageUrl(imagePath)

            Glide.with(binding.root.context)
                .load(imageUrl)
                .placeholder(R.drawable.ic_umkm)
                .error(R.drawable.ic_umkm) // Tambahkan error fallback
                .into(binding.imgUmkm)
        }

        // Helper function untuk format currency
        private fun formatCurrency(amount: String): String {
            return try {
                val number = amount.toDouble()
                val formatter = NumberFormat.getNumberInstance(Locale("id", "ID"))
                formatter.format(number)
            } catch (e: NumberFormatException) {
                amount
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LayoutItemUmkmBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = umkmList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val umkm = umkmList[position]
        holder.bind(umkm)

        // Click listeners
        holder.itemView.setOnClickListener {
            listener.onClick(umkm)
        }

        // Long click untuk show options (edit/delete) - opsional
        holder.itemView.setOnLongClickListener {
            if (umkm.canEdit) {
                // Show popup menu untuk edit/delete
                showPopupMenu(holder.itemView, umkm)
            }
            true
        }
    }

    private fun showPopupMenu(view: android.view.View, umkm: CardUmkm) {
        val popupMenu = androidx.appcompat.widget.PopupMenu(view.context, view)
        popupMenu.menuInflater.inflate(R.menu.umkm_item_menu, popupMenu.menu)

        // Show/hide menu items based on status
        popupMenu.menu.findItem(R.id.menu_edit).isVisible = umkm.canEdit
        popupMenu.menu.findItem(R.id.menu_delete).isVisible = umkm.canDelete

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId) {
                R.id.menu_edit -> {
                    listener.onEdit(umkm)
                    true
                }
                R.id.menu_delete -> {
                    listener.onDelete(umkm)
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    fun updateData(newList: List<CardUmkm>) {
        this.umkmList = newList
        notifyDataSetChanged()
    }
}