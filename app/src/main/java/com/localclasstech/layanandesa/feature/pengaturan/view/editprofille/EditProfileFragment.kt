package com.localclasstech.layanandesa.feature.pengaturan.view.editprofille

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputType
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.localclasstech.layanandesa.R
import com.localclasstech.layanandesa.auth.viewmodel.LoginViewModel
import com.localclasstech.layanandesa.auth.viewmodel.LoginViewModelFactory
import com.localclasstech.layanandesa.databinding.FragmentEditProfileBinding
import com.localclasstech.layanandesa.feature.pengaturan.viewmodel.editprofile.EditProfileViewModel
import com.localclasstech.layanandesa.feature.pengaturan.viewmodel.editprofile.EditProfileViewModelFactory
import com.localclasstech.layanandesa.settings.utils.UrlConstant
import java.io.File
import java.io.FileOutputStream

class EditProfileFragment : Fragment() {
    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!
    private var selectedImageUri: Uri? = null

    // ViewModels
    private val viewModel: EditProfileViewModel by viewModels {
        EditProfileViewModelFactory(requireContext())
    }
    private val loginViewModel: LoginViewModel by activityViewModels {
        LoginViewModelFactory(requireContext())
    }

    // Activity Result Launcher untuk galeri
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedImageUri = uri
                // Tampilkan gambar yang dipilih
                Glide.with(this)
                    .load(uri)
                    .transform(CircleCrop())
                    .into(binding.imageProfileEdit)

