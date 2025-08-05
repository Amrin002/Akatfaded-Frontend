package com.localclasstech.layanandesa.feature.keluhan.view

import android.app.Activity
import android.content.Context
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
import com.localclasstech.layanandesa.R
import com.localclasstech.layanandesa.databinding.FragmentDetailkeluhanBinding
import com.localclasstech.layanandesa.feature.keluhan.data.KeluhanRequest
import com.localclasstech.layanandesa.feature.keluhan.viewmodel.DetailkeluhanViewModel
import com.localclasstech.layanandesa.feature.keluhan.viewmodel.DetailkeluhanViewModelFactory
import com.localclasstech.layanandesa.settings.utils.Constant
import com.localclasstech.layanandesa.settings.utils.DialogHelper
import com.localclasstech.layanandesa.settings.utils.UrlConstant
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class DetailkeluhanFragment : Fragment() {
    private var _binding: FragmentDetailkeluhanBinding? = null
    private val binding get() = _binding!!

    private val IMAGE_PICKER_REQUEST_CODE = 1001
    private var selectedImageUri: Uri? = null
    private var selectedImageFile: File? = null

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
            Log.d("DetailkeluhanFragment", "Fetching detail for ID: $id, Type: $type")
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
            if (binding.btnTambahGambar.isEnabled) {
                openImagePicker()
            }
        }

        configureUIBasedOnType(type)
        observeDetailData(type)
        setupSubmitButton(type, id)
    }

    private fun validateForm(): Boolean {
        return binding.etJudul.text.isNotBlank() &&
                binding.etIsiKeluhan.text.isNotBlank()
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
            if (validateForm()) {
                val keluhanData = collectFormData()
                when (type) {
                    Constant.TYPE_CREATE -> {
                        viewModel.createKeluhan(keluhanData)
                    }
                    Constant.TYPE_UPDATE -> {
                        viewModel.updateKeluhan(idKeluhan, keluhanData)
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Silahkan isi semua form", Toast.LENGTH_SHORT).show()
            }
        }

        // Observe operation result
        viewModel.operationResult.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                Toast.makeText(
                    requireContext(),
                    if (type == Constant.TYPE_CREATE) "Keluhan berhasil dibuat" else "Keluhan berhasil diperbarui",
                    Toast.LENGTH_SHORT
                ).show()
                parentFragmentManager.popBackStack()
            }
        }
    }

    private fun collectFormData(): KeluhanRequest {
        // Langsung ambil String, tidak perlu dikonversi ke RequestBody
        val judul = binding.etJudul.text.toString()
        val isiKeluhan = binding.etIsiKeluhan.text.toString()
        // Debug log
        Log.d("DetailkeluhanFragment", "Collecting form data - judul: $judul, isi: $isiKeluhan")

        val imagePart = selectedImageFile?.let { file ->
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("gambar", file.name, requestFile)
        }

        return KeluhanRequest(
            judul = judul,
            isi = isiKeluhan,
            gambar = imagePart
        )
    }

    private fun configureUIBasedOnType(type: Int) {
        when (type) {
            Constant.TYPE_DETAIL -> {
                // Disable semua input untuk mode DETAIL
                binding.etJudul.isEnabled = false
                binding.etIsiKeluhan.isEnabled = false
                binding.btnTambahGambar.isEnabled = false
                binding.btnAjukan.visibility = View.GONE
                binding.btnEditKeluhan.visibility = View.VISIBLE
                binding.btnDeleteKeluhan.visibility = View.VISIBLE
            }
            Constant.TYPE_CREATE -> {
                // Enable semua input untuk mode CREATE
                binding.etJudul.isEnabled = true
                binding.etIsiKeluhan.isEnabled = true
                binding.btnTambahGambar.isEnabled = true
                binding.btnEditKeluhan.visibility = View.GONE
                binding.btnDeleteKeluhan.visibility = View.GONE
                binding.btnAjukan.visibility = View.VISIBLE
                binding.btnAjukan.text = "Ajukan Keluhan"
            }
            Constant.TYPE_UPDATE -> {
                // Enable semua input untuk mode UPDATE
                binding.etJudul.isEnabled = true
                binding.etIsiKeluhan.isEnabled = true
                binding.btnTambahGambar.isEnabled = true
                binding.btnAjukan.visibility = View.VISIBLE
                binding.btnEditKeluhan.visibility = View.GONE
                binding.btnDeleteKeluhan.visibility = View.GONE
                binding.btnAjukan.text = "Perbarui Keluhan"
            }
        }
    }

    private fun observeDetailData(type: Int) {
        Log.d("DetailkeluhanFragment", "observeDetailData called with type: $type")

        // Observe delete result
        viewModel.operationResult.observe(viewLifecycleOwner) { isSuccess ->
            // Hasil delete juga menggunakan operationResult, bisa dibedakan berdasarkan konteks
        }

        // Observe detail data
        viewModel.keluhanDetail.observe(viewLifecycleOwner) { dataKeluhan ->
            Log.d("DetailkeluhanFragment", "Received data: $dataKeluhan")

            if (dataKeluhan != null && (type == Constant.TYPE_DETAIL || type == Constant.TYPE_UPDATE)) {
                // Fill form dengan data
                binding.etJudul.setText(dataKeluhan.judul)
                binding.etIsiKeluhan.setText(dataKeluhan.isi)

                // Load gambar menggunakan UrlConstant - PERBAIKAN DI SINI
                if (!dataKeluhan.gambar.isNullOrEmpty()) {
                    val imageUrl = UrlConstant.getValidImageUrl(dataKeluhan.gambar)
                    Log.d("DetailkeluhanFragment", "Loading image from: $imageUrl")

                    Glide.with(this)
                        .load(imageUrl)
                        .placeholder(R.drawable.image_error)
                        .error(R.drawable.image_error)
                        .into(binding.imageKeluhan)
                }

                Log.d("DetailkeluhanFragment", "Form data set completed")
            }
        }

        // Observe error
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            Log.e("DetailkeluhanFragment", "ViewModel Error: $errorMessage")
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
        }

        // Observe loading state
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.btnAjukan.isEnabled = !isLoading
            // Jika ada progress bar di layout, bisa ditampilkan/disembunyikan di sini
            binding.btnAjukan.text = if (isLoading) "Prosess..." else when(type) {
                Constant.TYPE_CREATE -> "Ajukan Keluhan"
                Constant.TYPE_UPDATE -> "Perbarui Keluhan"
                else -> "Ajukan"
            }
            binding.btnAjukan.isEnabled = if (isLoading) false else true
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, IMAGE_PICKER_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMAGE_PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                selectedImageUri = uri

                // Display image in ImageView
                Glide.with(this)
                    .load(uri)
                    .placeholder(R.drawable.image_error)
                    .error(R.drawable.image_error)
                    .into(binding.imageKeluhan)

                // Convert URI to File for upload
                convertUriToFile(uri)
            }
        }
    }

    private fun convertUriToFile(uri: Uri) {
        try {
            val inputStream: InputStream? = requireContext().contentResolver.openInputStream(uri)
            inputStream?.let { stream ->
                val fileName = "keluhan_image_${System.currentTimeMillis()}.jpg"
                val file = File(requireContext().cacheDir, fileName)

                val outputStream = FileOutputStream(file)
                stream.copyTo(outputStream)

                outputStream.close()
                stream.close()

                selectedImageFile = file
                Log.d("DetailkeluhanFragment", "Image file created: ${file.absolutePath}")
            }
        } catch (e: Exception) {
            Log.e("DetailkeluhanFragment", "Error converting URI to file: ${e.message}")
            Toast.makeText(requireContext(), "Gagal memproses gambar", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}