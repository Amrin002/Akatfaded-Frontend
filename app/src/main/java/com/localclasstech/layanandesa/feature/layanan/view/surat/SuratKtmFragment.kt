package com.localclasstech.layanandesa.feature.layanan.view.surat

import android.app.DatePickerDialog
import android.content.pm.PackageManager
import android.os.Build
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
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
import android.Manifest
import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.localclasstech.layanandesa.settings.utils.DialogHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlinx.coroutines.launch

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


        // Fetch data for both DETAIL and UPDATE modes
        if ((type == Constant.TYPE_DETAIL || type == Constant.TYPE_UPDATE) && idSurat != -1) {
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

        binding.btnEditSurat.setOnClickListener{
            val bundle = Bundle().apply {
                putInt("id_surat", idSurat)
                putInt("type", Constant.TYPE_UPDATE)
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentView, SuratKtmFragment::class.java, bundle)
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
    // And update the showDeleteConfirmationDialog method to use the helper
    private fun showDeleteConfirmationDialog(idSurat: Int) {
        DialogHelper.showConfirmationDialog(
            requireContext(),
            "Apakah anda yakin ingin menghapus surat ini?",
            onConfirm = {
                viewModel.deleteSuratKtm(idSurat)
            }
        )
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

                // Edit button visibility will be set in observeDetailData() based on status
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
                binding.etAlamat.isEnabled = true
                binding.etKeterangan.isEnabled = true

                // Show submit button with appropriate text
                binding.btnAjukan.text = "Ajukan Surat KTM"
                binding.btnAjukan.visibility = View.VISIBLE
            }
            Constant.TYPE_UPDATE -> {
                // Enable all fields for editing
                binding.btnEditSurat.visibility = View.GONE
                binding.btnDeleteSurat.visibility = View.GONE // Hide delete button in update mode
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

                // Control edit button visibility based on letter status
                if (type == Constant.TYPE_DETAIL) {
                    // Show edit and delete buttons only if status is NOT "Approve"
                    val buttonsVisibility = if (dataSktm.status != "Approve") View.VISIBLE else View.GONE
                    binding.btnEditSurat.visibility = buttonsVisibility
                    binding.btnDeleteSurat.visibility = buttonsVisibility

                    // Show download button if status is "Approve"
                    if (dataSktm.status == "Approve") {
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
            binding.btnAjukan.text = if (isLoading) "" else when(type){
                Constant.TYPE_CREATE -> "Ajukan Surat"
                Constant.TYPE_DETAIL -> "Unduh Surat"
                Constant.TYPE_UPDATE -> "Perbarui Surat"
                else -> "Ajukan"
            }
        }
    }
    private fun setupSubmitButton(type: Int, idSurat: Int) {
        binding.btnAjukan.setOnClickListener {
            // Get the current surat status from viewModel data
            val currentSuratStatus = viewModel.detailSuratKtm.value?.status

            // If we're in detail mode and status is Approve, perform download
            if (type == Constant.TYPE_DETAIL && currentSuratStatus == "Approve") {
                downloadPdf(idSurat)
            } else if (validateForm()) {
                // Normal form submission for create/update
                val suratKtmData = collectFormData()

                when (type) {
                    Constant.TYPE_CREATE -> {
                        viewModel.createSuratKtm(suratKtmData)
                    }
                    Constant.TYPE_UPDATE -> {
                        viewModel.updateSuratKtm(idSurat, suratKtmData)
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
    private fun downloadPdf(idSurat: Int) {
        // Show loading indicator
        //binding.progressBar.visibility = View.VISIBLE

        // Get the download URL from your API
        viewModel.getDownloadUrl(idSurat)

        // Observe the result
        viewModel.downloadUrlResult.observe(viewLifecycleOwner) { result ->
           // binding.progressBar.visibility = View.GONE

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

    //    private fun downloadPdf(idSurat: Int) {
//        // Request runtime permissions for storage if needed (Android 10+ handles differently)
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q &&
//            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
//            != PackageManager.PERMISSION_GRANTED) {
//
//            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1001)
//            return
//        }
//
//        // Show loading indicator
//        binding.progressBar.visibility = View.VISIBLE
//
//        // Use viewModel to export PDF
//        viewModel.exportPdfSuratKtm(idSurat)
//
//        // Observe the response
//        viewModel.pdfDownloadResult.observe(viewLifecycleOwner) { result ->
//            binding.progressBar.visibility = View.GONE
//
//            val (isSuccess, responseBody) = result
//
//            if (isSuccess && responseBody != null) {
//                try {
//                    savePdfFile(responseBody, "SuratKTM_${idSurat}.pdf")
//                } catch (e: Exception) {
//                    Toast.makeText(requireContext(), "Error saving PDF: ${e.message}", Toast.LENGTH_SHORT).show()
//                    Log.e("SuratKtmFragment", "Error saving PDF: ${e.message}")
//                }
//            } else {
//                Toast.makeText(requireContext(), "Gagal mengunduh PDF", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
    private fun savePdfFile(responseBody: ResponseBody, fileName: String) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // For Android 10 and above, use MediaStore
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }

                val uri = requireContext().contentResolver.insert(
                    MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                    contentValues
                ) ?: throw IOException("Failed to create new MediaStore record.")

                requireContext().contentResolver.openOutputStream(uri)?.use { outputStream ->
                    val inputStream = responseBody.byteStream()
                    inputStream.copyTo(outputStream)
                    outputStream.flush()
                } ?: throw IOException("Failed to open output stream.")

                Toast.makeText(requireContext(),
                    "PDF disimpan di folder Download dengan nama '$fileName'",
                    Toast.LENGTH_LONG).show()

            } else {
                // For older Android versions, direct file access
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                if (!downloadsDir.exists()) {
                    downloadsDir.mkdirs()
                }

                val file = File(downloadsDir, fileName)
                val inputStream = responseBody.byteStream()
                val outputStream = FileOutputStream(file)

                inputStream.copyTo(outputStream)
                outputStream.flush()
                outputStream.close()
                inputStream.close()

                // Notify media scanner to make the file visible
                MediaScannerConnection.scanFile(
                    requireContext(),
                    arrayOf(file.absolutePath),
                    arrayOf("application/pdf"),
                    null
                )

                Toast.makeText(requireContext(),
                    "PDF disimpan di folder Download dengan nama '$fileName'",
                    Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Log.e("SuratKtmFragment", "Error saving PDF: ${e.message}")
            Toast.makeText(requireContext(), "Gagal menyimpan PDF: ${e.message}", Toast.LENGTH_SHORT).show()
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