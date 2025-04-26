package com.localclasstech.layanandesa.feature.layanan.view.surat

import android.app.DatePickerDialog
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
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
import com.localclasstech.layanandesa.settings.utils.CustomSpinnerAdapter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class SuratKtmFragment : Fragment() {
    private var _binding: FragmentSuratKtmBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = SuratKtmFragment()
    }

    private lateinit var viewModel: SuratKtmViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val factory = SuratKtmViewModelFactory(requireContext())
        viewModel = ViewModelProvider(this, factory)[SuratKtmViewModel::class.java]
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

        val idSurat = arguments?.getInt("id_surat") ?: return
        val type = arguments?.getInt("type")

        viewModel.fetchSuratKtmDetail(idSurat)

        val jenisKelaminItems = listOf("Laki-laki", "Perempuan")
        val statusKawinItems = listOf("Belum kawin", "Sudah kawin", "Cerai")
        val jenisKelaminAdapter = CustomSpinnerAdapter(requireContext(), jenisKelaminItems)
        binding.spinerJK.adapter = jenisKelaminAdapter

        // Set up date picker functionality
        binding.etTanggalLahir.setOnClickListener {
            // Only show date picker if not in detail mode or if enabled
            if (binding.etTanggalLahir.isEnabled) {
                showDatePicker()
            }
        }

        val statusKawinAdapter = CustomSpinnerAdapter(requireContext(), statusKawinItems)
        binding.spinerSK.adapter = statusKawinAdapter
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
                // Disable spinner in detail mode
                binding.spinerJK.isEnabled = false

                // Untuk Spinner statusKawin, cari index dari statusKawin dan set selection
                val statusKawinIndex = statusKawinItems.indexOf(dataSktm.statusKawin)
                if (statusKawinIndex != -1) {
                    binding.spinerSK.setSelection(statusKawinIndex)
                }
                // Disable spinner in detail mode
                binding.spinerSK.isEnabled = false
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

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        // Set to 18 years ago as default if creating new data
        calendar.add(Calendar.YEAR, -18)

        val currentText = binding.etTanggalLahir.text.toString()
        if (currentText != "Pilih Tanggal Lahir") {
            try {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val date = dateFormat.parse(currentText)
                if (date != null) {
                    calendar.time = date
                }
            } catch (e: Exception) {
                // If date parsing fails, just use the default (18 years ago)
            }
        }

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                // Format the date
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(selectedYear, selectedMonth, selectedDay)
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val formattedDate = dateFormat.format(selectedCalendar.time)

                // Set the formatted date to TextView
                binding.etTanggalLahir.text = formattedDate
            },
            year, month, day
        )

        // Set maximum date to today (no future dates)
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()

        datePickerDialog.show()
    }

}