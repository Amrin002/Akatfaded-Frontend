package com.localclasstech.layanandesa.feature.layanan.view.helper.surathelper

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.localclasstech.layanandesa.R
import com.localclasstech.layanandesa.databinding.ListSuratBinding
import com.localclasstech.layanandesa.feature.layanan.data.DataClassCardSurat
import com.localclasstech.layanandesa.feature.layanan.data.network.data.suratktm.SktmResponse
import com.localclasstech.layanandesa.settings.utils.DateUtils

class SuratKtmAdater(private var cardSuratList: List<DataClassCardSurat>,
    private val listener: OnAdapterListener
    ): RecyclerView.Adapter<SuratKtmAdater.ViewHolder>() {
    class ViewHolder(private val binding: ListSuratBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cardSurat: DataClassCardSurat) {
            binding.tvJenisSurat.text = cardSurat.jenisSurat
            binding.tvNamaPemohon.text = cardSurat.namaPengirim
            binding.tvTanggalSurat.text = DateUtils.formatDate(cardSurat.tanggalPembuatan)

            // Translate status to Indonesian
            val statusIndonesia = when (cardSurat.statusSurat) {
                "On Progress" -> "Diproses"
                "Approve" -> "Disetujui"
                "Cancel" -> "Dibatalkan"
                else -> cardSurat.statusSurat
            }
            binding.tvStatusSurat.text = statusIndonesia

            // Set status badge color and text color based on status
            when (cardSurat.statusSurat) {
                "On Progress" -> {
                    binding.statusBadge.setCardBackgroundColor(
                        binding.root.context.getColor(R.color.warning)
                    )
                    binding.tvStatusSurat.setTextColor(
                        binding.root.context.getColor(R.color.text_primary)
                    )
                }
                "Approve" -> {
                    binding.statusBadge.setCardBackgroundColor(
                        binding.root.context.getColor(R.color.success)
                    )
                    binding.tvStatusSurat.setTextColor(
                        binding.root.context.getColor(R.color.text_primary)
                    )
                }
                "Cancel" -> {
                    binding.statusBadge.setCardBackgroundColor(
                        binding.root.context.getColor(R.color.error)
                    )
                    binding.tvStatusSurat.setTextColor(
                        binding.root.context.getColor(R.color.text_primary)
                    )
                }
                else -> {
                    binding.statusBadge.setCardBackgroundColor(
                        binding.root.context.getColor(R.color.background_light)
                    )
                    binding.tvStatusSurat.setTextColor(
                        binding.root.context.getColor(R.color.text_primary)
                    )
                }
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

    fun updateDataKtm(newListKtm: List<DataClassCardSurat>) {
        this.cardSuratList = newListKtm
        notifyDataSetChanged()
    }

    interface OnAdapterListener{
        fun onClick(cardSuratList: DataClassCardSurat)
        fun onUpdete(cardSuratList: DataClassCardSurat)

    }
}