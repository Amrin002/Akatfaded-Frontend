package com.localclasstech.layanandesa.feature.umkm.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.localclasstech.layanandesa.R
import com.localclasstech.layanandesa.databinding.FragmentKelolaUmkmBinding
import com.localclasstech.layanandesa.feature.umkm.data.CardUmkm
import com.localclasstech.layanandesa.feature.umkm.view.adapter.UmkmAdapter
import com.localclasstech.layanandesa.feature.umkm.viewmodel.KelolaUmkmViewModel
import com.localclasstech.layanandesa.feature.umkm.viewmodel.KelolaUmkmViewModelFactory
import com.localclasstech.layanandesa.settings.utils.Constant
import com.localclasstech.layanandesa.settings.utils.DialogHelper

class KelolaUmkmFragment : Fragment() {
    private var _binding: FragmentKelolaUmkmBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: KelolaUmkmViewModel
    private lateinit var umkmAdapter: UmkmAdapter

    companion object {
        fun newInstance() = KelolaUmkmFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val factory = KelolaUmkmViewModelFactory(requireContext())
        viewModel = ViewModelProvider(this, factory)[KelolaUmkmViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentKelolaUmkmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.addUmkm.setOnClickListener {
            navigateToCreateUmkm()
        }

        setupRecyclerView()
        observeViewModel()

        // Load data UMKM user
        viewModel.fetchMyUmkm()

        // Setup swipe refresh
        binding.umkmSwipeRefresh.setOnRefreshListener {
            viewModel.fetchMyUmkm()
        }
    }

    private fun setupRecyclerView() {
        umkmAdapter = UmkmAdapter(
            umkmList = emptyList(),
            object : UmkmAdapter.OnAdapterListener {
                override fun onClick(umkm: CardUmkm) {
                    // Navigate to detail (bisa tambahkan nanti)
                    // navigateToDetailUmkm(umkm.id)
                }

                override fun onEdit(umkm: CardUmkm) {
                    navigateToEditUmkm(umkm.id)
                }

                override fun onDelete(umkm: CardUmkm) {
                    showDeleteConfirmation(umkm)
                }
            }
        )

        binding.recyclerViewUmkm.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = umkmAdapter
        }
    }

    private fun observeViewModel() {
        // Observe loading state
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.shimmerLayoutUmkm.visibility = View.VISIBLE
                binding.shimmerLayoutUmkm.startShimmer()
                binding.recyclerViewUmkm.visibility = View.GONE
            } else {
                binding.shimmerLayoutUmkm.stopShimmer()
                binding.shimmerLayoutUmkm.visibility = View.GONE
                binding.umkmSwipeRefresh.isRefreshing = false
                binding.recyclerViewUmkm.visibility = View.VISIBLE
            }
        }

        // Observe UMKM list
        viewModel.listUmkm.observe(viewLifecycleOwner) { umkmList ->
            if (umkmList.isEmpty()) {
                // User belum punya UMKM - redirect ke daftar
                showEmptyStateAndRedirect()
            } else {
                // User sudah punya UMKM - tampilkan list
                umkmAdapter.updateData(umkmList)
            }
        }

        // Observe error message
        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            if (error.isNotEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()
            }
        }

        // Observe delete result
        viewModel.deleteResult.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(requireContext(), "UMKM berhasil dihapus", Toast.LENGTH_SHORT).show()
                // Data akan auto refresh dari viewModel
            }
        }
    }

    private fun showEmptyStateAndRedirect() {
        // Hide recycler view, show empty state
        binding.recyclerViewUmkm.visibility = View.GONE

        // Show dialog untuk first time user
        DialogHelper.showConfirmationDialog(
            requireContext(),
            "Belum Ada UMKM",
            "Anda belum mendaftarkan UMKM. Daftarkan UMKM pertama Anda sekarang?",
            onPositive = {
                navigateToCreateUmkm()
            },
            onNegative = {
                // User cancel, kembali ke halaman sebelumnya
                parentFragmentManager.popBackStack()
            }
        )
    }

    private fun showDeleteConfirmation(umkm: CardUmkm) {
        if (!umkm.canDelete) {
            Toast.makeText(
                requireContext(),
                "UMKM yang sudah disetujui tidak dapat dihapus",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val message = "Apakah Anda yakin ingin menghapus UMKM \"${umkm.namaUsaha} - ${umkm.namaProduk}\"?"

        DialogHelper.showConfirmationDialog(
            requireContext(),
            "Hapus UMKM",
            message,
            onPositive = {
                viewModel.deleteUmkm(umkm.id)
            }
        )
    }

    private fun navigateToCreateUmkm() {
        val bundle = Bundle().apply {
            putInt("type", Constant.TYPE_CREATE)
        }
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentView, DaftarUmkmFragment::class.java, bundle)
            .addToBackStack(null)
            .commit()
    }

    private fun navigateToEditUmkm(umkmId: Int) {
        val bundle = Bundle().apply {
            putInt("umkm_id", umkmId)
            putInt("type", Constant.TYPE_UPDATE)
        }
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentView, DaftarUmkmFragment::class.java, bundle)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.shimmerLayoutUmkm.stopShimmer()
        _binding = null
    }
}