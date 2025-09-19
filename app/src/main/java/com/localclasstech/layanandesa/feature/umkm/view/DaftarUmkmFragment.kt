package com.localclasstech.layanandesa.feature.umkm.view

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.localclasstech.layanandesa.R
import com.localclasstech.layanandesa.databinding.FragmentDaftarUmkmBinding
import com.localclasstech.layanandesa.feature.umkm.data.Umkm
import com.localclasstech.layanandesa.feature.umkm.viewmodel.DaftarUmkmViewModel
import com.localclasstech.layanandesa.feature.umkm.viewmodel.DaftarUmkmViewModelFactory
import com.localclasstech.layanandesa.settings.utils.Constant
import com.localclasstech.layanandesa.settings.utils.DialogHelper
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class DaftarUmkmFragment : Fragment() {
    private var _binding: FragmentDaftarUmkmBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: DaftarUmkmViewModel
    private var type = Constant.TYPE_CREATE
    private var umkmId = -1

    private val IMAGE_PICKER_REQUEST_CODE = 1001
    private var selectedImageUri: Uri? = null
    private var selectedImageFile: File? = null

    companion object {
        fun newInstance() = DaftarUmkmFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val factory = DaftarUmkmViewModelFactory(requireContext())
        viewModel = ViewModelProvider(this, factory)[DaftarUmkmViewModel::class.java]

        // Get arguments
        umkmId = arguments?.getInt("umkm_id", -1) ?: -1
        type = arguments?.getInt("type", Constant.TYPE_CREATE) ?: Constant.TYPE_CREATE

        // Set mode berdasarkan type
        if (type == Constant.TYPE_UPDATE && umkmId != -1) {
            viewModel.setEditMode(umkmId)
        } else {
            viewModel.setCreateMode()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDaftarUmkmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.btnPilihFoto.setOnClickListener {
            openImagePicker()
        }

        setupSpinner()
        configureUIBasedOnType()
        observeViewModel()
        setupSubmitButton()

        // Load options dan detail (jika edit mode)
        viewModel.fetchUmkmOptions()
        // TAMBAHAN: Check existing business untuk user baru
        if (type == Constant.TYPE_CREATE) {
            viewModel.checkExistingBusiness()
        }
    }
    // TAMBAHAN: Method baru untuk setup spinner existing business
    private fun setupExistingBusinessSpinner(businessNames: List<String>) {
        val spinnerData = mutableListOf("Pilih Usaha Existing").apply {
            addAll(businessNames)
            add("+ Buat Usaha Baru")
        }

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            spinnerData
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerPilihUsaha.adapter = adapter

        // Handle spinner selection
        binding.spinnerPilihUsaha.setOnItemSelectedListener(object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                when {
                    position == 0 -> {
                        // "Pilih Usaha Existing" - do nothing
                    }
                    position == spinnerData.size - 1 -> {
                        // "+ Buat Usaha Baru" - show full form
                        binding.groupUsahaSection.visibility = View.VISIBLE
                        binding.groupExistingUsahaSection.visibility = View.GONE
                        binding.tvDaftarUmkm.text = "Daftar Usaha Baru"
                        binding.btnDaftarUmkm.text = "Daftar UMKM"
                    }
                    else -> {
                        // Existing business selected - hide full form
                        binding.groupUsahaSection.visibility = View.GONE
                        binding.groupExistingUsahaSection.visibility = View.VISIBLE
                        binding.tvDaftarUmkm.text = "Tambah Produk ke ${spinnerData[position]}"
                        binding.btnDaftarUmkm.text = "Tambah Produk"
                    }
                }
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        })
    }

    // TAMBAHAN: Method untuk configure form visibility
    private fun configureFormVisibility(hasExistingBusiness: Boolean) {
        if (type == Constant.TYPE_CREATE && hasExistingBusiness) {
            // User sudah punya usaha - tampilkan dropdown pilih usaha
            binding.groupUsahaSection.visibility = View.GONE
            binding.groupExistingUsahaSection.visibility = View.VISIBLE

            // Update title dan button text
            binding.tvDaftarUmkm.text = "Tambah Produk Baru"
            binding.btnDaftarUmkm.text = "Tambah Produk"

        } else {
            // User baru atau edit mode - tampilkan form lengkap
            binding.groupUsahaSection.visibility = View.VISIBLE
            binding.groupExistingUsahaSection.visibility = View.GONE

            // Title sesuai mode
            when (type) {
                Constant.TYPE_CREATE -> {
                    binding.tvDaftarUmkm.text = "Daftar Usaha & Produk Pertama"
                    binding.btnDaftarUmkm.text = "Daftar UMKM"
                }
                Constant.TYPE_UPDATE -> {
                    binding.tvDaftarUmkm.text = "Edit UMKM"
                    binding.btnDaftarUmkm.text = "Perbarui UMKM"
                }
            }
        }
    }

    private fun setupSpinner() {
        val kategoriArray = resources.getStringArray(R.array.kategori_umkm)
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            kategoriArray
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerKategori.adapter = adapter
    }

    private fun configureUIBasedOnType() {
        when (type) {
            Constant.TYPE_CREATE -> {
                binding.tvDaftarUmkm.text = "Daftar UMKM"
                binding.btnDaftarUmkm.text = "Daftar UMKM"
            }
            Constant.TYPE_UPDATE -> {
                binding.tvDaftarUmkm.text = "Edit UMKM"
                binding.btnDaftarUmkm.text = "Perbarui UMKM"
            }
        }
    }

    private fun observeViewModel() {
        // Observe loading state
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.btnDaftarUmkm.isEnabled = !isLoading
            binding.btnDaftarUmkm.text = if (isLoading) {
                "Loading..."
            } else {
                when (type) {
                    Constant.TYPE_CREATE -> "Daftar UMKM"
                    Constant.TYPE_UPDATE -> "Perbarui UMKM"
                    else -> "Submit"
                }
            }
        }

        // Observe UMKM detail untuk edit mode
        viewModel.umkmDetail.observe(viewLifecycleOwner) { umkm ->
            umkm?.let { populateFormWithData(it) }
        }

        // Observe submit result
        viewModel.submitResult.observe(viewLifecycleOwner) { success ->
            if (success) {
                val message = if (type == Constant.TYPE_UPDATE) {
                    "UMKM berhasil diperbarui"
                } else {
                    "UMKM berhasil didaftarkan"
                }
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            }
        }

        // Observe error message
        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            if (error.isNotEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()
                viewModel.resetErrorMessage()
            }
        }

        // Observe options untuk spinner
        viewModel.umkmOptions.observe(viewLifecycleOwner) { options ->
            options?.data?.kategori_options?.let { kategoriOptions ->
                // Update spinner jika perlu dengan data dari server
                Log.d("DaftarUmkmFragment", "Kategori options: $kategoriOptions")
            }
        }
        // TAMBAHAN: Observe existing business status
        viewModel.hasExistingBusiness.observe(viewLifecycleOwner) { hasExisting ->
            configureFormVisibility(hasExisting)
        }

        // TAMBAHAN: Observe existing business names
        viewModel.existingBusinessNames.observe(viewLifecycleOwner) { businessNames ->
            setupExistingBusinessSpinner(businessNames)
        }
    }

    private fun populateFormWithData(umkm: Umkm) {
        binding.etNamaUsaha.setText(umkm.nama_usaha)
        binding.etNamaProduk.setText(umkm.nama_produk)
        binding.etDeskripsiProduk.setText(umkm.deskripsi_produk)
        binding.etHargaProduk.setText(umkm.harga_produk)
        binding.etNomorTelepon.setText(umkm.nomor_telepon)
        binding.etLinkFacebook.setText(umkm.link_facebook)
        binding.etLinkInstagram.setText(umkm.link_instagram)
        binding.etLinkTiktok.setText(umkm.link_tiktok)

        // Set kategori spinner
        val kategoriArray = resources.getStringArray(R.array.kategori_umkm)
        val kategoriLabel = when(umkm.kategori) {
            "makanan" -> "Makanan & Minuman"
            "jasa" -> "Jasa"
            "kerajinan" -> "Kerajinan"
            "pertanian" -> "Pertanian"
            "perdagangan" -> "Perdagangan"
            else -> "Lainnya"
        }
        val kategoriIndex = kategoriArray.indexOf(kategoriLabel)
        if (kategoriIndex >= 0) {
            binding.spinnerKategori.setSelection(kategoriIndex)
        }

        // Load existing image
        if (!umkm.foto_produk.isNullOrEmpty()) {
            Glide.with(this)
                .load(umkm.foto_produk)
                .placeholder(R.drawable.ic_image_placeholder)
                .into(binding.imgPreviewProduk)
        }
    }

    private fun setupSubmitButton() {
        binding.btnDaftarUmkm.setOnClickListener {
            if (validateForm()) {
                submitForm()
            }
        }
    }

    // UPDATE: Method validateForm dengan logic dynamic
    private fun validateForm(): Boolean {
        val hasExistingBusiness = viewModel.hasExistingBusiness.value ?: false

        if (type == Constant.TYPE_CREATE && hasExistingBusiness) {
            // Mode tambah produk ke usaha existing
            val selectedPosition = binding.spinnerPilihUsaha.selectedItemPosition
            val isNewBusiness = selectedPosition == (binding.spinnerPilihUsaha.adapter?.count ?: 0) - 1

            if (!isNewBusiness && selectedPosition == 0) {
                Toast.makeText(requireContext(), "Pilih usaha terlebih dahulu", Toast.LENGTH_SHORT).show()
                return false
            }

            if (isNewBusiness) {
                // Validate full form untuk usaha baru
                return validateFullForm()
            } else {
                // Validate hanya produk fields
                return validateProductFields()
            }
        } else {
            // Mode normal - validate full form
            return validateFullForm()
        }
    }
    // TAMBAHAN: Method untuk validate full form
    private fun validateFullForm(): Boolean {
        val namaUsaha = binding.etNamaUsaha.text.toString().trim()
        val namaProduk = binding.etNamaProduk.text.toString().trim()
        val deskripsi = binding.etDeskripsiProduk.text.toString().trim()
        val nomorTelepon = binding.etNomorTelepon.text.toString().trim()

        when {
            namaUsaha.isEmpty() -> {
                binding.etNamaUsaha.error = "Nama usaha wajib diisi"
                return false
            }
            binding.spinnerKategori.selectedItemPosition == 0 -> {
                Toast.makeText(requireContext(), "Pilih kategori usaha", Toast.LENGTH_SHORT).show()
                return false
            }
            namaProduk.isEmpty() -> {
                binding.etNamaProduk.error = "Nama produk wajib diisi"
                return false
            }
            deskripsi.isEmpty() -> {
                binding.etDeskripsiProduk.error = "Deskripsi produk wajib diisi"
                return false
            }
            nomorTelepon.isEmpty() -> {
                binding.etNomorTelepon.error = "Nomor telepon wajib diisi"
                return false
            }
            else -> return true
        }
    }

    // TAMBAHAN: Method untuk validate product fields saja
    private fun validateProductFields(): Boolean {
        val namaProduk = binding.etNamaProduk.text.toString().trim()
        val deskripsi = binding.etDeskripsiProduk.text.toString().trim()

        when {
            namaProduk.isEmpty() -> {
                binding.etNamaProduk.error = "Nama produk wajib diisi"
                return false
            }
            deskripsi.isEmpty() -> {
                binding.etDeskripsiProduk.error = "Deskripsi produk wajib diisi"
                return false
            }
            else -> return true
        }
    }

    // UPDATE: Method submitForm dengan logic dynamic
    private fun submitForm() {
        val hasExistingBusiness = viewModel.hasExistingBusiness.value ?: false

        if (type == Constant.TYPE_CREATE && hasExistingBusiness) {
            val selectedPosition = binding.spinnerPilihUsaha.selectedItemPosition
            val isNewBusiness = selectedPosition == (binding.spinnerPilihUsaha.adapter?.count ?: 0) - 1

            if (!isNewBusiness) {
                // Tambah produk ke usaha existing
                val selectedBusinessName = binding.spinnerPilihUsaha.selectedItem.toString()
                submitProductToExistingBusiness(selectedBusinessName)
                return
            }
        }

        // Submit full form (usaha baru atau edit)
        submitFullForm()
    }
    // TAMBAHAN: Method untuk submit produk ke usaha existing
    private fun submitProductToExistingBusiness(businessName: String) {
        val namaProduk = binding.etNamaProduk.text.toString().trim()
        val deskripsiProduk = binding.etDeskripsiProduk.text.toString().trim()
        val hargaProduk = binding.etHargaProduk.text.toString().trim().toDoubleOrNull()

        viewModel.submitUmkm(
            namaUsaha = businessName,
            kategori = "makanan", // Default, seharusnya ambil dari existing business
            namaProduk = namaProduk,
            deskripsiProduk = deskripsiProduk,
            hargaProduk = hargaProduk,
            nomorTelepon = "08123456789", // Default, ambil dari existing business
            linkFacebook = null,
            linkInstagram = null,
            linkTiktok = null,
            imageFile = selectedImageFile
        )
    }
    // RENAME: Method submitForm yang lama jadi submitFullForm
    private fun submitFullForm() {
        val namaUsaha = binding.etNamaUsaha.text.toString().trim()
        val kategori = getSelectedKategori()
        val namaProduk = binding.etNamaProduk.text.toString().trim()
        val deskripsiProduk = binding.etDeskripsiProduk.text.toString().trim()
        val hargaProduk = binding.etHargaProduk.text.toString().trim().toDoubleOrNull()
        val nomorTelepon = binding.etNomorTelepon.text.toString().trim()
        val linkFacebook = binding.etLinkFacebook.text.toString().trim().takeIf { it.isNotEmpty() }
        val linkInstagram = binding.etLinkInstagram.text.toString().trim().takeIf { it.isNotEmpty() }
        val linkTiktok = binding.etLinkTiktok.text.toString().trim().takeIf { it.isNotEmpty() }

        viewModel.submitUmkm(
            namaUsaha = namaUsaha,
            kategori = kategori,
            namaProduk = namaProduk,
            deskripsiProduk = deskripsiProduk,
            hargaProduk = hargaProduk,
            nomorTelepon = nomorTelepon,
            linkFacebook = linkFacebook,
            linkInstagram = linkInstagram,
            linkTiktok = linkTiktok,
            imageFile = selectedImageFile
        )
    }


    private fun getSelectedKategori(): String {
        return when (binding.spinnerKategori.selectedItem.toString()) {
            "Makanan & Minuman" -> "makanan"
            "Jasa" -> "jasa"
            "Kerajinan" -> "kerajinan"
            "Pertanian" -> "pertanian"
            "Perdagangan" -> "perdagangan"
            else -> "lainnya"
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
                selectedImageFile = createFileFromUri(uri)

                // Show preview
                Glide.with(this)
                    .load(uri)
                    .placeholder(R.drawable.ic_image_placeholder)
                    .into(binding.imgPreviewProduk)
            }
        }
    }

    private fun createFileFromUri(uri: Uri): File? {
        return try {
            val inputStream: InputStream? = requireContext().contentResolver.openInputStream(uri)
            val file = File(requireContext().cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
            val outputStream = FileOutputStream(file)

            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()

            file
        } catch (e: Exception) {
            Log.e("DaftarUmkmFragment", "Error creating file from URI: ${e.message}")
            null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}