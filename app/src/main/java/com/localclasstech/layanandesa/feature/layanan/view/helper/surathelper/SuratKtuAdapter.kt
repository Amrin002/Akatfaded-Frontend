package com.localclasstech.layanandesa.feature.layanan.view.helper.surathelper

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.localclasstech.layanandesa.R
import com.localclasstech.layanandesa.databinding.ListSuratBinding
import com.localclasstech.layanandesa.feature.layanan.data.DataClassCardSurat
import com.localclasstech.layanandesa.settings.utils.DateUtils

class SuratKtuAdapter(private var cardSuratList: List<DataClassCardSurat>,
                      private val listener: OnAdapterListener
) : RecyclerView.Adapter<SuratKtuAdapter.ViewHolder>() {

    class ViewHolder(private val binding: ListSuratBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cardSurat: DataClassCardSurat) {
            binding.tvJenisSurat.text = cardSurat.jenisSurat
            binding.tvNamaPengirim.text = cardSurat.namaPengirim
            binding.tvTanggalPembuatan.text = DateUtils.formatDate(cardSurat.tanggalPembuatan)

            // Translate status to Indonesian
            val statusIndonesia = when (cardSurat.statusSurat) {
                "On Progress" -> "Diproses"
                "Approve" -> "Disetujui"
                "Cancel" -> "Dibatalkan"
                else -> cardSurat.statusSurat
            }
            binding.tvStatusSurat.text = statusIndonesia

            // Set status color based on original status value
            when (cardSurat.statusSurat) {
                "On Progress" -> binding.tvStatusSurat.setTextColor(
                    binding.root.context.getColor(R.color.textOnProgress)
                )
                "Approve" -> binding.tvStatusSurat.setTextColor(
                    binding.root.context.getColor(R.color.textAproved)
                )
                "Cancel" -> binding.tvStatusSurat.setTextColor(
                    binding.root.context.getColor(R.color.textCancel)
                )
                else -> binding.tvStatusSurat.setTextColor(
                    binding.root.context.getColor(android.R.color.black)
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListSuratBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = cardSuratList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val surat = cardSuratList[position]
        holder.bind(surat)
        holder.itemView.setOnClickListener {
            listener.onClick(surat)
        }
    }

    fun updateDataKtu(newListKtu: List<DataClassCardSurat>) {
        this.cardSuratList = newListKtu
        notifyDataSetChanged()
    }

    interface OnAdapterListener {
        fun onClick(cardSuratList: DataClassCardSurat)
        fun onUpdate(cardSuratList: DataClassCardSurat)
    }
}
