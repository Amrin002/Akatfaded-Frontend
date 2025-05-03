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
import com.localclasstech.layanandesa.feature.berita.view.DetailBeritaFragment
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
        val factory = BerandaViewModelFactory(loginViewModel) //No value passed for parameter 'context'
        viewModel = ViewModelProvider(this, factory)[BerandaViewModel::class.java]
        val tvTanggal = view.findViewById<TextView>(R.id.tvCalender)

        viewModel.tanggalSekarang.observe(viewLifecycleOwner) { tanggal ->
            tvTanggal.text = tanggal
        }
        setupRecyclerView()



        observeViewModel()

    }

    private fun setupRecyclerView() {
        binding.rvBerita.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun observeViewModel() {
        viewModel.beritaList.observe(viewLifecycleOwner) { beritaList ->
            // Create adapter with click listener
            val adapter = BeritaBerandaAdapterHelper(beritaList, object : BeritaBerandaAdapterHelper.OnAdapterListener {
                override fun onClick(beritaId: Int) {
                    // Navigate to DetailBeritaFragment
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragmentView, DetailBeritaFragment.newInstance(beritaId))
                        .addToBackStack(null)
                        .commit()
                }
            })
            binding.rvBerita.adapter = adapter
        }

        loginViewModel.loginMode.observe(viewLifecycleOwner){ loginMode->
            binding.tvUsername.text = loginMode ?: "Guest"
        }

        // Observe loading state
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                showShimmer()
            } else {
                hideShimmer()
            }
        }

        loginViewModel.image.observe(viewLifecycleOwner) { image ->
            val baseUrl = "http://192.168.56.1:8000/storage/" //ingat untuk selalu di ganti ketika memulai project
            val fullImageUrl = if (image.startsWith("http")) image else baseUrl + image

            if (!image.isNullOrEmpty()) {
                Glide.with(this)
                    .load(fullImageUrl)
                    .transform(CircleCrop())
                    .into(binding.imageProfile)
            } else {
                binding.imageProfile.setImageResource(R.drawable.ic_profile_default)
            }
        }
    }
    private fun showShimmer() {
        // Sembunyikan RecyclerView
        binding.rvBerita.visibility = View.GONE

        // Tampilkan shimmer layout
        binding.shimerBeritaBeranda.root.visibility = View.VISIBLE
    }

    private fun hideShimmer() {
        // Tampilkan RecyclerView
        binding.rvBerita.visibility = View.VISIBLE

        // Sembunyikan shimmer layout
        binding.shimerBeritaBeranda.root.visibility = View.GONE
    }


    override fun onDestroyView() {
        super.onDestroyView()
    }

}