                Log.d("EditProfileFragment", "Gambar dipilih: $uri")
            }
        }
    }

    // Launcher untuk permintaan izin single
    private val requestSinglePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            openGallery()
        } else {
            Toast.makeText(
                requireContext(),
                "Izin penyimpanan diperlukan untuk mengubah foto profil",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Launcher untuk permintaan multiple permissions (for Android 13+)
    private val requestMultiplePermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // Check if all required permissions are granted
        val allGranted = permissions.all { it.value }
        if (allGranted) {
            openGallery()
        } else {
            Toast.makeText(
                requireContext(),
                "Izin akses media diperlukan untuk mengubah foto profil",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Fetch user profile
        viewModel.fetchUserProfile()

        // Observasi profil pengguna
        viewModel.userProfile.observe(viewLifecycleOwner) { userData ->
            Log.d("EditProfileFragment", "User Data Received: $userData")
            binding.etNamaEdit.setText(userData.name)
            binding.etNamaEdit.isEnabled = false
            binding.etEmailEdit.setText(userData.email)
            binding.etNikEdit.setText(userData.nik)
            binding.etNikEdit.isEnabled = false
            binding.etNomorEdit.setText(userData.noTelp)

            // Tampilkan gambar profil jika ada
            if (!userData.image.isNullOrEmpty()) {
                val fullImageUrl = UrlConstant.getValidImageUrl(userData.image)

                Glide.with(this)
                    .load(fullImageUrl)
                    .transform(CircleCrop())
                    .into(binding.imageProfileEdit)
            }
        }

        // Tombol untuk edit foto profil
        binding.editButton.setOnClickListener {
            showImagePickerDialog()
        }

        // Tombol simpan
        binding.btnSimpan.setOnClickListener {
            val name = binding.etNamaEdit.text.toString().trim()
            val email = binding.etEmailEdit.text.toString().trim()
            val nik = binding.etNikEdit.text.toString().trim()
            val noTelp = binding.etNomorEdit.text.toString().trim()
            val password = binding.etPasswordEdit.text.toString().trim()

            // Jika ada gambar yang dipilih, simpan dan perbarui profil dengan gambar
            val file = selectedImageUri?.let { getFileFromUri(it) }

            viewModel.updateUserProfile(
                name = name,
                email = email,
                nik = nik,
                noTelp = noTelp,
                password = password.takeIf { it.isNotBlank() },
                imageFile = file // bisa null atau File, tergantung user memilih gambar atau tidak
            )

        }
        setupPasswordVisibility()

        // Tombol batal
        binding.btnCancel.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Observasi hasil operasi
        // Observe update result and propagate to LoginViewModel
        viewModel.operationResult.observe(viewLifecycleOwner) { success ->
            if (success) {
                val updated = viewModel.userProfile.value
                updated?.let {
                    loginViewModel.updateUserProfile(it.name, it.image)
                }
                Toast.makeText(requireContext(), "Profil berhasil diperbarui", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            } else {
                Toast.makeText(requireContext(), "Gagal memperbarui profil", Toast.LENGTH_SHORT).show()
            }
        }

        // Observasi error
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
        }
    }
    @SuppressLint("ClickableViewAccessibility")
    private fun setupPasswordVisibility() {
        // Variabel untuk mengecek apakah password sedang ditampilkan atau tidak
        var isPasswordVisible = false

        binding.etPasswordEdit.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = binding.etPasswordEdit.compoundDrawablesRelative[2]
                if (drawableEnd != null) {
                    val drawableBounds = drawableEnd.bounds
                    val clickArea = binding.etPasswordEdit.right - drawableBounds.width() - binding.etPasswordEdit.paddingEnd
                    if (event.rawX >= clickArea) {
                        isPasswordVisible = !isPasswordVisible
                        val currentTypeface = binding.etPasswordEdit.typeface // Simpan font

                        if (isPasswordVisible) {
                            binding.etPasswordEdit.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                            binding.etPasswordEdit.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_show_pass, 0)
                        } else {
                            binding.etPasswordEdit.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                            binding.etPasswordEdit.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_eye_block, 0)
                        }

                        binding.etPasswordEdit.typeface = currentTypeface // Restore font
                        binding.etPasswordEdit.setSelection(binding.etPasswordEdit.text.length)
                        return@setOnTouchListener true
                    }
                }
            }
            false
        }
    }

    private fun showImagePickerDialog() {
        val options = arrayOf("Pilih dari Galeri", "Batal")
        AlertDialog.Builder(requireContext())
            .setTitle("Pilih Foto Profil")
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> checkStoragePermission()
                    1 -> dialog.dismiss()
                }
            }
            .show()
    }

    private fun openCamera() {
        // Implementasi untuk membuka kamera
        Toast.makeText(requireContext(), "Fitur kamera akan segera tersedia", Toast.LENGTH_SHORT).show()
    }

    private fun checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // For Android 13 and higher, use granular permissions
            when {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_MEDIA_IMAGES
                ) == PackageManager.PERMISSION_GRANTED -> {
                    openGallery()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.READ_MEDIA_IMAGES) -> {
                    showPermissionRationaleDialog(
                        "Izin akses media diperlukan untuk memilih foto profil",
                        arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
                    )
                }
                else -> {
                    requestMultiplePermissionsLauncher.launch(
                        arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
                    )
                }
            }
        } else {
            // For Android 12 and lower, use legacy storage permission
            when {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    openGallery()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                    showPermissionRationaleDialog(
                        "Izin penyimpanan diperlukan untuk memilih foto profil",
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                }
                else -> {
                    requestSinglePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }
        }
    }

    private fun showPermissionRationaleDialog(message: String, permissions: Array<String>) {
        AlertDialog.Builder(requireContext())
            .setTitle("Izin Diperlukan")
            .setMessage(message)
            .setPositiveButton("Berikan Izin") { _, _ ->
                requestMultiplePermissionsLauncher.launch(permissions)
            }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .create().show()
    }

    private fun showPermissionRationaleDialog(message: String, permission: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Izin Diperlukan")
            .setMessage(message)
            .setPositiveButton("Berikan Izin") { _, _ ->
                requestSinglePermissionLauncher.launch(permission)
            }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .create().show()
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }

    private fun getFileFromUri(uri: Uri): File? {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val file = File(requireContext().cacheDir, "profile_image_${System.currentTimeMillis()}.jpg")
            inputStream?.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }
            file
        } catch (e: Exception) {
            Log.e("EditProfileFragment", "Error creating file from URI: ${e.message}")
            null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}