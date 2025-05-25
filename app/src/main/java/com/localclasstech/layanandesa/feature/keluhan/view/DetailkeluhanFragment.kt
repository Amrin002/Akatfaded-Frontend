package com.localclasstech.layanandesa.feature.keluhan.view

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.localclasstech.layanandesa.R
import com.localclasstech.layanandesa.databinding.FragmentDetailkeluhanBinding
import com.localclasstech.layanandesa.feature.keluhan.data.KeluhanRequest
import com.localclasstech.layanandesa.feature.keluhan.viewmodel.DetailkeluhanViewModel
import com.localclasstech.layanandesa.feature.keluhan.viewmodel.DetailkeluhanViewModelFactory
import com.localclasstech.layanandesa.settings.utils.Constant
import com.localclasstech.layanandesa.settings.utils.DialogHelper

class DetailkeluhanFragment : Fragment() {
    private var _binding : FragmentDetailkeluhanBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = DetailkeluhanFragment()
    }

    private lateinit var viewModel: DetailkeluhanViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val factory = DetailkeluhanViewModelFactory(requireContext())
        viewModel = ViewModelProvider(this, factory)[DetailkeluhanViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailkeluhanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        val id = arguments?.getInt("id", -1) ?: -1
        val type = arguments?.getInt("type", Constant.TYPE_CREATE) ?: Constant.TYPE_CREATE

        if ((type == Constant.TYPE_DETAIL || type == Constant.TYPE_UPDATE) && id != -1){
            Log.d("Keluhan Fragment", "Fetching Detail for id : $id, Type: $type")
            viewModel.fetchKeluhanDetail(id)
        }
        binding.btnEditKeluhan.setOnClickListener{
            val bundle = Bundle().apply {
                putInt("id", id)
                putInt("type", Constant.TYPE_UPDATE)
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentView, DetailkeluhanFragment::class.java, bundle)
                .addToBackStack(null)
                .commit()
        }
        binding.btnDeleteKeluhan.setOnClickListener{
            showDeleteConfirmationDialog(id)
        }

        setupSubmitButton(type, id)
        observeDetailData(type)
        configureUIBasedOnType(type)
    }

    fun validateForm(): Boolean{
        return binding.etJudul.text.isNotBlank() &&
                binding.etIsiKeluhan.text.isNotBlank()
    }
    private fun showDeleteConfirmationDialog(id: Int) {
        DialogHelper.showConfirmationDialog(
            requireContext(),
            "Apakah anda yakin ingin menghapus surat domisili ini?",
            onConfirm = {
                viewModel.deleteKeluhan(id)
            }
        )
    }
    private fun configureUIBasedOnType(type: Int) {
        when(type){
            Constant.TYPE_CREATE -> {
                binding.btnEditKeluhan.visibility = View.GONE
                binding.btnDeleteKeluhan.visibility = View.GONE
                binding.btnAjukan.visibility = View.VISIBLE
                binding.etJudul.isEnabled = true
                binding.etIsiKeluhan.isEnabled = true
                binding.tvKeluhan.text = "Buat Keluhan"
            }
            Constant.TYPE_DETAIL -> {
                binding.btnEditKeluhan.visibility = View.VISIBLE
                binding.btnDeleteKeluhan.visibility = View.VISIBLE
                binding.btnAjukan.visibility = View.GONE
                binding.etJudul.isEnabled = false
                binding.etIsiKeluhan.isEnabled = false
                binding.tvKeluhan.text = "Detail Keluhan"
            }
            Constant.TYPE_UPDATE -> {
                binding.btnEditKeluhan.visibility = View.GONE
                binding.btnDeleteKeluhan.visibility = View.GONE
                binding.etJudul.isEnabled = true
                binding.etIsiKeluhan.isEnabled = true
                binding.btnAjukan.visibility = View.VISIBLE
                binding.btnAjukan.text = "Perbarui Keluhan"
                binding.tvKeluhan.text = "Perbarui Keluhan"
            }

        }
    }

    private fun observeDetailData(type: Int) {
        Log.d("Keluhan Fragment", "Observing Detail Data: $type")
        viewModel.deleteResult.observe(viewLifecycleOwner){ isSuccess->
            if (isSuccess){
                Toast.makeText(requireContext(), "Keluhan berhasil dihapus", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            } else {
                Toast.makeText(requireContext(), "Gagal menghapus keluhan", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.detailKeluhan.observe(viewLifecycleOwner){ dataKeluhan->
            Log.d("Detail Keluhan Fragment", "Data Keluhan: $dataKeluhan")
            if (dataKeluhan != null && (type == Constant.TYPE_DETAIL || type == Constant.TYPE_UPDATE)){
                binding.etJudul.setText(dataKeluhan.judul)
                binding.etIsiKeluhan.setText(dataKeluhan.isi)
                Log.d("Detail Keluhan Fragment", "Form Di isi")
            }
            if (type == Constant.TYPE_DETAIL){
                binding.btnAjukan.visibility = View.GONE
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
            Log.d("Detail Keluhan Fragment", "Error Message: $errorMessage")
        }
    }

    private fun setupSubmitButton(type: Int, id: Int) {
        binding.btnAjukan.setOnClickListener {
            val curentStatus = viewModel.detailKeluhan.value?.status
            if (type == Constant.TYPE_DETAIL && curentStatus == "diproses"){
                //
                Toast.makeText(requireContext(), "Keluhan anda sedang diproses", Toast.LENGTH_SHORT).show()
            } else if (validateForm()){
                val keluhanData = collectFromData()
                when(type){
                    Constant.TYPE_CREATE->{
                    viewModel.createKeluhan(keluhanData)
                    }
                    Constant.TYPE_UPDATE->{
                        viewModel.updateKeluhan(id, keluhanData)
                    }

                }
            }else{
                Toast.makeText(requireContext(), "Mohon lengkapi data", Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.operationResult.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess){
                Toast.makeText(requireContext(),
                    if (type ==Constant.TYPE_CREATE) "Keluhan Berhasil Dibuat" else "Keluhan berhasil Di perbarui", Toast.LENGTH_SHORT
                ).show()
                parentFragmentManager.popBackStack()
            }
        }
    }

    private fun collectFromData(): KeluhanRequest {
        return KeluhanRequest(
            judul = binding.etJudul.text.toString(),
            isi = binding.etIsiKeluhan.text.toString()
        )
    }
}