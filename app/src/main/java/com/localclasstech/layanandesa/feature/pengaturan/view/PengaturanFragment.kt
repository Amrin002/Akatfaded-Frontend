package com.localclasstech.layanandesa.feature.pengaturan.view

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.Typeface
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.localclasstech.layanandesa.R
import com.localclasstech.layanandesa.auth.view.LoginActivity
import com.localclasstech.layanandesa.auth.viewmodel.LoginViewModel
import com.localclasstech.layanandesa.auth.viewmodel.LoginViewModelFactory
import com.localclasstech.layanandesa.databinding.FragmentPengaturanBinding
import com.localclasstech.layanandesa.feature.pengaturan.view.editprofille.EditProfileFragment
import com.localclasstech.layanandesa.feature.pengaturan.viewmodel.PengaturanViewModel
import com.localclasstech.layanandesa.feature.pengaturan.viewmodel.SharedThemeViewModel
import com.localclasstech.layanandesa.feature.pengaturan.viewmodel.SharedThemeViewModelFactory
import com.localclasstech.layanandesa.settings.PreferencesHelper
import com.localclasstech.layanandesa.view.getstarted.GetstartedActivity

class PengaturanFragment : Fragment() {

    private var isUpdatingSwitch = false

    private var _binding: FragmentPengaturanBinding? = null
    private val binding get() = _binding!!

    private lateinit var preferencesHelper: PreferencesHelper

    private val sharedThemeViewModel: SharedThemeViewModel by activityViewModels {
        SharedThemeViewModelFactory(preferencesHelper)
    }

    private val loginViewModel: LoginViewModel by activityViewModels {
        LoginViewModelFactory(requireContext())
    }

    private val viewModel: PengaturanViewModel by viewModels{
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return PengaturanViewModel(preferencesHelper) as T
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferencesHelper = PreferencesHelper(requireContext())

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPengaturanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        viewModel.isModernTheme.observe(viewLifecycleOwner) { isModern ->
//            if (!isUpdatingSwitch) {
//                isUpdatingSwitch = true
//                updateUi(isModern)
//                binding.switchThema.isChecked = isModern
//                sharedThemeViewModel.setTheme(isModern)
//                isUpdatingSwitch = false
//            }
//        }



        observeViewModel()

        // Observer untuk mengupdate textUsername sesuai status login
        loginViewModel.loginMode.observe(viewLifecycleOwner) { loginMode ->
            binding.textUsername.text = when {
                loginMode.isNullOrEmpty() -> "Tidak ada yang login"
                loginMode == "Guest" -> "Guest"
                else -> loginMode
            }
        }
        loginViewModel.image.observe(viewLifecycleOwner) { image ->
            val baseUrl = "http://192.168.56.1:8000/storage/" //ingat untuk selalu di ganti ketika memulai project
            val fullImageUrl = if (image.startsWith("http")) image else baseUrl + image

            Log.d("Profile Image", "Full URL: $image")

            if (!image.isNullOrEmpty()) {
                Glide.with(this)
                    .load(fullImageUrl)
                    .transform(CircleCrop())
                    .into(binding.imageProfile)
            } else {
               binding.imageProfile.setImageResource(R.drawable.ic_profile_default)
            }
        }


//        binding.switchThema.setOnCheckedChangeListener { _, isChecked ->
//            if (!isUpdatingSwitch) {
//                isUpdatingSwitch = true
//                confirmSwitchTheme(isChecked) // Tampilkan dialog
//                sharedThemeViewModel.setTheme(isChecked)
//                viewModel.toggleTheme()
//                isUpdatingSwitch = false
//            }
//        }
        binding.layoutEditProfille.setOnClickListener{
            val fragmentEditProfile = EditProfileFragment()
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentView, fragmentEditProfile)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        binding.layoutLogout.setOnClickListener{
            showCustomDialog(
                message = "Apakah anda yakin ingin keluar?",
                onConfirm = {
                    viewModel.confirmLogout()
                    navigateToLogin()
                    Log.d("PengaturanVM", "login_mode cleared? ${preferencesHelper.getLoginMode() == null}")
                }
            )
        }

    }

    // New centralized method for navigating to login
    private fun navigateToLogin() {
        val intent = Intent(requireContext(), LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        Toast.makeText(requireContext(), "Anda berhasil keluar", Toast.LENGTH_SHORT).show()
        requireActivity().finish()
    }
    private fun observeViewModel() {
        viewModel.logoutEvent.observe(viewLifecycleOwner) { shouldLogout ->
            if (shouldLogout) {
                // Clear semua aktivitas dan mulai LoginActivity
                val intent = Intent(requireContext(), LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
                activity?.finish()
            }
        }
    }



    private fun showCustomDialog(
        message: String,
        onConfirm: () -> Unit,
        onCancel: (() -> Unit) ? = null
    ){
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.costum_pop_up, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()
        dialogView.findViewById<TextView>(R.id.textMessage).text = message

        val btnConfirm = dialogView.findViewById<AppCompatButton>(R.id.btnYes)
        val btnCancel = dialogView.findViewById<AppCompatButton>(R.id.btnNo)
        btnConfirm.setOnClickListener {
            onConfirm()
            dialog.dismiss()
        }
        btnCancel.setOnClickListener {
            onCancel?.invoke()
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun restartApp() {
        val context = requireContext().applicationContext
        val intent = requireActivity().intent
        val restartIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        restartIntent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        requireActivity().finish()
        android.os.Handler().postDelayed({
            context.startActivity(restartIntent) // Mulai ulang aplikasi
        }, 500)
    }

//    private fun updateUi(isModern: Boolean) {
//        binding.switchThema.isChecked = isModern
//        if (isModern) {
//            Toast.makeText(requireContext(),"Tampilan Modern", Toast.LENGTH_SHORT).show()
//        } else {
//            Toast.makeText(requireContext(),"Tampilan Simple", Toast.LENGTH_SHORT).show()
//        }
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}