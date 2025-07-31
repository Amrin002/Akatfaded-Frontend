package com.localclasstech.layanandesa.feature.pengaturan.view.aboutus

import android.content.pm.PackageManager
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

        setupWindowInsets()
        setupClickListeners()
        setupContent()
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupClickListeners() {
        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun setupContent() {
        setDynamicVersionName()
        setJustifiedNumberedList()
    }

    private fun setDynamicVersionName() {
        try {
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            val versionName = packageInfo.versionName
            val versionCode = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                packageInfo.longVersionCode
            } else {
                @Suppress("DEPRECATION")
                packageInfo.versionCode.toLong()
            }

            binding.tvVersionApps.text = "Version $versionName ($versionCode)"

        } catch (e: PackageManager.NameNotFoundException) {
            // Fallback jika terjadi error
            binding.tvVersionApps.text = "Version 1.0"
        }
    }

    private fun setJustifiedNumberedList() {
        val tujuanList = listOf(
            "Meningkatkan pelayanan publik desa melalui teknologi digital yang mudah diakses",
            "Mempermudah akses masyarakat terhadap layanan administrasi seperti Pengaduan Surat, Transparansi APBDes, dan informasi desa terkini",
            "Mendukung program digitalisasi desa yang transparan, akuntabel, dan berkelanjutan"
        )

        val bulletMargin = 20   // Margin untuk angka ke kiri (baris pertama)
        val textIndent = 70     // Indent untuk baris kedua dan selanjutnya
        val builder = SpannableStringBuilder()

        tujuanList.forEachIndexed { index, item ->
            val numberText = "${index + 1}. "
            val fullText = "$numberText$item\n"
            val spannable = SpannableString(fullText)

            // Set margin untuk seluruh text
            spannable.setSpan(
                LeadingMarginSpan.Standard(bulletMargin, textIndent),
                0,
                fullText.length,
                0
            )

            // Bold hanya untuk nomor (misalnya "1.")
            spannable.setSpan(
                StyleSpan(Typeface.BOLD),
                0,
                numberText.length,
                0
            )

            builder.append(spannable)
        }

        // Hapus line break terakhir
        if (builder.isNotEmpty()) {
            builder.delete(builder.length - 1, builder.length)
        }

        binding.textViewTujuan.text = builder
    }
}