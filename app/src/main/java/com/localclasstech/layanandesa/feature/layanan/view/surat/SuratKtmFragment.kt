package com.localclasstech.layanandesa.feature.layanan.view.surat

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.localclasstech.layanandesa.R
import com.localclasstech.layanandesa.databinding.FragmentSuratKtmBinding
import com.localclasstech.layanandesa.feature.layanan.data.DataClassCardSurat
import com.localclasstech.layanandesa.feature.layanan.data.network.apiservice.SuratApiService
import com.localclasstech.layanandesa.feature.layanan.data.network.data.suratktm.SktmResponse
import com.localclasstech.layanandesa.feature.layanan.data.repository.SuratKtmRepository
import com.localclasstech.layanandesa.feature.layanan.viewmodel.surat.SuratKtmViewModel
import com.localclasstech.layanandesa.feature.layanan.viewmodel.surat.SuratKtmViewModelFactory
import com.localclasstech.layanandesa.network.RetrofitClient
import com.localclasstech.layanandesa.settings.PreferencesHelper
import com.localclasstech.layanandesa.settings.utils.Constant

class SuratKtmFragment : Fragment() {
    private var _binding: FragmentSuratKtmBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = SuratKtmFragment()
    }

    private lateinit var viewModel: SuratKtmViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inisialisasi apiService dan preferencesHelper secara manual
        val apiService = RetrofitClient.suratApiService   // Unresolved reference: create
        val preferencesHelper = PreferencesHelper(requireContext()) // Sesuaikan cara Anda menginisialisasi preferencesHelper

        // Inisialisasi repository dengan apiService dan preferencesHelper
        val repository = SuratKtmRepository(apiService, preferencesHelper)

        // Buat ViewModel dengan factory
        val factory = SuratKtmViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(SuratKtmViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSuratKtmBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Ambil id_surat dari bundle
        val idSurat = arguments?.getInt("id_surat") ?: return
        val type = arguments?.getInt("type")
    // Panggil ViewModel untuk mengambil data detail surat berdasarkan id
        viewModel.fetchSuratKtmDetail(idSurat)
        val jenisKelaminItems = listOf("Laki-laki", "Perempuan")
        val statusKawinItems = listOf("Kawin", "Belum Kawin")

        // Observe data surat
        viewModel.detailSuratKtm.observe(viewLifecycleOwner) { dataSktm ->
            if (type == Constant.TYPE_DETAIL && dataSktm != null) {
                // Set data ke EditText/TextView sesuai dengan dataSktm
                binding.etNama.setText(dataSktm.nama)
                binding.etNama.isEnabled = false
                binding.etTempatLahir.setText(dataSktm.tempatLahir)
                binding.etTempatLahir.isEnabled = false
                binding.etTanggalLahir.setText(dataSktm.tanggalLahir)
                binding.etTanggalLahir.isEnabled = false

                // Untuk Spinner jenisKelamin, cari index dari jenisKelamin dan set selection
                val jenisKelaminIndex = jenisKelaminItems.indexOf(dataSktm.jenisKelamin)
                if (jenisKelaminIndex != -1) {
                    binding.spinerJK.setSelection(jenisKelaminIndex)
                }

                // Untuk Spinner statusKawin, cari index dari statusKawin dan set selection
                val statusKawinIndex = statusKawinItems.indexOf(dataSktm.statusKawin)
                if (statusKawinIndex != -1) {
                    binding.spinerSK.setSelection(statusKawinIndex)
                }
                binding.etKewarganegaraan.setText(dataSktm.kewarganegaraan)
                binding.etKewarganegaraan.isEnabled = false
                binding.etAlamat.setText(dataSktm.alamat)
                binding.etAlamat.isEnabled=false
                binding.etKeterangan.setText(dataSktm.keterangan)
                binding.etKeterangan.isEnabled=false
                // Sembunyikan tombol ajukan jika hanya menampilkan detail
                binding.btnAjukan.visibility = View.GONE
            }
        }
    }

}