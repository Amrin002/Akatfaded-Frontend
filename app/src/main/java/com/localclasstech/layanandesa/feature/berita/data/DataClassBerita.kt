package com.localclasstech.layanandesa.feature.berita.data

import android.text.Html
import android.util.Log
import com.google.gson.annotations.SerializedName
import com.localclasstech.layanandesa.settings.utils.UrlConstant
import com.localclasstech.layanandesa.network.User
import java.text.SimpleDateFormat
import java.util.Locale


data class BeritaResponse(
    val success: Boolean,
    val messsage: String, // perhatikan typo pada "messsage"
    val data: List<Berita>
)

data class DetailBeritaResponse(
    val success: Boolean,
    val messsage: String,
    val data: Berita
)

data class Berita(
    val id: Int,
    val judul: String,
    val konten: String,
    val gambar: String?, // null jika tidak ada gambar
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    val user: User
)

data class DataClassBerita(
    val idBerita: Int,
    val imgBerita: String,
    val judulBerita: String,
    val penulisBerita: String,
    val tanggalBerita: String,
    val kontenBerita: String
)
// Perbaikan mapping function
fun Berita.toUiModel(): DataClassBerita {
    val imageUrl = UrlConstant.getValidImageUrl(this.gambar)
    // Parse HTML content
    val parsedKonten = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
        Html.fromHtml(this.konten, Html.FROM_HTML_MODE_COMPACT).toString()
    } else {
        @Suppress("DEPRECATION")
        Html.fromHtml(this.konten).toString()
    }
    // Daftar nama bulan dalam bahasa Indonesia
    val bulanIndonesia = arrayOf(
        "Januari", "Februari", "Maret", "April", "Mei", "Juni",
        "Juli", "Agustus", "September", "Oktober", "November", "Desember"
    )

    // Parsing manual
    val tanggalBerita = try {
        val parts = this.createdAt.substring(0, 10).split("-")
        if (parts.size == 3) {
            val hari = parts[2]
            val bulan = bulanIndonesia[parts[1].toInt() - 1]
            val tahun = parts[0]
            "$hari/$bulan/$tahun"
        } else {
            this.createdAt.substring(0, 10)
        }
    } catch (e: Exception) {
        this.createdAt.substring(0, 10)
    }

    return DataClassBerita(
        idBerita = this.id,
        imgBerita = imageUrl,
        judulBerita = this.judul,
        penulisBerita = this.user?.name ?: "Tidak Dikenal",
        tanggalBerita = tanggalBerita,
        kontenBerita = this.konten
    )
}