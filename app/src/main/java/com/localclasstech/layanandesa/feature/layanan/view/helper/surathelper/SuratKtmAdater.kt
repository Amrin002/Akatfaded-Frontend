package com.localclasstech.layanandesa.feature.layanan.view.helper.surathelper

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
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
            binding.tvNamaPengirim.text = cardSurat.namaPengirim
            binding.tvTanggalPembuatan.text = DateUtils.formatDate(cardSurat.tanggalPembuatan)
            binding.tvStatusSurat.text = cardSurat.statusSurat
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