package com.localclasstech.layanandesa.feature.berita.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.localclasstech.layanandesa.R
import com.localclasstech.layanandesa.databinding.FragmentBeritaBinding
import com.localclasstech.layanandesa.feature.berita.data.DataClassBerita
import com.localclasstech.layanandesa.feature.berita.view.helper.ListBeritaLainnyaAdapter
import com.localclasstech.layanandesa.feature.berita.view.helper.ListBeritaTerkiniAdapter
import com.localclasstech.layanandesa.feature.berita.viewmodel.BeritaViewModel
import com.localclasstech.layanandesa.feature.berita.viewmodel.BeritaViewModelFactory
import com.localclasstech.layanandesa.feature.pengaturan.viewmodel.SharedThemeViewModel
import com.localclasstech.layanandesa.feature.pengaturan.viewmodel.SharedThemeViewModelFactory
import com.localclasstech.layanandesa.settings.PreferencesHelper

class BeritaFragment : Fragment() {
    private var _binding: FragmentBeritaBinding? = null
    private val binding get() = _binding!!
    private lateinit var preferencesHelper: PreferencesHelper
    private lateinit var beritaAdapter: ListBeritaTerkiniAdapter
    private lateinit var beritaAdapterLainnya: ListBeritaLainnyaAdapter

    private val sharedThemeViewModel: SharedThemeViewModel by activityViewModels(){
        SharedThemeViewModelFactory(preferencesHelper)
    }

    companion object {
        fun newInstance() = BeritaFragment()
    }

    private lateinit var viewModel: BeritaViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferencesHelper = PreferencesHelper(requireContext())
        val factory = BeritaViewModelFactory(requireContext())
        viewModel = ViewModelProvider(this, factory)[BeritaViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBeritaBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerViewBeritaTerkini()
        setupRecyclerViewBerita()
        setupSwipeRefresh()
        observeViewModel()
        showShimmer()
        viewModel.fetchBeritaTerkini()
        viewModel.fetchBerita()
    }

    private fun showShimmer() {
        binding.shimmerContainerBeritaTerkini.visibility = View.VISIBLE
        binding.shimmerContainerBeritaLainnya.visibility = View.VISIBLE
        binding.rvBeritaTerkini.visibility = View.GONE
        binding.rvBeritaLainnya.visibility = View.GONE
    }

    private fun hideShimmer() {
        binding.shimmerContainerBeritaTerkini.visibility = View.GONE
        binding.shimmerContainerBeritaLainnya.visibility = View.GONE
        binding.rvBeritaTerkini.visibility = View.VISIBLE
        binding.rvBeritaLainnya.visibility = View.VISIBLE
    }

    private fun setupRecyclerViewBeritaTerkini() {
        beritaAdapter = ListBeritaTerkiniAdapter(emptyList(), object : ListBeritaTerkiniAdapter.OnAdapterListener {
            override fun onClick(beritaId: Int) {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragmentView, DetailBeritaFragment.newInstance(beritaId))
                    .addToBackStack(null)
                    .commit()
            }
        }, lifecycleScope,
            requireContext()
        )

        binding.rvBeritaTerkini.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = beritaAdapter
        }
    }
    private fun setupRecyclerViewBerita() {
        beritaAdapterLainnya = ListBeritaLainnyaAdapter(emptyList(), object : ListBeritaLainnyaAdapter.OnAdapterListener {
            override fun onClick(beritaId: Int) {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragmentView, DetailBeritaFragment.newInstance(beritaId))
                    .addToBackStack(null)
                    .commit()
            }
        }, lifecycleScope,
            requireContext()
        )

        binding.rvBeritaLainnya.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = beritaAdapterLainnya
        }
    }
    private fun setupSwipeRefresh() {
        binding.swipeRefreshBerita.setOnRefreshListener {
            viewModel.refreshData()
        }
    }
    private fun observeViewModel() {
        viewModel.beritaTerkiniUiList.observe(viewLifecycleOwner) { dataList ->
            if (dataList.isNotEmpty()) {
                beritaAdapter.setData(dataList)
                hideShimmer()
                binding.swipeRefreshBerita.isRefreshing = false
            } else {
                showEmptyState()
                binding.swipeRefreshBerita.isRefreshing = false
            }
        }

        viewModel.beritaUiList.observe(viewLifecycleOwner) { dataList ->
            if (dataList.isNotEmpty()) {
                beritaAdapterLainnya.setData(dataList)
                hideShimmer()
                binding.swipeRefreshBerita.isRefreshing = false
            } else {
                showEmptyState()
                binding.swipeRefreshBerita.isRefreshing = false
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                showCustomToast(it)
                hideShimmer()
                binding.swipeRefreshBerita.isRefreshing = false
                showEmptyState()
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                showShimmer()
            } else {
                hideShimmer()
            }
        }
    }
    // Fungsi untuk menampilkan toast kustom
    private fun showCustomToast(message: String) {
        val toast = Toast.makeText(requireContext(), message, Toast.LENGTH_LONG)
        val view = toast.view
        view?.setBackgroundResource(R.drawable.toast_error_background) // Buat drawable untuk background toast
        val text = view?.findViewById<TextView>(android.R.id.message)
        text?.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        toast.show()
    }
    // Fungsi untuk menampilkan state kosong
    private fun showEmptyState() {
        // Sembunyikan RecyclerView
        binding.rvBeritaTerkini.visibility = View.GONE
        binding.rvBeritaLainnya.visibility = View.GONE

        // Tampilkan view kosong atau pesan
        val emptyView = layoutInflater.inflate(R.layout.view_empty_state, null)
        val emptyTextView = emptyView.findViewById<TextView>(R.id.tvEmptyState)
        emptyTextView.text = "Ups! Tidak ada berita yang tersedia.\nCoba periksa koneksi internet Anda."

        // Tambahkan view kosong ke layout
        (binding.root as ViewGroup).addView(emptyView)
    }



}