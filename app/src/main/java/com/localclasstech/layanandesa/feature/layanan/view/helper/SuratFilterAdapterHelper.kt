package com.localclasstech.layanandesa.feature.layanan.view.helper

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.localclasstech.layanandesa.R
import com.localclasstech.layanandesa.databinding.LayoutFilterSuratBinding
import com.localclasstech.layanandesa.feature.layanan.data.DataClassSuratFilter


class SuratFilterAdapterHelper(private var list:List<DataClassSuratFilter>): RecyclerView.Adapter<SuratFilterAdapterHelper.ViewHolder>(){
    inner class ViewHolder(val binding: LayoutFilterSuratBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LayoutFilterSuratBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        with(holder.binding) {
            tvTypeSurat.text = item.tipeSurat
            tvJumlahSurat.text = "(${item.jumlahSurat})"

            // Atur visibility berdasarkan state
            recyclerViewSuratItem.visibility = if (item.isExpanded) View.VISIBLE else View.GONE

            // Ganti icon berdasarkan state
            val iconRes = if (item.isExpanded) R.drawable.ic_dropdown else R.drawable.ic_dropdown_close
            dropdownSurat.setImageResource(iconRes)

            // Setup adapter untuk inner RecyclerView
            recyclerViewSuratItem.adapter = SuratItemAdapter(item.listSurat)
            recyclerViewSuratItem.layoutManager = LinearLayoutManager(root.context)

            // Toggle saat dropdown diklik
            layoutFilter.setOnClickListener {
                item.isExpanded = !item.isExpanded
                notifyItemChanged(position) // trigger UI update
            }
        }
    }
    fun updateData(newList: List<DataClassSuratFilter>) {
        list = newList
        notifyDataSetChanged()
    }


}
