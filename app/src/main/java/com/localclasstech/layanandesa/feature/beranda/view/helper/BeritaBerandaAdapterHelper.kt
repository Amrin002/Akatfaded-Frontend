package com.localclasstech.layanandesa.feature.beranda.view.helper

import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.localclasstech.layanandesa.R
import com.localclasstech.layanandesa.feature.beranda.data.DataClassBerita

class BeritaBerandaAdapterHelper(
    private var list: List<DataClassBerita>,
    private val listener: OnAdapterListener
): RecyclerView.Adapter<BeritaBerandaAdapterHelper.BeritaViewHolder>() {
    class BeritaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgBerita: ImageView = itemView.findViewById(R.id.imgBerita)
        val tvJudul: TextView = itemView.findViewById(R.id.tvJudulBerita)
        val tvPenulis: TextView = itemView.findViewById(R.id.tvPenulisBerita)
        val tvTanggal: TextView = itemView.findViewById(R.id.tvTanggalBerita)
        val tvKonten: TextView = itemView.findViewById(R.id.tvKontenBerita)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BeritaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_berita, parent, false)
        return BeritaViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: BeritaViewHolder, position: Int) {
        val berita = list[position]
        holder.tvJudul.text = berita.judulBerita
        holder.tvPenulis.text = berita.penulisBerita
        holder.tvTanggal.text = berita.tanggalBerita
        holder.tvKonten.apply {
            text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(berita.kontenBerita, Html.FROM_HTML_MODE_COMPACT)
            } else {
                @Suppress("DEPRECATION")
                Html.fromHtml(berita.kontenBerita)
            }
        }
        // Load image pakai Glide
        Glide.with(holder.itemView.context)
            .load(berita.imgBerita)
            .placeholder(R.drawable.ic_berita)
            .into(holder.imgBerita)

        // Add click listener
        holder.itemView.setOnClickListener {
            listener.onClick(berita.idBerita)
        }
    }

    interface OnAdapterListener {
        fun onClick(beritaId: Int)
    }

    fun setData(newList: List<DataClassBerita>) {
        list = newList
        notifyDataSetChanged()
    }
}