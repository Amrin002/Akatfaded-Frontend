package com.localclasstech.layanandesa.settings.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

object ImageLoader {
    private const val TAG = "ImageLoader"

    suspend fun loadImage(
        context: Context,
        imageUrl: String?,
        imageView: ImageView
    ) {
        // Validasi URL
        val validUrl = imageUrl?.takeIf { it.isNotBlank() }
            ?: run {
                Log.w(TAG, "Empty image URL")
                imageView.setImageResource(android.R.drawable.ic_dialog_alert)
                return
            }

        try {
            // Coba metode download manual dengan coroutine
            withContext(Dispatchers.Main) {
                Glide.with(context)
                    .load(validUrl)
                    .override(800, 600) // Tetapkan ukuran maksimum
                    .centerCrop() // Potong gambar agar sesuai
                    .diskCacheStrategy(DiskCacheStrategy.ALL) // Simpan cache
                    .skipMemoryCache(false)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>,
                            isFirstResource: Boolean
                        ): Boolean {
                            Log.e(TAG, "Image load failed: ${e?.message}")
                            e?.logRootCauses(TAG)
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable,
                            model: Any,
                            target: Target<Drawable>,
                            dataSource: DataSource,
                            isFirstResource: Boolean
                        ): Boolean {
                            Log.d(TAG, "Image loaded successfully")
                            return false
                        }
                    })
                    .into(imageView)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading image: ${e.message}")
            imageView.setImageResource(android.R.drawable.ic_dialog_alert)
        }
    }

    private suspend fun downloadBitmap(imageUrl: String): Bitmap? = withContext(Dispatchers.IO) {
        try {
            val connection = URL(imageUrl).openConnection() as HttpURLConnection
            connection.apply {
                connectTimeout = 15000
                readTimeout = 15000
                requestMethod = "GET"
                doInput = true
            }

            // Periksa kode respon
            if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                Log.e(TAG, "HTTP error: ${connection.responseCode}")
                return@withContext null
            }

            // Baca input stream
            connection.inputStream.use { inputStream ->
                // Decode bitmap dengan opsi untuk mengurangi ukuran
                val options = BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }
                BitmapFactory.decodeStream(inputStream, null, options)

                // Hitung sample size untuk mengurangi ukuran bitmap
                options.inSampleSize = calculateInSampleSize(options, 800, 800)
                options.inJustDecodeBounds = false

                // Buka koneksi ulang karena input stream sudah ditutup
                val reconnection = URL(imageUrl).openConnection() as HttpURLConnection
                reconnection.apply {
                    connectTimeout = 15000
                    readTimeout = 15000
                    requestMethod = "GET"
                    doInput = true
                }

                reconnection.inputStream.use { newInputStream ->
                    BitmapFactory.decodeStream(newInputStream, null, options)
                }
            }
        } catch (e: IOException) {
            Log.e(TAG, "IO Error downloading bitmap: ${e.message}")
            null
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error downloading bitmap: ${e.message}")
            null
        }
    }

    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        // Raw height and width of image
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    private fun loadImageWithGlide(
        context: Context,
        imageUrl: String,
        imageView: ImageView
    ) {
        Glide.with(context)
            .load(imageUrl)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {
                    Log.e(TAG, "Glide image load failed: ${e?.message}")
                    e?.logRootCauses(TAG)
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    Log.d(TAG, "Glide image loaded successfully")
                    return false
                }
            })
            .into(object : CustomTarget<Drawable>() {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    imageView.setImageDrawable(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    imageView.setImageDrawable(
                        placeholder ?: ContextCompat.getDrawable(context, android.R.drawable.ic_dialog_alert)
                    )
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    imageView.setImageDrawable(
                        errorDrawable ?: ContextCompat.getDrawable(context, android.R.drawable.ic_dialog_alert)
                    )
                }
            })
    }
}