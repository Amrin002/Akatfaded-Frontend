package com.localclasstech.layanandesa.feature.pengaturan.view.aboutus

import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.LeadingMarginSpan
import android.text.style.StyleSpan
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.localclasstech.layanandesa.databinding.ActivityAboutUsBinding

class AboutUsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAboutUsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAboutUsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(binding.main.id)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Tombol back kembali ke PengaturanFragment (via finish)
        binding.backButton.setOnClickListener {
            finish()
        }

        setJustifiedNumberedList()
    }

    private fun setJustifiedNumberedList() {
        val tujuanList = listOf(
            "Meningkatkan pelayanan publik desa melalui teknologi",
            "Mempermudah akses masyarakat terhadap layanan administrasi seperti Pengaduan Surat, Tranparansi APBDes, dan informasi desa",
            "Mendukung program digitalisasi desa yang transparan dan akuntabel"
        )

        val bulletMargin = 20   // Margin angka ke kiri (baris pertama)
        val textIndent = 70     // Indent untuk baris kedua dan selanjutnya
        val builder = SpannableStringBuilder()

        for ((index, item) in tujuanList.withIndex()) {
            val text = "${index + 1}. $item\n"
            val spannable = SpannableString(text)

            spannable.setSpan(
                LeadingMarginSpan.Standard(bulletMargin, textIndent),
                0,
                text.length,
                0
            )

            // Bold hanya angka dan titik (misalnya "1."), bukan keseluruhan isi
            spannable.setSpan(StyleSpan(Typeface.BOLD), 0, 2, 0)

            builder.append(spannable)
        }

        binding.textViewTujuan.text = builder
    }
}