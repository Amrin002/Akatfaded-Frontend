package com.localclasstech.layanandesa.feature.keluhan.view.helper

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.localclasstech.layanandesa.databinding.LayoutItemKeluhansBinding
import com.localclasstech.layanandesa.feature.keluhan.data.CardListKeluhan
import com.localclasstech.layanandesa.settings.utils.DateUtils
import com.localclasstech.layanandesa.R

class KeluhanAdapter(private var cardListKeluhan : List<CardListKeluhan>,
                     private val listener: OnAdapterListener
):RecyclerView.Adapter<KeluhanAdapter.ViewHolder>()
{


    interface OnAdapterListener {
        fun onClick(cardListKeluhan: CardListKeluhan)
        fun onUpdete(cardListKeluhan: CardListKeluhan)
    }

    class ViewHolder(private val binding: LayoutItemKeluhansBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(cardKeluhan: CardListKeluhan){
            binding.tvJudulKeluhan.text = cardKeluhan.judul
            binding.tvNamaPengirim.text  = cardKeluhan.namaPengirim
            binding.tvTanggalPembuatanKeluhan.text = DateUtils.formatDate(cardKeluhan.tanggalPembuatan)

            val statusIndonesia = when(cardKeluhan.statusKeluhan){
                "pending" -> "Pending"
                "diproses" -> "Diproses"
                "selesai" -> "Selesai"
                else -> cardKeluhan.statusKeluhan
            }
            binding.tvStatusKeluhan.text = statusIndonesia

            when(cardKeluhan.statusKeluhan){
                "pending" -> {
                    binding.statusBadge.setCardBackgroundColor(
                        binding.root.context.getColor(R.color.warning)
                    )
                    binding.tvStatusKeluhan.setTextColor(
                        binding.root.context.getColor(R.color.text_primary)
                    )
                }
                "diproses" -> {
                    binding.statusBadge.setCardBackgroundColor(
                        binding.root.context.getColor(R.color.primary_blue)
                    )
                    binding.tvStatusKeluhan.setTextColor(
                        binding.root.context.getColor(R.color.text_primary)
                    )
                }
                "selesai" -> {
                    binding.statusBadge.setCardBackgroundColor(
                        binding.root.context.getColor(R.color.success)
                    )
                    binding.tvStatusKeluhan.setTextColor(
                        binding.root.context.getColor(R.color.text_primary)
                    )
                }
                else -> {
                    binding.statusBadge.setCardBackgroundColor(
                        binding.root.context.getColor(R.color.background_light)
                    )
                    binding.tvStatusKeluhan.setTextColor(
                        binding.root.context.getColor(R.color.text_primary)
                    )
                }
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LayoutItemKeluhansBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = cardListKeluhan.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val keluhan = cardListKeluhan[position]
        holder.bind(keluhan)
        holder.itemView.setOnClickListener{
            listener.onClick(keluhan)
        }
    }
    fun updateDataKeluhan(newListKeluhan: List<CardListKeluhan>){
        this.cardListKeluhan = newListKeluhan
        notifyDataSetChanged()
    }


}