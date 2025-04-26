package com.localclasstech.layanandesa.feature.layanan.view.surat

import android.app.DatePickerDialog
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.localclasstech.layanandesa.R
import com.localclasstech.layanandesa.databinding.FragmentSuratKtmBinding
import com.localclasstech.layanandesa.feature.layanan.data.DataClassCardSurat
import com.localclasstech.layanandesa.feature.layanan.data.network.apiservice.SuratApiService
import com.localclasstech.layanandesa.feature.layanan.data.network.data.suratktm.CreateSktmRequest
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

        val idSurat = arguments?.getInt("id_surat", -1) ?: -1
        val type = arguments?.getInt("type", Constant.TYPE_CREATE) ?: Constant.TYPE_CREATE


        if (type == Constant.TYPE_DETAIL && idSurat != -1){
            viewModel.fetchSuratKtmDetail(idSurat)
        }

        setupSpinner()
        setupDatePicker()

        // Set up date picker functionality
        binding.etTanggalLahir.setOnClickListener {
            // Only show date picker if not in detail mode or if enabled
            if (binding.etTanggalLahir.isEnabled) {
                showDatePicker()
            }
        }
        binding.backButton.setOnClickListener{
            parentFragmentManager.popBackStack()
        }

        // Observe data surat
        configureUIBasedOnType(type)
        observeDetailData(type)

        setupSubmitButton(type, idSurat)
    }

    private fun setupDatePicker() {
        binding.etTanggalLahir.setOnClickListener {
            if (binding.etTanggalLahir.isEnabled) {
                showDatePicker()
            }
        }
    }

    private fun setupSpinner() {
        val jenisKelaminItems = listOf("Laki-laki", "Perempuan")
        val statusKawinItems = listOf("Belum kawin", "Sudah kawin", "Cerai")
        val jenisKelaminAdapter = CustomSpinnerAdapter(requireContext(), jenisKelaminItems)
        binding.spinerJK.adapter = jenisKelaminAdapter
        val statusKawinAdapter = CustomSpinnerAdapter(requireContext(), statusKawinItems)
        binding.spinerSK.adapter = statusKawinAdapter
    }

    private fun configureUIBasedOnType(type: Int) {
        when (type) {
            Constant.TYPE_DETAIL -> {
                // UI elements are read-only
                binding.etNama.isEnabled = false
                binding.etTempatLahir.isEnabled = false
                binding.etTanggalLahir.isEnabled = false
                binding.spinerJK.isEnabled = false
                binding.spinerSK.isEnabled = false
                binding.etKewarganegaraan.isEnabled = false
                binding.etAlamat.isEnabled = false
                binding.etKeterangan.isEnabled = false

                // Hide submit button in detail mode
                binding.btnAjukan.visibility = View.GONE
            }
            Constant.TYPE_CREATE -> {
                // Set default text for date field
                binding.etTanggalLahir.text = "Pilih Tanggal Lahir"

                // Enable all fields for input
                binding.etNama.isEnabled = true
                binding.etTempatLahir.isEnabled = true
                binding.etTanggalLahir.isEnabled = true
                binding.spinerJK.isEnabled = true
                binding.spinerSK.isEnabled = true
                binding.etKewarganegaraan.isEnabled = true
                binding.etAlamat.isEnabled = true
                binding.etKeterangan.isEnabled = true

                // Show submit button with appropriate text
                binding.btnAjukan.text = "Ajukan Surat KTM"
                binding.btnAjukan.visibility = View.VISIBLE
            }
            Constant.TYPE_UPDATE -> {
                // Enable all fields for editing
                binding.etNama.isEnabled = true
                binding.etTempatLahir.isEnabled = true
                binding.etTanggalLahir.isEnabled = true
                binding.spinerJK.isEnabled = true
                binding.spinerSK.isEnabled = true
                binding.etKewarganegaraan.isEnabled = true
                binding.etAlamat.isEnabled = true
                binding.etKeterangan.isEnabled = true

                // Show submit button with update text
                binding.btnAjukan.text = "Perbarui Surat KTM"
                binding.btnAjukan.visibility = View.VISIBLE
            }
        }
    }
    private fun observeDetailData(type: Int) {
        viewModel.detailSuratKtm.observe(viewLifecycleOwner) { dataSktm ->
            if (dataSktm != null && (type == Constant.TYPE_DETAIL || type == Constant.TYPE_UPDATE)) {
                // Fill form with existing data
                binding.etNama.setText(dataSktm.nama)
                binding.etTempatLahir.setText(dataSktm.tempatLahir)
                binding.etTanggalLahir.setText(dataSktm.tanggalLahir)

                // Set spinner selections
                val jenisKelaminItems = listOf("Laki-laki", "Perempuan")
                val jenisKelaminIndex = jenisKelaminItems.indexOf(dataSktm.jenisKelamin)
                if (jenisKelaminIndex != -1) {
                    binding.spinerJK.setSelection(jenisKelaminIndex)
                }

                val statusKawinItems = listOf("Belum kawin", "Sudah kawin", "Cerai")
                val statusKawinIndex = statusKawinItems.indexOf(dataSktm.statusKawin)
                if (statusKawinIndex != -1) {
                    binding.spinerSK.setSelection(statusKawinIndex)
                }

                binding.etKewarganegaraan.setText(dataSktm.kewarganegaraan)
                binding.etAlamat.setText(dataSktm.alamat)
                binding.etKeterangan.setText(dataSktm.keterangan)
            }
        }
    }
    private fun setupSubmitButton(type: Int, idSurat: Int) {
        binding.btnAjukan.setOnClickListener {
            if (validateForm()) {
                val suratKtmData = collectFormData()

                when (type) {
                    Constant.TYPE_CREATE -> {
                        viewModel.createSuratKtm(suratKtmData) //Type mismatch: inferred type is SktmResponse but CreateSktmRequest was expected
                    }
                    Constant.TYPE_UPDATE -> {
                        viewModel.updateSuratKtm(idSurat, suratKtmData) //Type mismatch: inferred type is SktmResponse but CreateSktmRequest was expected
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Silakan lengkapi semua field", Toast.LENGTH_SHORT).show()
            }
        }

        // Observe creation/update result
        viewModel.operationResult.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                Toast.makeText(
                    requireContext(),
                    if (type == Constant.TYPE_CREATE) "Surat berhasil dibuat" else "Surat berhasil diperbarui",
                    Toast.LENGTH_SHORT
                ).show()

                // Navigate back to the listing
                parentFragmentManager.popBackStack()
            }
        }
    }


    private fun validateForm(): Boolean {
        // Basic validation
        return binding.etNama.text.isNotBlank() &&
                binding.etTempatLahir.text.isNotBlank() &&
                binding.etTanggalLahir.text.toString() != "Pilih Tanggal Lahir" &&
                binding.etKewarganegaraan.text.isNotBlank() &&
                binding.etAlamat.text.isNotBlank()
    }

    private fun collectFormData(): CreateSktmRequest {
        return CreateSktmRequest(
            nama = binding.etNama.text.toString(),
            tempat_lahir = binding.etTempatLahir.text.toString(),
            tanggal_lahir = binding.etTanggalLahir.text.toString(),
            jenis_kelamin = binding.spinerJK.selectedItem.toString(),
            status_kawin = binding.spinerSK.selectedItem.toString(),
            kewarganegaraan = binding.etKewarganegaraan.text.toString(),
            alamat = binding.etAlamat.text.toString(),
            keterangan = binding.etKeterangan.text.toString()
        )
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