package com.localclasstech.layanandesa.feature.layanan.view.surat

import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.net.Uri
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.localclasstech.layanandesa.R
import com.localclasstech.layanandesa.databinding.FragmentSuratKtuBinding
import com.localclasstech.layanandesa.feature.layanan.data.network.data.suratktu.CreateSuratKtuRequest
import com.localclasstech.layanandesa.feature.layanan.viewmodel.surat.SuratKtmViewModel
import com.localclasstech.layanandesa.feature.layanan.viewmodel.surat.SuratKtmViewModelFactory
import com.localclasstech.layanandesa.feature.layanan.viewmodel.surat.SuratKtuViewModel
import com.localclasstech.layanandesa.feature.layanan.viewmodel.surat.SuratKtuViewModelFactory
import com.localclasstech.layanandesa.settings.utils.Constant
import com.localclasstech.layanandesa.settings.utils.CustomSpinnerAdapter
import com.localclasstech.layanandesa.settings.utils.DialogHelper
import java.text.SimpleDateFormat
import java.util.Locale

class SuratKtuFragment : Fragment() {
    private var _binding: FragmentSuratKtuBinding? = null
    private val binding get() = _binding!!
    companion object {
        fun newInstance() = SuratKtuFragment()
    }

    private lateinit var viewModel: SuratKtuViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val factory = SuratKtuViewModelFactory(requireContext())
        viewModel = ViewModelProvider(this, factory)[SuratKtuViewModel::class.java]
        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSuratKtuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val idSurat = arguments?.getInt("id_surat", -1) ?: -1
        val type = arguments?.getInt("type", Constant.TYPE_CREATE) ?: Constant.TYPE_CREATE

        // Fetch data for both DETAIL and UPDATE modes
        if ((type == Constant.TYPE_DETAIL || type == Constant.TYPE_UPDATE) && idSurat != -1) {
            viewModel.fetchSuratKtuDetail(idSurat)
        }
        setupSpinner()
        setupDatePicker()

        // Date picker click listener - now using the CardView
        binding.datePickerLayout.setOnClickListener {
            if (binding.etTanggalLahir.isEnabled) {
                showDatePicker()
            }
        }

        binding.btnEditSurat.setOnClickListener{
            val bundle = Bundle().apply {
                putInt("id_surat", idSurat)
                putInt("type", Constant.TYPE_UPDATE)
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentView, SuratKtuFragment::class.java, bundle)
                .addToBackStack(null)
                .commit()
        }

        // Fixed: changed from backButton to back_button to match the XML ID
        binding.backButton.setOnClickListener{
            parentFragmentManager.popBackStack()
        }

