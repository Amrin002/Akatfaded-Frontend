package com.localclasstech.layanandesa.feature.layanan.view.surat

import android.app.DatePickerDialog
import android.content.Intent
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
import com.localclasstech.layanandesa.databinding.FragmentSuratKtmBinding
import com.localclasstech.layanandesa.databinding.FragmentSuratPindahdomisiliBinding
import com.localclasstech.layanandesa.feature.layanan.data.network.data.suratktm.CreateSktmRequest
import com.localclasstech.layanandesa.feature.layanan.data.network.data.suratpindahdomisili.CreateSuratPindahRequest
import com.localclasstech.layanandesa.feature.layanan.viewmodel.surat.SuratKtmViewModel
import com.localclasstech.layanandesa.feature.layanan.viewmodel.surat.SuratKtmViewModelFactory
import com.localclasstech.layanandesa.feature.layanan.viewmodel.surat.SuratPindahdomisiliViewModel
import com.localclasstech.layanandesa.feature.layanan.viewmodel.surat.SuratPindahdomisiliViewModelFactory
import com.localclasstech.layanandesa.settings.utils.Constant
import com.localclasstech.layanandesa.settings.utils.CustomSpinnerAdapter
import com.localclasstech.layanandesa.settings.utils.DialogHelper
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class SuratPindahdomisiliFragment : Fragment() {
    private var _binding: FragmentSuratPindahdomisiliBinding? = null
    private val binding get() = _binding!!

    private var type = Constant.TYPE_CREATE

    companion object {
        fun newInstance() = SuratPindahdomisiliFragment()
    }

    private lateinit var viewModel: SuratPindahdomisiliViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val factory = SuratPindahdomisiliViewModelFactory(requireContext())
        viewModel = ViewModelProvider(this, factory)[SuratPindahdomisiliViewModel::class.java]
        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSuratPindahdomisiliBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val idSurat = arguments?.getInt("id_surat", -1) ?: -1
        val type = arguments?.getInt("type", Constant.TYPE_CREATE) ?: Constant.TYPE_CREATE

        // Fetch data for both DETAIL and UPDATE modes
        if ((type == Constant.TYPE_DETAIL || type == Constant.TYPE_UPDATE) && idSurat != -1) {
            viewModel.fetchSuratPindahDetail(idSurat)
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

        binding.btnEditSurat.setOnClickListener{
            val bundle = Bundle().apply {
                putInt("id_surat", idSurat)
                putInt("type", Constant.TYPE_UPDATE)
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentView, SuratPindahdomisiliFragment::class.java, bundle)
                .addToBackStack(null)
                .commit()
        }
        binding.btnDeleteSurat.setOnClickListener {
            showDeleteConfirmationDialog(idSurat)
        }
        // Observe data surat
        configureUIBasedOnType(type)
        observeDetailData(type)

        setupSubmitButton(type, idSurat)
    }

    private fun configureUIBasedOnType(type: Int) {
        when(type){
            Constant.TYPE_DETAIL->{
                binding.etNama.isEnabled = false
                binding.etTempatLahir.isEnabled = false
                binding.etTanggalLahir.isEnabled = false
                binding.spinerJK.isEnabled = false
                binding.spinerSK.isEnabled = false
                binding.etKewarganegaraan.isEnabled = false
                binding.etPekerjaan.isEnabled = false
                binding.etKewarganegaraan.isEnabled = false
                binding.etPekerjaan.isEnabled = false
                binding.etKecamatan.isEnabled = false
                binding.etKabupaten.isEnabled = false
                binding.etAlamat.isEnabled = false
                binding.etDesaTujuan.isEnabled = false
                binding.etRtTujuan.isEnabled = false
                binding.etRwTujuan.isEnabled = false
                binding.etJalanTujuan.isEnabled = false
                binding.etKecamatanTujuan.isEnabled = false
                binding.etKabupatenTujuan.isEnabled = false
                binding.etProvinsiTujuan.isEnabled = false
                binding.etKeterangan.isEnabled = false
                binding.btnAjukan.visibility = View.GONE
            }
            Constant.TYPE_UPDATE->{
                binding.btnEditSurat.visibility = View.GONE
                binding.btnDeleteSurat.visibility = View.GONE

                binding.etNama.isEnabled = true
                binding.etTempatLahir.isEnabled = true
                binding.etTanggalLahir.isEnabled = true
                binding.spinerJK.isEnabled = true
                binding.spinerSK.isEnabled = true
                binding.etKewarganegaraan.isEnabled = true
                binding.etPekerjaan.isEnabled = true
                binding.etKewarganegaraan.isEnabled = true
                binding.etPekerjaan.isEnabled = true
                binding.etKecamatan.isEnabled = true
                binding.etKabupaten.isEnabled = true
                binding.etAlamat.isEnabled = true
                binding.etDesaTujuan.isEnabled = true
                binding.etRtTujuan.isEnabled = true
                binding.etRwTujuan.isEnabled = true
                binding.etJalanTujuan.isEnabled = true
                binding.etKecamatanTujuan.isEnabled = true
                binding.etKabupatenTujuan.isEnabled = true
                binding.etProvinsiTujuan.isEnabled = true
                binding.etKeterangan.isEnabled = true
                binding.btnAjukan.text = "Perbarui Surat"
                // Show submit button with update text
                binding.btnAjukan.visibility = View.VISIBLE
            }
            Constant.TYPE_CREATE -> {
                // Set default text for date field
                binding.etTanggalLahir.text = "Pilih Tanggal Lahir"
                binding.btnEditSurat.visibility = View.GONE
                binding.btnDeleteSurat.visibility = View.GONE // Hide delete button in create mode

                // Enable all fields for input
                binding.etNama.isEnabled = true
                binding.etTempatLahir.isEnabled = true
                binding.etTanggalLahir.isEnabled = true
                binding.spinerJK.isEnabled = true
                binding.spinerSK.isEnabled = true
                binding.etKewarganegaraan.isEnabled = true
                binding.etPekerjaan.isEnabled = true
                binding.etKewarganegaraan.isEnabled = true
                binding.etPekerjaan.isEnabled = true
                binding.etKecamatan.isEnabled = true
                binding.etKabupaten.isEnabled = true
                binding.etAlamat.isEnabled = true
                binding.etDesaTujuan.isEnabled = true
                binding.etRtTujuan.isEnabled = true
                binding.etRwTujuan.isEnabled = true
                binding.etJalanTujuan.isEnabled = true
                binding.etKecamatanTujuan.isEnabled = true
                binding.etKabupatenTujuan.isEnabled = true
                binding.etProvinsiTujuan.isEnabled = true
                binding.etKeterangan.isEnabled = true
                binding.btnAjukan.text = "Ajukan Surat"
                // Show submit button with appropriate text
                binding.btnAjukan.visibility = View.VISIBLE
            }
        }
    }

    private fun setupSubmitButton(type: Int, idSurat: Int) {
        binding.btnAjukan.setOnClickListener {
            val currentSuratStatus = viewModel.detailSuratPindah.value?.status

            // If we're in detail mode and status is Approve, perform download
            if (type == Constant.TYPE_DETAIL && currentSuratStatus == "Approve") {
                downloadPdf(idSurat)
            } else if (validateForm()) {
                // Normal form submission for create/update
                val suratDomisiliPindah = collectFormData()

                when (type) {
                    Constant.TYPE_CREATE -> {
                        viewModel.createSuratPindah(suratDomisiliPindah)
                    }
                    Constant.TYPE_UPDATE -> {
                        viewModel.updateSuratPindah(idSurat, suratDomisiliPindah)
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Silakan lengkapi semua field", Toast.LENGTH_SHORT).show()
            }
        }
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
    private fun validateForm(): Boolean {
        // Basic validation
        return binding.etNama.text.isNotBlank() &&
                binding.etTempatLahir.text.isNotBlank() &&
                binding.etTanggalLahir.text.toString() != "Pilih Tanggal Lahir" &&
                binding.etKewarganegaraan.text.isNotBlank() &&
                binding.etPekerjaan.text.isNotBlank() &&
                binding.etKecamatan.text.isNotBlank() &&
                binding.etKabupaten.text.isNotBlank() &&
                binding.etAlamat.text.isNotBlank() &&
                binding.etRtTujuan.text.isNotBlank() &&
                binding.etRwTujuan.text.isNotBlank() &&
                binding.etJalanTujuan.text.isNotBlank() &&
                binding.etKecamatanTujuan.text.isNotBlank() &&
                binding.etKabupatenTujuan.text.isNotBlank() &&
                binding.etProvinsiTujuan.text.isNotBlank() 
                
    }
    private fun collectFormData(): CreateSuratPindahRequest {
        return CreateSuratPindahRequest(
            nama = binding.etNama.text.toString(),
            tempatLahir = binding.etTempatLahir.text.toString(),
            tanggalLahir = binding.etTanggalLahir.text.toString(),
            jenisKelamin = binding.spinerJK.selectedItem.toString(),
            statusKawin = binding.spinerSK.selectedItem.toString(),
            kewarganegaraan = binding.etKewarganegaraan.text.toString(),
            pekerjaan = binding.etPekerjaan.text.toString(),
            alamat = binding.etAlamat.text.toString(),
            kecamatan = binding.etKecamatan.text.toString(),
            kabupaten = binding.etKabupaten.text.toString(),
            desaPindah = binding.etDesaTujuan.text.toString(),
            rt = binding.etRtTujuan.text.toString(),
            rw = binding.etRwTujuan.text.toString(),
            jalan = binding.etJalanTujuan.text.toString(),
            kecamatanPindah = binding.etKecamatanTujuan.text.toString(),
            kabupatenPindah = binding.etKabupatenTujuan.text.toString(),
            provinsi = binding.etProvinsiTujuan.text.toString(),
            keterangan = binding.etKeterangan.text.toString()
        )
    }
    private fun downloadPdf(idSurat: Int) {
        // Show loading indicator


        // Get the download URL from your API
        viewModel.getDownloadUrl(idSurat)

        // Observe the result
        viewModel.downloadUrlResult.observe(viewLifecycleOwner) { result ->


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
    
    private fun observeDetailData(type: Int) {
        viewModel.deleteResult.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                Toast.makeText(requireContext(), "Surat berhasil dihapus", Toast.LENGTH_SHORT).show()
                // Navigasi kembali ke halaman sebelumnya
                parentFragmentManager.popBackStack()
            } else {
                Toast.makeText(requireContext(), "Gagal menghapus surat", Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
            }
        }
        viewModel.detailSuratPindah.observe(viewLifecycleOwner) { dataSuratPindah ->
            if (dataSuratPindah != null && (type == Constant.TYPE_DETAIL || type == Constant.TYPE_UPDATE)){
                //isi data
                binding.etNama.setText(dataSuratPindah.nama)
                binding.etTempatLahir.setText(dataSuratPindah.tempatLahir)
                binding.etTanggalLahir.setText(dataSuratPindah.tanggalLahir)
                // Set spinner selections
                val jenisKelaminItems = listOf("Laki-laki", "Perempuan")
                val jenisKelaminIndex = jenisKelaminItems.indexOf(dataSuratPindah.jenisKelamin)
                if (jenisKelaminIndex != -1) {
                    binding.spinerJK.setSelection(jenisKelaminIndex)
                }
                val statusKawinItems = listOf("Belum kawin", "Sudah kawin", "Cerai")
                val statusKawinIndex = statusKawinItems.indexOf(dataSuratPindah.statusKawin)
                if (statusKawinIndex != -1) {
                    binding.spinerSK.setSelection(statusKawinIndex)
                }
                binding.etKewarganegaraan.setText(dataSuratPindah.kewarganegaraan)
                binding.etPekerjaan.setText(dataSuratPindah.pekerjaan)
                binding.etKecamatan.setText(dataSuratPindah.kecamatan)
                binding.etKabupaten.setText(dataSuratPindah.kabupaten)
                binding.etAlamat.setText(dataSuratPindah.alamat)
                binding.etDesaTujuan.setText(dataSuratPindah.desaPindah)
                binding.etRtTujuan.setText(dataSuratPindah.rt)
                binding.etRwTujuan.setText(dataSuratPindah.rw)
                binding.etJalanTujuan.setText(dataSuratPindah.jalan)
                binding.etKecamatanTujuan.setText(dataSuratPindah.kecamatanPindah)
                binding.etKabupatenTujuan.setText(dataSuratPindah.kabupatenPindah)
                binding.etProvinsiTujuan.setText(dataSuratPindah.provinsi)
                binding.etKeterangan.setText(dataSuratPindah.keterangan)

                // Control edit button visibility based on letter status
                if (type == Constant.TYPE_DETAIL) {
                    // Show edit and delete buttons only if status is NOT "Approve"
                    val buttonsVisibility = if (dataSuratPindah.status != "Approve") View.VISIBLE else View.GONE
                    binding.btnEditSurat.visibility = buttonsVisibility
                    binding.btnDeleteSurat.visibility = buttonsVisibility

                    // Show download button if status is "Approve"
                    if (dataSuratPindah.status == "Approve") {
                        binding.btnAjukan.text = "Unduh Surat"
                        binding.btnAjukan.visibility = View.VISIBLE
                    } else {
                        binding.btnAjukan.visibility = View.GONE
                    }
                }
            }

        }


        viewModel.isLoading.observe(viewLifecycleOwner){ isLoading->
            binding.btnAjukan.isEnabled = !isLoading
            binding.progressBarButton.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnAjukan.text = if(isLoading) "" else when (type) {
                Constant.TYPE_CREATE -> "Ajukan Surat"
                Constant.TYPE_UPDATE -> "Perbarui Surat"
                Constant.TYPE_DETAIL -> "Unduh Surat"
                else -> "Ajukan"
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
    private fun setupDatePicker() {
        binding.etTanggalLahir.setOnClickListener {
            if (binding.etTanggalLahir.isEnabled) {
                showDatePicker()
            }
        }
    }
    private fun setLoadingButton(isLoading: Boolean) {
        binding.btnAjukan.isEnabled = !isLoading
        if (isLoading) {
            binding.btnAjukan.text = ""
            binding.progressBarButton.visibility = View.VISIBLE
        } else {
            binding.progressBarButton.visibility = View.GONE
            // Reset teks tombol sesuai tipe saat ini (bisa simpan currentType di class)
            binding.btnAjukan.text = when (type) {
                Constant.TYPE_CREATE -> "Ajukan Surat"
                Constant.TYPE_UPDATE -> "Perbarui Surat"
                Constant.TYPE_DETAIL -> "Unduh Surat"
                else -> "Ajukan"
            }
        }
    }

    // And update the showDeleteConfirmationDialog method to use the helper
    private fun showDeleteConfirmationDialog(idSurat: Int) {
        DialogHelper.showConfirmationDialog(
            requireContext(),
            "Apakah anda yakin ingin menghapus surat ini?",
            onConfirm = {
                viewModel.deleteSuratPindah(idSurat)
            }
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