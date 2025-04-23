package com.localclasstech.layanandesa.feature.layanan.view.helper

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.localclasstech.layanandesa.databinding.ListSuratBinding
import com.localclasstech.layanandesa.feature.layanan.data.DataClassCardSurat
import com.localclasstech.layanandesa.settings.utils.DateUtils

class SuratItemAdapter(private val items: List<DataClassCardSurat>):RecyclerView.Adapter<SuratItemAdapter.ViewHolder>(){
    class ViewHolder(val binding: ListSuratBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: DataClassCardSurat) {
        binding.tvJenisSurat.text = item.jenisSurat
        binding.tvNamaPengirim.text = item.namaPengirim
        binding.tvTanggalPembuatan.text =  DateUtils.formatDate(item.tanggalPembuatan)
        binding.tvStatusSurat.text = item.statusSurat
    }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListSuratBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }
}