        binding.btnDeleteSurat.setOnClickListener {
            showDeleteConfirmationDialog(idSurat)
        }
        // Observe changes
        configureUIBasedOnType(type)
        observeDetailData(type)
        setupSubmitButton(type, idSurat)

    }
    private fun setupSpinner() {
        val jenisKelaminItems = listOf("Laki-laki", "Perempuan")

        val jenisKelaminAdapter = CustomSpinnerAdapter(requireContext(), jenisKelaminItems)
        binding.spinerJK.adapter = jenisKelaminAdapter

    }
    private fun configureUIBasedOnType(type: Int) {
        when (type) {
            Constant.TYPE_CREATE -> {
                binding.btnEditSurat.visibility = View.GONE
                binding.btnDeleteSurat.visibility = View.GONE
                binding.btnAjukan.visibility = View.VISIBLE
                binding.btnAjukan.text = "Ajukan Surat"

                // Enable semua field input
                binding.etNama.isEnabled = true
                binding.etTempatLahir.isEnabled = true
                binding.etTanggalLahir.isEnabled = true
                binding.spinerJK.isEnabled = true
                binding.etKewarganegaraan.isEnabled = true
                binding.etAgama.isEnabled = true
                binding.etPekerjaan.isEnabled = true
                binding.etAlamat.isEnabled = true
                binding.etNamaTempatUsaha.isEnabled = true
                binding.etJenisUsaha.isEnabled = true
                binding.etAlamatUsaha.isEnabled = true
                binding.etPemilikUsaha.isEnabled = true
                binding.etKeterangan.isEnabled = true
            }
            Constant.TYPE_DETAIL -> {
                // Set semua field disabled
                binding.etNama.isEnabled = false
                binding.etTempatLahir.isEnabled = false
                binding.etTanggalLahir.isEnabled = false
                binding.spinerJK.isEnabled = false
                binding.etKewarganegaraan.isEnabled = false
                binding.etAgama.isEnabled = false
                binding.etPekerjaan.isEnabled = false
                binding.etAlamat.isEnabled = false
                binding.etNamaTempatUsaha.isEnabled = false
                binding.etJenisUsaha.isEnabled = false
                binding.etAlamatUsaha.isEnabled = false
                binding.etPemilikUsaha.isEnabled = false
                binding.etKeterangan.isEnabled = false

                binding.btnAjukan.visibility = View.GONE
                // tombol Edit dan Delete akan diatur di observeDetailData berdasarkan status surat
            }
            Constant.TYPE_UPDATE -> {
                binding.btnEditSurat.visibility = View.GONE
                binding.btnDeleteSurat.visibility = View.GONE
                binding.btnAjukan.visibility = View.VISIBLE
                binding.btnAjukan.text = "Perbarui Surat"

                // Enable semua field input
                binding.etNama.isEnabled = true
                binding.etTempatLahir.isEnabled = true
                binding.etTanggalLahir.isEnabled = true
                binding.spinerJK.isEnabled = true
                binding.etKewarganegaraan.isEnabled = true
                binding.etAgama.isEnabled = true
                binding.etPekerjaan.isEnabled = true
                binding.etAlamat.isEnabled = true
                binding.etNamaTempatUsaha.isEnabled = true
                binding.etJenisUsaha.isEnabled = true
                binding.etAlamatUsaha.isEnabled = true
                binding.etPemilikUsaha.isEnabled = true
                binding.etKeterangan.isEnabled = true
            }
        }
    }

    private fun observeDetailData(type: Int) {
        viewModel.deleteResult.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                Toast.makeText(requireContext(), "Surat berhasil dihapus", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            } else {
                Toast.makeText(requireContext(), "Gagal menghapus surat", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.btnAjukan.isEnabled = !isLoading
            binding.progressBarButton.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnAjukan.text = if (isLoading) "" else when(type){
                Constant.TYPE_CREATE -> "Ajukan Surat KTU"
                Constant.TYPE_UPDATE -> "Perbarui Surat KTU"
                Constant.TYPE_DETAIL -> "Unduh Surat"
                else -> ""
            }

        }
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
            }
        }

        viewModel.detailSuratKtu.observe(viewLifecycleOwner) { dataSktu ->
            if (dataSktu != null && (type == Constant.TYPE_DETAIL || type == Constant.TYPE_UPDATE)) {
                // Isi form dengan data dari detail surat - Fixed: use setText for TextInputEditText
                binding.etNama.setText(dataSktu.nama)
                binding.etTempatLahir.setText(dataSktu.tempatLahir)
                binding.etTanggalLahir.text = dataSktu.tanggalLahir // For TextView in date picker
                binding.spinerJK.setSelection(
                    listOf(
                        "Laki-laki",
                        "Perempuan"
                    ).indexOf(dataSktu.jenisKelamin)
                )
                binding.etKewarganegaraan.setText(dataSktu.kewarganegaraan)
                binding.etAgama.setText(dataSktu.agama)
                binding.etPekerjaan.setText(dataSktu.pekerjaan)
                binding.etAlamat.setText(dataSktu.alamat)
                binding.etNamaTempatUsaha.setText(dataSktu.namaUsaha)
                binding.etJenisUsaha.setText(dataSktu.jenisUsaha)
                binding.etAlamatUsaha.setText(dataSktu.alamatUsaha)
                binding.etPemilikUsaha.setText(dataSktu.pemilikUsaha)
                binding.etKeterangan.setText(dataSktu.keterangan)

                // Control edit button visibility based on letter status
                if (type == Constant.TYPE_DETAIL) {
                    // Show edit and delete buttons only if status is NOT "Approve"
                    val buttonsVisibility =
                        if (dataSktu.status != "Approve") View.VISIBLE else View.GONE
                    binding.btnEditSurat.visibility = buttonsVisibility
                    binding.btnDeleteSurat.visibility = buttonsVisibility

                    // Show download button if status is "Approve"
                    if (dataSktu.status == "Approve") {
                        binding.btnAjukan.text = "Unduh Surat"
                        binding.btnAjukan.visibility = View.VISIBLE
                    } else {
                        binding.btnAjukan.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun setupSubmitButton(type: Int, idSurat: Int){
        binding.btnAjukan.setOnClickListener {
            val currentSuratStatus = viewModel.detailSuratKtu.value?.status
            if (type == Constant.TYPE_DETAIL && currentSuratStatus == "Approve"){
                downloadPdf(idSurat)
            } else if (validateForm()){
                val suratKtuData = collectFormData()
                when (type){
                    Constant.TYPE_CREATE -> {
                        viewModel.createSuratKtu(suratKtuData)
                    }
                    Constant.TYPE_UPDATE -> {
                        viewModel.updateSuratKtu(idSurat, suratKtuData)
                    }
                }
            }else{
                Toast.makeText(requireContext(), "Silahkan Lengkapi semua Field", Toast.LENGTH_SHORT).show() }

        }
        viewModel.operationResult.observe(viewLifecycleOwner){ isSuccess ->
            if (isSuccess){
                Toast.makeText(
                    requireContext(),
                    if (type == Constant.TYPE_CREATE) "Surat berhasil dibuat" else "Surat berhasil diperbarui",
                    Toast.LENGTH_SHORT
                ).show()
                parentFragmentManager.popBackStack()
            }
        }
        // Observe PDF download result
        viewModel.pdfDownloadResult.observe(viewLifecycleOwner) { result ->
            val (isSuccess, _) = result
            if (isSuccess) {
                Toast.makeText(requireContext(), "PDF berhasil diunduh", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Gagal mengunduh PDF", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun collectFormData(): CreateSuratKtuRequest {
        return CreateSuratKtuRequest(
            nama = binding.etNama.text.toString(),
            tempatLahir = binding.etTempatLahir.text.toString(),
            tanggalLahir = binding.etTanggalLahir.text.toString(),
            jenisKelamin = binding.spinerJK.selectedItem.toString(),
            kewarganegaraan = binding.etKewarganegaraan.text.toString(),
            agama = binding.etAgama.text.toString(),
            pekerjaan = binding.etPekerjaan.text.toString(),
            alamat = binding.etAlamat.text.toString(),
            namaUsaha = binding.etNamaTempatUsaha.text.toString(),
            jenisUsaha = binding.etJenisUsaha.text.toString(),
            alamatUsaha = binding.etAlamatUsaha.text.toString(),
            pemilikUsaha = binding.etPemilikUsaha.text.toString(),
            keterangan = binding.etKeterangan.text.toString()
        )

    }

    private fun downloadPdf(idSurat: Int) {
        // Show loading indicator
        // binding.progressBar.visibility = View.VISIBLE

        // Get the download URL from your API
        viewModel.getDownloadUrl(idSurat)

        // Observe the result
        viewModel.downloadUrlResult.observe(viewLifecycleOwner) { result ->
            //  binding.progressBar.visibility = View.GONE

            if (result.success && result.downloadUrl != null) {
                // Open the URL directly in a browser or PDF viewer
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(result.downloadUrl))
                browserIntent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
                startActivity(browserIntent)
            } else {
                Toast.makeText(requireContext(), "Gagal mendapatkan URL download", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateForm(): Boolean {
        // Basic validation - Fixed: use text property for TextInputEditText
        return binding.etNama.text?.isNotBlank() == true &&
                binding.etTempatLahir.text?.isNotBlank() == true &&
                binding.etTanggalLahir.text.toString() != "Pilih Tanggal Lahir" &&
                binding.etKewarganegaraan.text?.isNotBlank() == true &&
                binding.etAlamat.text?.isNotBlank() == true &&
                binding.etAgama.text?.isNotBlank() == true &&
                binding.etPekerjaan.text?.isNotBlank() == true &&
                binding.etJenisUsaha.text?.isNotBlank() == true &&
                binding.etAlamatUsaha.text?.isNotBlank() == true &&
                binding.etPemilikUsaha.text?.isNotBlank() == true

    }
    private fun showDeleteConfirmationDialog(idSurat: Int) {
        DialogHelper.showConfirmationDialog(
            requireContext(),
            "Apakah anda yakin ingin menghapus surat ini?",
            onConfirm = {
                viewModel.deleteSuratKtu(idSurat)
            }
        )
    }
    private fun setupDatePicker() {
        // Date picker setup - now using the CardView click
        binding.datePickerLayout.setOnClickListener {
            if (binding.etTanggalLahir.isEnabled) {
                showDatePicker()
            }
        }
    }

    private fun showDatePicker() {
        val calendar = java.util.Calendar.getInstance()
        // Set to 18 years ago as default if creating new data
        calendar.add(java.util.Calendar.YEAR, -18)

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

        val year = calendar.get(java.util.Calendar.YEAR)
        val month = calendar.get(java.util.Calendar.MONTH)
        val day = calendar.get(java.util.Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                // Format the date
                val selectedCalendar = java.util.Calendar.getInstance()
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