package com.localclasstech.layanandesa.feature.umkm.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.localclasstech.layanandesa.databinding.FragmentUmkmBinding
import com.localclasstech.layanandesa.feature.umkm.view.adapter.UmkmGridAdapter
import com.localclasstech.layanandesa.feature.umkm.view.adapter.UmkmHorizontalAdapter
import com.localclasstech.layanandesa.feature.umkm.viewmodel.UmkmViewModel
import com.localclasstech.layanandesa.feature.umkm.viewmodel.UmkmViewModelFactory
import com.localclasstech.layanandesa.network.RetrofitClient
import com.localclasstech.layanandesa.settings.PreferencesHelper

class UmkmFragment : Fragment() {

    companion object {
        fun newInstance() = UmkmFragment()
    }

    private var _binding: FragmentUmkmBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: UmkmViewModel
    private lateinit var gridAdapter: UmkmGridAdapter
    private lateinit var horizontalAdapter: UmkmHorizontalAdapter
    private lateinit var preferencesHelper: PreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize PreferencesHelper
        preferencesHelper = PreferencesHelper(requireContext())

        // Setup ViewModel with Factory
        val factory = UmkmViewModelFactory(
            RetrofitClient.umkmApiService,
            preferencesHelper
        )
        viewModel = ViewModelProvider(this, factory)[UmkmViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUmkmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapters()
        setupRecyclerViews()
        setupSwipeRefresh()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupAdapters() {
        // Grid Adapter untuk Produk Terbaru
        gridAdapter = UmkmGridAdapter { umkm ->
            // TODO: Navigate to DetailUmkmFragment
            Toast.makeText(requireContext(), "Clicked UMKM ID: ${umkm.id}", Toast.LENGTH_SHORT).show()
        }

        // Horizontal Adapter untuk Semua Produk
        horizontalAdapter = UmkmHorizontalAdapter { umkm ->
            // TODO: Navigate to DetailUmkmFragment
            Toast.makeText(requireContext(), "Clicked UMKM ID: ${umkm.id}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRecyclerViews() {
        // Setup Grid RecyclerView untuk Produk Terbaru
        with(binding.rvProdukTerbaru) {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = gridAdapter
            setHasFixedSize(true)
        }

        // Setup Horizontal RecyclerView untuk Semua Produk
        with(binding.rvSemuaProduk) {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = horizontalAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshUmkm.setOnRefreshListener {
            viewModel.refreshData()
        }
    }

    private fun setupClickListeners() {
        // Lihat Semua button
        binding.tvLihatSemua.setOnClickListener {
            // TODO: Navigate to UmkmListFragment or UmkmFullListActivity
            Toast.makeText(requireContext(), "Navigate to Full UMKM List", Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeViewModel() {
        // Observe Produk Terbaru
        viewModel.produkTerbaru.observe(viewLifecycleOwner) { umkmList ->
            gridAdapter.updateData(umkmList)
        }

        // Observe Semua Produk
        viewModel.semuaProduk.observe(viewLifecycleOwner) { umkmList ->
            horizontalAdapter.updateData(umkmList)
        }

        // Observe Loading State untuk Produk Terbaru
        viewModel.isLoadingProdukTerbaru.observe(viewLifecycleOwner) { isLoading ->
            binding.shimmerContainerProdukTerbaru.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.rvProdukTerbaru.visibility = if (isLoading) View.GONE else View.VISIBLE
        }

        // Observe Loading State untuk Semua Produk
        viewModel.isLoadingSemuaProduk.observe(viewLifecycleOwner) { isLoading ->
            binding.shimmerContainerSemuaProduk.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.rvSemuaProduk.visibility = if (isLoading) View.GONE else View.VISIBLE
        }

        // Observe Error Messages
        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
                viewModel.clearErrorMessage()
            }
        }

        // Stop SwipeRefresh when both loading states are false
        viewModel.isLoadingProdukTerbaru.observe(viewLifecycleOwner) { isLoadingTerbaru ->
            viewModel.isLoadingSemuaProduk.observe(viewLifecycleOwner) { isLoadingSemua ->
                if (!isLoadingTerbaru && !isLoadingSemua) {
                    binding.swipeRefreshUmkm.isRefreshing = false
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}