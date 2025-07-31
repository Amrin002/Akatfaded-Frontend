package com.localclasstech.layanandesa.feature.layanan.view.surat

import android.app.DatePickerDialog
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.localclasstech.layanandesa.R
import com.localclasstech.layanandesa.databinding.FragmentSuratDomisiliBinding
import com.localclasstech.layanandesa.feature.layanan.data.network.data.suratdomisili.CreateSuratDomisiliRequest
import com.localclasstech.layanandesa.feature.layanan.viewmodel.surat.SuratDomisiliViewModel
import com.localclasstech.layanandesa.feature.layanan.viewmodel.surat.SuratDomisiliViewModelFactory
import com.localclasstech.layanandesa.settings.utils.Constant
import com.localclasstech.layanandesa.settings.utils.CustomSpinnerAdapter
import com.localclasstech.layanandesa.settings.utils.DialogHelper
import java.util.Locale

class SuratDomisiliFragment : Fragment() {
    private var _binding: FragmentSuratDomisiliBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = SuratDomisiliFragment()
    }

    private lateinit var viewModel: SuratDomisiliViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val factory = SuratDomisiliViewModelFactory(requireContext())
        viewModel = ViewModelProvider(this, factory)[SuratDomisiliViewModel::class.java]
        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSuratDomisiliBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val idSurat = arguments?.getInt("id_surat", -1) ?: -1
        val type = arguments?.getInt("type", Constant.TYPE_CREATE) ?: Constant.TYPE_CREATE

