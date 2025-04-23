package com.localclasstech.layanandesa.feature.beranda.view

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.localclasstech.layanandesa.R
import com.localclasstech.layanandesa.auth.viewmodel.LoginViewModel
import com.localclasstech.layanandesa.auth.viewmodel.LoginViewModelFactory
import com.localclasstech.layanandesa.databinding.FragmentBerandaBinding
import com.localclasstech.layanandesa.feature.beranda.view.helper.BeritaBerandaAdapterHelper
import com.localclasstech.layanandesa.feature.beranda.viewmodel.BerandaViewModel
import com.localclasstech.layanandesa.feature.beranda.viewmodel.BerandaViewModelFactory
import com.localclasstech.layanandesa.settings.PreferencesHelper

class BerandaFragment : Fragment() {

    private lateinit var preferencesHelper: PreferencesHelper
    private var _binding: FragmentBerandaBinding? = null
    private val binding get() = _binding!!
    private val loginViewModel: LoginViewModel by activityViewModels {
        LoginViewModelFactory(requireContext())
    }

    private lateinit var viewModel: BerandaViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferencesHelper = PreferencesHelper(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBerandaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val factory = BerandaViewModelFactory(loginViewModel)
        viewModel = ViewModelProvider(this, factory)[BerandaViewModel::class.java]
        val tvTanggal = view.findViewById<TextView>(R.id.tvCalender)

        viewModel.tanggalSekarang.observe(viewLifecycleOwner) { tanggal ->
            tvTanggal.text = tanggal
        }

        binding.rvBerita.layoutManager = LinearLayoutManager(requireContext())
        viewModel.beritaList.observe(viewLifecycleOwner) { beritaList ->
            binding.rvBerita.adapter = BeritaBerandaAdapterHelper(beritaList)
        }

        observeViewModel()

    }

    private fun observeViewModel() {
        loginViewModel.loginMode.observe(viewLifecycleOwner){ loginMode->
            binding.tvUsername.text = loginMode ?: "Guest"
        }
        loginViewModel.image.observe(viewLifecycleOwner) { image ->
            val baseUrl = "http://192.168.56.1:8000/storage/" //ingat untuk selalu di ganti ketika memulai project
            val fullImageUrl = if (image.startsWith("http")) image else baseUrl + image

            Log.d("Profile Image", "Full URL: $image")

            if (!image.isNullOrEmpty()) {
                Glide.with(this)
                    .load(fullImageUrl)
                    .transform(CircleCrop())
                    .into(binding.imageProfile)
            } else {
                val userName = loginViewModel.userName.value ?: "null"
                Log.d("Profile Image", "Using initials, userName: $userName")
                val initialsBitmap = getInitials(loginViewModel.userName.value)
                binding.imageProfile.setImageBitmap(initialsBitmap)
            }

        }

    }
    private fun getInitials(name: String?): Bitmap {
        val initials = if (!name.isNullOrBlank()) {
            val words = name.trim().split("\\s+".toRegex())
            when {
                words.size >= 2 -> "${words[0].first()}${words[1].first()}".uppercase()
                words.size == 1 -> "${words[0].first()}".uppercase()
                else -> "A"
            }
        } else {
            "-"
        }

        val size = 200
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint().apply {
            color = Color.parseColor("#6200EE")
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        val textPaint = Paint().apply {
            color = Color.WHITE
            textSize = 64f
            typeface = Typeface.DEFAULT_BOLD
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }

        // Buat lingkaran
        val path = Path()
        path.addCircle(size / 2f, size / 2f, size / 2f, Path.Direction.CCW)
        canvas.drawPath(path, paint)

        // Posisi teks
        val textBounds = Rect()
        textPaint.getTextBounds(initials, 0, initials.length, textBounds)
        val x = size / 2f
        val y = (size / 2f) - textBounds.exactCenterY()
        canvas.drawText(initials, x, y, textPaint)

        return bitmap
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

}