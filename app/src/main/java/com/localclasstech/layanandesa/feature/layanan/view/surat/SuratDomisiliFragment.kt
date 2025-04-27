package com.localclasstech.layanandesa.feature.layanan.view.surat

import android.app.DatePickerDialog
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.localclasstech.layanandesa.databinding.FragmentSuratDomisiliBinding
import com.localclasstech.layanandesa.feature.layanan.data.network.data.suratdomisili.CreateSuratDomisiliRequest
import com.localclasstech.layanandesa.feature.layanan.viewmodel.surat.SuratDomisiliViewModel
import com.localclasstech.layanandesa.feature.layanan.viewmodel.surat.SuratDomisiliViewModelFactory
import com.localclasstech.layanandesa.settings.utils.Constant
import com.localclasstech.layanandesa.settings.utils.CustomSpinnerAdapter
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

        if(type == Constant.TYPE_DETAIL && idSurat != -1){
            viewModel.fetchSuratDomisiliDetail(idSurat)
        }

        binding.backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        setupSpinner()
        setupDatePicker()

        configureUIBasedOnType(type)
        observeDetailData(type)
        setupSubmitButton(type, idSurat)



    }

    private fun validateForm(): Boolean {
        return binding.etNama.text.isNotBlank() &&
                binding.etTempatLahir.text.isNotBlank() &&
                binding.etTanggalLahir.text.isNotBlank() &&
                binding.etKewarganegaraan.text.isNotBlank() &&
                binding.etPekerjaan.text.isNotBlank() &&
                binding.etAlamat.text.isNotBlank()

    }

    private fun setupSubmitButton(type: Int, idSurat: Int) {
        binding.btnAjukan.setOnClickListener {
            if(validateForm()){
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
            }Constant.TYPE_CREATE -> {
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
        }Constant.TYPE_UPDATE->{

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
            binding.btnAjukan.text = "Perbarua Surat Domisili"
        }
        }
    }

    private fun observeDetailData(type: Int){
        viewModel.detailSuratDomisili.observe(viewLifecycleOwner){ dataSDomisili->
           if (dataSDomisili != null && type == Constant.TYPE_DETAIL || type == Constant.TYPE_UPDATE){
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
