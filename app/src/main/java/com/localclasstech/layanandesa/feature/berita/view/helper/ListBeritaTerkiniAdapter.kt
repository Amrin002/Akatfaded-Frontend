package com.localclasstech.layanandesa.feature.berita.view.helper

import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.RecyclerView
import com.localclasstech.layanandesa.R
import com.localclasstech.layanandesa.databinding.ListBeritaBinding
import com.localclasstech.layanandesa.feature.berita.data.DataClassBerita
import com.localclasstech.layanandesa.settings.utils.ImageLoader
import com.localclasstech.layanandesa.settings.utils.UrlConstant
import kotlinx.coroutines.launch


class ListBeritaTerkiniAdapter(private var beritaList: List<DataClassBerita>,
                               private val listener: OnAdapterListener,
                               private val lifecycleScope: LifecycleCoroutineScope, // Tambahkan parameter ini
                               private val context: android.content.Context
    ):RecyclerView.Adapter<ListBeritaTerkiniAdapter.ViewHolder>() {
    inner class ViewHolder(private val binding: ListBeritaBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(berita: DataClassBerita) {
            // Gunakan URL dengan timestamp unik
            val uniqueImageUrl = UrlConstant.getValidImageUrlWithTimestamp(berita.imgBerita)

            lifecycleScope.launch {
                ImageLoader.loadImage(
                    context,
                    uniqueImageUrl,
                    binding.imgBerita
                )
            }

            binding.tvJudulBerita.text = berita.judulBerita
            binding.tvPenulisBerita.text = berita.penulisBerita
            binding.tvTanggalBerita.text = berita.tanggalBerita
            binding.tvKontenBerita.apply {
                text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Html.fromHtml(berita.kontenBerita, Html.FROM_HTML_MODE_COMPACT)
                } else {
                    @Suppress("DEPRECATION")
                    Html.fromHtml(berita.kontenBerita)
                }
            }

            binding.root.setOnClickListener {
                listener.onClick(berita.idBerita)
            }

            binding.root.setOnClickListener {
                listener.onClick(berita.idBerita)
            }



//            binding.root.setOnLongClickListener {
//                listener.onUpdete(berita)
//                true
//            }
        }
    }

    interface OnAdapterListener {
        fun onClick(beritaId: Int )
//        fun onUpdete(berita: DataClassBerita)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListBeritaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = beritaList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(beritaList[position])
    }
    fun setData(newList: List<DataClassBerita>) {
        beritaList = newList
        notifyDataSetChanged()
    }


}