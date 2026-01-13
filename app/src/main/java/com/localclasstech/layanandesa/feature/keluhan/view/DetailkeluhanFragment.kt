package com.localclasstech.layanandesa.feature.keluhan.view

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.localclasstech.layanandesa.BuildConfig
import com.localclasstech.layanandesa.R
import com.localclasstech.layanandesa.databinding.FragmentDetailkeluhanBinding
import com.localclasstech.layanandesa.feature.keluhan.viewmodel.DetailkeluhanViewModel
import com.localclasstech.layanandesa.feature.keluhan.viewmodel.DetailkeluhanViewModelFactory
import com.localclasstech.layanandesa.settings.utils.Constant
import com.localclasstech.layanandesa.settings.utils.DialogHelper
import com.localclasstech.layanandesa.settings.utils.UrlConstant

class DetailkeluhanFragment : Fragment() {
    private var _binding: FragmentDetailkeluhanBinding? = null
    private val binding get() = _binding!!

    private val IMAGE_PICKER_REQUEST_CODE = 1001
    private var selectedImageUri: Uri? = null

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

        val id = arguments?.getInt("id", -1) ?: -1
        val type = arguments?.getInt("type", Constant.TYPE_CREATE) ?: Constant.TYPE_CREATE

        // Fetch detail data jika type DETAIL atau UPDATE
        if ((type == Constant.TYPE_DETAIL || type == Constant.TYPE_UPDATE) && id != -1) {
            logDebug("Fetching detail for ID: $id, Type: $type")
            viewModel.getKeluhanDetail(id)
        }

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Button Edit - navigasi ke mode UPDATE
        binding.btnEditKeluhan.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("id", id)
                putInt("type", Constant.TYPE_UPDATE)
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentView, DetailkeluhanFragment::class.java, bundle)
                .addToBackStack(null)
                .commit()
        }

        // Button Delete
        binding.btnDeleteKeluhan.setOnClickListener {
            showDeleteConfirmationDialog(id)
        }

        // Button Pilih Gambar
        binding.btnTambahGambar.setOnClickListener {
            openImagePicker()
        }

        configureUIBasedOnType(type)
        observeDetailData(type)
        setupSubmitButton(type, id)
    }

    private fun validateForm(): Boolean {
        val isValid = binding.etJudul.text.isNotBlank() &&
                binding.etIsiKeluhan.text.isNotBlank()

        if (!isValid) {
            // ✅ Error toast tetap ditampilkan
            Toast.makeText(requireContext(), "Judul dan isi keluhan harus diisi", Toast.LENGTH_SHORT).show()
        }

        return isValid
    }

    private fun showDeleteConfirmationDialog(idKeluhan: Int) {
        DialogHelper.showConfirmationDialog(
            requireContext(),
            "Apakah anda yakin ingin menghapus keluhan ini?",
            onConfirm = {
                viewModel.deleteKeluhan(idKeluhan)
            }
        )
    }

    private fun setupSubmitButton(type: Int, idKeluhan: Int) {
        binding.btnAjukan.setOnClickListener {
            if (!validateForm()) {
                return@setOnClickListener
            }

            val judul = binding.etJudul.text.toString()
            val isi = binding.etIsiKeluhan.text.toString()

            // Gunakan fungsi baru dengan kompresi otomatis
            when (type) {
                Constant.TYPE_CREATE -> {
                    logDebug("Creating keluhan with compression")
                    viewModel.createKeluhanWithImage(
                        context = requireContext(),
                        judul = judul,
                        isi = isi,
                        imageUri = selectedImageUri
                    )
                }
                Constant.TYPE_UPDATE -> {
                    logDebug("Updating keluhan with compression")
                    viewModel.updateKeluhanWithImage(
                        context = requireContext(),
                        id = idKeluhan,
                        judul = judul,
                        isi = isi,
                        imageUri = selectedImageUri
                    )
                }
            }
        }

        // Observe operation result
        viewModel.operationResult.observe(viewLifecycleOwner) { isSuccess ->
            isSuccess?.let {
                if (it) {
                    val message = when (type) {
                        Constant.TYPE_CREATE -> "Keluhan berhasil dibuat"
                        Constant.TYPE_UPDATE -> "Keluhan berhasil diperbarui"
                        else -> "Operasi berhasil"
                    }
                    // ✅ Success toast tetap ditampilkan
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

                    viewModel.resetOperationResult()
                    parentFragmentManager.popBackStack()
                }
            }
        }
    }

    private fun configureUIBasedOnType(type: Int) {
        when (type) {
            Constant.TYPE_DETAIL -> {
                binding.etJudul.isEnabled = false
                binding.etIsiKeluhan.isEnabled = false
                binding.btnTambahGambar.visibility = View.GONE
                binding.btnAjukan.visibility = View.GONE
                binding.btnEditKeluhan.visibility = View.VISIBLE
                binding.btnDeleteKeluhan.visibility = View.VISIBLE
            }
            Constant.TYPE_CREATE -> {
                binding.etJudul.isEnabled = true
                binding.etIsiKeluhan.isEnabled = true
                binding.btnTambahGambar.visibility = View.VISIBLE
                binding.btnTambahGambar.isEnabled = true
                binding.btnEditKeluhan.visibility = View.GONE
                binding.btnDeleteKeluhan.visibility = View.GONE
                binding.btnAjukan.visibility = View.VISIBLE
                binding.btnAjukan.text = "Ajukan Keluhan"
                selectedImageUri = null
            }
            Constant.TYPE_UPDATE -> {
                binding.etJudul.isEnabled = true
                binding.etIsiKeluhan.isEnabled = true
                binding.btnTambahGambar.visibility = View.VISIBLE
                binding.btnTambahGambar.isEnabled = true
                binding.btnAjukan.visibility = View.VISIBLE
                binding.btnEditKeluhan.visibility = View.GONE
                binding.btnDeleteKeluhan.visibility = View.GONE
                binding.btnAjukan.text = "Perbarui Keluhan"
            }
        }
    }

    private fun observeDetailData(type: Int) {
        logDebug("observeDetailData called with type: $type")

        // Observe detail data
        viewModel.keluhanDetail.observe(viewLifecycleOwner) { dataKeluhan ->
            logDebug("Received data: $dataKeluhan")

            if (dataKeluhan != null && (type == Constant.TYPE_DETAIL || type == Constant.TYPE_UPDATE)) {
                binding.etJudul.setText(dataKeluhan.judul)
                binding.etIsiKeluhan.setText(dataKeluhan.isi)

                if (!dataKeluhan.gambar.isNullOrEmpty()) {
                    val imageUrl = UrlConstant.getValidImageUrlWithTimestamp(dataKeluhan.gambar)
                    logDebug("Loading image from: $imageUrl")

                    Glide.with(this)
                        .load(imageUrl)
                        .placeholder(R.drawable.image_error)
                        .error(R.drawable.image_error)
                        .into(binding.imageKeluhan)
                } else {
                    binding.imageKeluhan.setImageResource(R.drawable.image_error)
                }

                if (type == Constant.TYPE_UPDATE) {
                    selectedImageUri = null
                }

                logDebug("Form data set completed")
            }
        }

        // Observe error
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Log.e("DetailkeluhanFragment", "ViewModel Error: $it")
                // ✅ Error toast SELALU ditampilkan (DEBUG & RELEASE)
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }

        // Observe loading state
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.btnAjukan.isEnabled = !isLoading
            binding.btnTambahGambar.isEnabled = !isLoading && type != Constant.TYPE_DETAIL

            binding.btnAjukan.text = if (isLoading) {
                "Memproses..."
            } else {
                when(type) {
                    Constant.TYPE_CREATE -> "Ajukan Keluhan"
                    Constant.TYPE_UPDATE -> "Perbarui Keluhan"
                    else -> "Ajukan"
                }
            }
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICKER_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMAGE_PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                selectedImageUri = uri
                logDebug("Image selected: $uri")

                Glide.with(this)
                    .load(uri)
                    .placeholder(R.drawable.image_error)
                    .error(R.drawable.image_error)
                    .into(binding.imageKeluhan)

                // ✅ Info toast HANYA di DEBUG mode
                showDebugToast("Gambar dipilih. Akan dikompres otomatis saat upload.")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        selectedImageUri = null
        _binding = null
    }

    // ✅ Helper function untuk debug log
    private fun logDebug(message: String) {
        if (BuildConfig.DEBUG) {
            Log.d("DetailkeluhanFragment", message)
        }
    }

    // ✅ Helper function untuk debug toast
    private fun showDebugToast(message: String) {
        if (BuildConfig.DEBUG) {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
    }
}