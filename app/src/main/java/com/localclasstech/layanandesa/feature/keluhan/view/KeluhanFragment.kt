package com.localclasstech.layanandesa.feature.keluhan.view

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.localclasstech.layanandesa.R
import com.localclasstech.layanandesa.databinding.FragmentKeluhanBinding
import com.localclasstech.layanandesa.feature.keluhan.data.CardListKeluhan
import com.localclasstech.layanandesa.feature.keluhan.view.helper.KeluhanAdapter
import com.localclasstech.layanandesa.feature.keluhan.viewmodel.KeluhanViewModel
import com.localclasstech.layanandesa.feature.keluhan.viewmodel.KeluhanViewModelFactory
import com.localclasstech.layanandesa.feature.layanan.view.surat.SuratKtuFragment
import com.localclasstech.layanandesa.network.RetrofitClient
import com.localclasstech.layanandesa.settings.PreferencesHelper
import com.localclasstech.layanandesa.settings.utils.Constant

class KeluhanFragment : Fragment() {
    private lateinit var preferencesHelper: PreferencesHelper
    private var _binding: FragmentKeluhanBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = KeluhanFragment()
    }

    private lateinit var viewModel: KeluhanViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferencesHelper = PreferencesHelper(requireContext())
        viewModel = ViewModelProvider(this, KeluhanViewModelFactory(
            RetrofitClient.keluhanApiService, preferencesHelper
        ))[KeluhanViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentKeluhanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButton.setOnClickListener{
            parentFragmentManager.popBackStack()
        }

        setupRecyclerView()
        observeViewModelData()
        observeLoadingState()

        binding.addKeluhan.setOnClickListener {
            navigateToCreateKeluhan(Constant.TYPE_CREATE)
        }
        viewModel.fetchKeluhans(id)
        binding.keluhanSwipeRefresh.setOnRefreshListener {
            viewModel.fetchKeluhans(id)
        }


    }

    private fun observeLoadingState() {
        viewModel.isLoading.observe(viewLifecycleOwner){ isLoading->
            if (isLoading){
                binding.shimmerLayoutKeluhan.visibility = View.VISIBLE
                binding.shimmerLayoutKeluhan.startShimmer()
            }else{
                binding.shimmerLayoutKeluhan.stopShimmer()
                binding.shimmerLayoutKeluhan.visibility = View.GONE
                binding.keluhanSwipeRefresh.isRefreshing = false
            }
            binding.keluhanSwipeRefresh.isRefreshing = false
        }
    }

    private fun observeViewModelData() {
        viewModel.listKeluhans.observe(viewLifecycleOwner){ list->
            Log.d("Recycler List Keluhan", "Jumlah Keluhan : ${list.size}")

            val adapter = binding.recyclerViewKeluhan.adapter as KeluhanAdapter
            adapter.updateDataKeluhan(list)
        }
    }

    private fun setupRecyclerView() {
        val adapter = KeluhanAdapter(
            cardListKeluhan = emptyList(),
            object : KeluhanAdapter.OnAdapterListener{
                override fun onClick(cardListKeluhan: CardListKeluhan) {
                    navigateToDetailKeluhan(cardListKeluhan.id)
                }

                override fun onUpdete(cardListKeluhan: CardListKeluhan) {
                    TODO("Not yet implemented")
                }
            }
        )
        binding.recyclerViewKeluhan.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = adapter
            visibility = View.VISIBLE
        }
    }

    private fun navigateToDetailKeluhan(id: Int) {
        val bundle = Bundle().apply {
            putInt("id", id)
            putInt("type", Constant.TYPE_DETAIL)
        }
        val fragment = DetailkeluhanFragment().apply {
            arguments = bundle
        }
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentView, fragment)
            .addToBackStack(null)
            .commit()


    }
    private fun navigateToCreateKeluhan(type: Int) {
        // Siapkan bundle dengan tipe operasi
        val bundle = Bundle().apply {
            putInt("type", type)
        }

        // Buat instance fragment baru dengan bundle
        val fragment = DetailkeluhanFragment().apply {
            arguments = bundle
        }

        // Navigasi ke fragment pembuatan surat
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentView, fragment)
            .addToBackStack(null)
            .commit()
    }
}