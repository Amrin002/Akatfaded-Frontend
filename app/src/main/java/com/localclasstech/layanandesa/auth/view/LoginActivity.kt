package com.localclasstech.layanandesa.auth.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.localclasstech.layanandesa.R
import com.localclasstech.layanandesa.auth.viewmodel.LoginViewModel
import com.localclasstech.layanandesa.auth.viewmodel.LoginViewModelFactory
import com.localclasstech.layanandesa.databinding.ActivityLoginBinding
import com.localclasstech.layanandesa.settings.PreferencesHelper
import com.localclasstech.layanandesa.view.MainActivity
import com.localclasstech.layanandesa.view.getstarted.GetstartedActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels { LoginViewModelFactory(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkLoginStatus()
        setupListeners()
        setupObservers()
        setupPasswordVisibility()
        binding.forgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgetPasswordActivity::class.java))
        }

        binding.btnDaftar.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupPasswordVisibility() {
        // Variabel untuk mengecek apakah password sedang ditampilkan atau tidak
        var isPasswordVisible = false

        binding.loginPassword.setOnTouchListener { _, event ->

            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = binding.loginPassword.compoundDrawablesRelative[2]
                if (drawableEnd != null) {
                    val drawableBounds = drawableEnd.bounds
                    val clickArea = binding.loginPassword.right - drawableBounds.width() - binding.loginPassword.paddingEnd
                    if (event.rawX >= clickArea) {
                        isPasswordVisible = !isPasswordVisible
                        val currentTypeface = binding.loginPassword.typeface // Simpan font

                        if (isPasswordVisible) {
                            binding.loginPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                            binding.loginPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_show_pass, 0)
                        } else {
                            binding.loginPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

                            binding.loginPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_eye_block, 0)
                        }

                        binding.loginPassword.typeface = currentTypeface // Restore font
                        binding.loginPassword.setSelection(binding.loginPassword.text.length)
                        return@setOnTouchListener true
                    }
                }
            }
            false
        }

    }

    private fun checkLoginStatus() {
        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        if (prefs.getString("login_mode", null) != null) {
            navigateToMain()
        }
    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            val nik = binding.loginNik.text.toString()
            val password = binding.loginPassword.text.toString()
            viewModel.login(nik, password)
        }
    }


    private fun setupObservers() {
        viewModel.loginState.observe(this) { isLoggedIn ->
            if (isLoggedIn) navigateToMain()
        }
        viewModel.isLoading.observe(this) { isLoading ->
            binding.btnLogin.isEnabled = !isLoading
            binding.progressBarLogin.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnLogin.text = if (isLoading) "" else "Login"
        }


        viewModel.errorMessage.observe(this) { message ->
            if (message.contains("koneksi", ignoreCase = true)) {
                // Koneksi internet gagal
                binding.loginNik.clearFocus()
                binding.loginPassword.clearFocus()
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()

            } else if (message.contains("credentials", ignoreCase = true) ||
                message.contains("NIK", ignoreCase = true) ||
                message.contains("Unauthorized", ignoreCase = true)) {

                // Salah password/NIK
                binding.loginPassword.clearFocus()
                binding.loginPassword.setText("")
                binding.loginPassword.hint = "NIK atau sandi salah"
                val errorColor = ContextCompat.getColor(this, R.color.textError)
                binding.loginPassword.setHintTextColor(errorColor)

            } else {
                // Error umum lainnya
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                Log.d("LoginActivity", "Error message: $message")
            }
        }



    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