//        if(type == Constant.TYPE_DETAIL && idSurat != -1){
//            viewModel.fetchSuratDomisiliDetail(idSurat)
//        }
        // Tambahkan kondisi untuk UPDATE
        if ((type == Constant.TYPE_DETAIL || type == Constant.TYPE_UPDATE) && idSurat != -1){
            Log.d("SuratDomisiliFragment", "Fetching detail for ID: $idSurat, Type: $type")
            viewModel.fetchSuratDomisiliDetail(idSurat)
        }

        binding.backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        // Tambahkan di onViewCreated()
        binding.btnEditSurat.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("id_surat", idSurat)
                putInt("type", Constant.TYPE_UPDATE)
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentView, SuratDomisiliFragment::class.java, bundle)
                .addToBackStack(null)
                .commit()
        }
        binding.btnDeleteSurat.setOnClickListener {
            showDeleteConfirmationDialog(idSurat)
        }
        setupSpinner()
        setupDatePicker()

        configureUIBasedOnType(type)
        observeDetailData(type)
        setupSubmitButton(type, idSurat)




    }

    private fun validateForm(): Boolean {
        return binding.etNama.text?.isNotBlank() == true &&
                binding.etTempatLahir.text?.isNotBlank()== true &&
                binding.etTanggalLahir.text?.isNotBlank()== true &&
                binding.etKewarganegaraan.text?.isNotBlank()== true &&
                binding.etPekerjaan.text?.isNotBlank()== true &&
                binding.etAlamat.text?.isNotBlank()== true

    }
    private fun showDeleteConfirmationDialog(idSurat: Int) {
        DialogHelper.showConfirmationDialog(
            requireContext(),
            "Apakah anda yakin ingin menghapus surat domisili ini?",
            onConfirm = {
                viewModel.deleteSuratDomisili(idSurat)
            }
        )
    }
    private fun setupSubmitButton(type: Int, idSurat: Int) {

        binding.btnAjukan.setOnClickListener {
            val currentSuratStatus = viewModel.detailSuratDomisili.value?.status
            if (type == Constant.TYPE_DETAIL && currentSuratStatus == "Approve") {
                downloadPdf(idSurat)
            } else if(validateForm()){
                val suratDomisiliData = collectFromData()
                when(type){
                    Constant.TYPE_CREATE -> {
                        viewModel.createSuratDomisili(suratDomisiliData)
                    }
                    Constant.TYPE_UPDATE -> {
                        viewModel.updateSuratDomisili(idSurat, suratDomisiliData)
                    }
                }
            }else {
                Toast.makeText(requireContext(), "Silahkan Isi Semua Form", Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.operationResult.observe(viewLifecycleOwner){ isSuccess ->
            if(isSuccess){
                Toast.makeText(requireContext(),
                    if (type ==Constant.TYPE_CREATE) "Surat Berhasil Dibuat" else "Surat berhasil Di perbarui", Toast.LENGTH_SHORT
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
    // Tambahkan method downloadPdf yang sama seperti di SuratKtmFragment
    private fun downloadPdf(idSurat: Int) {
        // Tampilkan indikator loading
        //binding.progressBar.visibility = View.VISIBLE

        // Dapatkan URL download dari API
        viewModel.getDownloadUrl(idSurat)

        // Observasi hasilnya
        viewModel.downloadUrlResult.observe(viewLifecycleOwner) { result ->
//            binding.progressBar.visibility = View.GONE

            if (result.success && result.downloadUrl != null) {
                // Buka URL secara langsung di browser atau PDF viewer
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(result.downloadUrl))
                browserIntent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
                startActivity(browserIntent)
            } else {
                Toast.makeText(requireContext(), "Gagal mendapatkan URL download", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun collectFromData(): CreateSuratDomisiliRequest {
        return CreateSuratDomisiliRequest(
            nama = binding.etNama.text.toString(),
            tempat_lahir = binding.etTempatLahir.text.toString(),
            tanggal_lahir = binding.etTanggalLahir.text.toString(),
            jenis_kelamin = binding.spinerJK.selectedItem.toString(),
            status_kawin = binding.spinerSK.selectedItem.toString(),
            kewarganegaraan = binding.etKewarganegaraan.text.toString(),
            pekerjaan = binding.etPekerjaan.text.toString(),
            alamat = binding.etAlamat.text.toString(),
            keterangan = binding.etKeterangan.text.toString()
        )

    }

    private fun configureUIBasedOnType(type: Int) {
        Log.d("SuratDomisiliFragment", "configureUIBasedOnType called with type: $type")
        Log.d("SuratDomisiliFragment", "TYPE_CREATE constant value: ${Constant.TYPE_CREATE}")
        Log.d("SuratDomisiliFragment", "TYPE_DETAIL constant value: ${Constant.TYPE_DETAIL}")
        Log.d("SuratDomisiliFragment", "TYPE_UPDATE constant value: ${Constant.TYPE_UPDATE}")
        when (type){
            Constant.TYPE_DETAIL -> {
                binding.etNama.isEnabled = false
                binding.etTempatLahir.isEnabled = false
                binding.etTanggalLahir.isEnabled = false
                binding.spinerJK.isEnabled = false
                binding.spinerSK.isEnabled = false
                binding.etKewarganegaraan.isEnabled = false
                binding.etPekerjaan.isEnabled = false
                binding.etAlamat.isEnabled = false
                binding.etKeterangan.isEnabled = false
                binding.btnAjukan.visibility = View.GONE
            }
            Constant.TYPE_CREATE -> {
                binding.etNama.isEnabled = true
                binding.etTempatLahir.isEnabled = true
                binding.etTanggalLahir.isEnabled = true
                binding.spinerJK.isEnabled = true
                binding.spinerSK.isEnabled = true
                binding.etKewarganegaraan.isEnabled = true
                binding.etPekerjaan.isEnabled = true
                binding.etAlamat.isEnabled = true
                binding.etKeterangan.isEnabled = true
                binding.btnEditSurat.visibility = View.GONE
                binding.btnDeleteSurat.visibility = View.GONE
                binding.btnAjukan.visibility = View.VISIBLE
                binding.btnAjukan.text = "Ajukan Surat"
            }
            Constant.TYPE_UPDATE -> {
                binding.etNama.isEnabled = true
                binding.etTempatLahir.isEnabled = true
                binding.etTanggalLahir.isEnabled = true
                binding.spinerJK.isEnabled = true
                binding.spinerSK.isEnabled = true
                binding.etKewarganegaraan.isEnabled = true
                binding.etPekerjaan.isEnabled = true
                binding.etAlamat.isEnabled = true
                binding.etKeterangan.isEnabled = true
                binding.btnAjukan.visibility = View.VISIBLE
                binding.btnEditSurat.visibility = View.GONE
                binding.btnDeleteSurat.visibility = View.GONE
                binding.btnAjukan.text = "Perbarui Surat"
            }
        }
    }

    private fun observeDetailData(type: Int){
        Log.d("SuratDomisiliFragment", "observeDetailData called with type: $type")
        viewModel.deleteResult.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                Toast.makeText(requireContext(), "Surat berhasil dihapus", Toast.LENGTH_SHORT).show()

                // Navigasi kembali ke halaman sebelumnya
                parentFragmentManager.popBackStack()
            } else {
                Toast.makeText(requireContext(), "Gagal menghapus surat", Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.detailSuratDomisili.observe(viewLifecycleOwner){ dataSDomisili->
            Log.d("SuratDomisiliFragment", "Received data: $dataSDomisili")
            // Perbaiki kondisi
            if (dataSDomisili != null && (type == Constant.TYPE_DETAIL || type == Constant.TYPE_UPDATE)){
                // fill data
                binding.etNama.setText(dataSDomisili.nama)
                binding.etTempatLahir.setText(dataSDomisili.tempatLahir)
                binding.etTanggalLahir.setText(dataSDomisili.tanggalLahir)
                // Set spinner selections
                val jenisKelaminItems = listOf("Laki-laki", "Perempuan")
                val jenisKelaminIndex = jenisKelaminItems.indexOf(dataSDomisili.jenisKelamin)
                if (jenisKelaminIndex != -1) {
                    binding.spinerJK.setSelection(jenisKelaminIndex)
                }

                val statusKawinItems = listOf("Belum kawin", "Sudah kawin", "Cerai")
                val statusKawinIndex = statusKawinItems.indexOf(dataSDomisili.statusKawin)
                if (statusKawinIndex != -1) {
                    binding.spinerSK.setSelection(statusKawinIndex)
                }

                binding.etKewarganegaraan.setText(dataSDomisili.kewarganegaraan)
                binding.etPekerjaan.setText(dataSDomisili.pekerjaan)
                binding.etAlamat.setText(dataSDomisili.alamat)
                binding.etKeterangan.setText(dataSDomisili.keterangan)

                Log.d("SuratDomisiliFragment", "Form data set completed")
            }

            if (type == Constant.TYPE_DETAIL) {
                // Tampilkan tombol edit dan delete hanya jika status bukan "Approve"
                val buttonsVisibility = if (dataSDomisili.status != "Approve") View.VISIBLE else View.GONE
                binding.btnEditSurat.visibility = buttonsVisibility
                binding.btnDeleteSurat.visibility = buttonsVisibility

                // Tampilkan tombol unduh jika status "Approve"
                if (dataSDomisili.status == "Approve") {
                    binding.btnAjukan.text = "Unduh Surat"
                    binding.btnAjukan.visibility = View.VISIBLE
                } else {
                    binding.btnAjukan.visibility = View.GONE
                }
            }

        }
        // Tambahkan observasi error untuk debugging
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            Log.e("SuratDomisiliFragment", "ViewModel Error: $errorMessage")
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
        }
        // Tambahkan method download PDF
        viewModel.pdfDownloadResult.observe(viewLifecycleOwner) { result ->
            val (isSuccess, _) = result
            if (isSuccess) {
                Toast.makeText(requireContext(), "PDF berhasil diunduh", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Gagal mengunduh PDF", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.btnAjukan.isEnabled = !isLoading
            binding.progressBarButton.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnAjukan.text = if (isLoading) "Proses..." else when(type){
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
