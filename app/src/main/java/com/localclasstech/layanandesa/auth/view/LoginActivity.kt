package com.localclasstech.layanandesa.auth.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
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

        binding.txtRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupPasswordVisibility() {
        // Variabel untuk mengecek apakah password sedang ditampilkan atau tidak
        var isPasswordVisible = false

        // Set event klik pada ikon di drawableEnd (icon mata)
        binding.loginPassword.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = binding.loginPassword.compoundDrawablesRelative[2] // Ambil drawableEnd
                if (drawableEnd != null) {
                    val drawableBounds = drawableEnd.bounds
                    val clickArea = binding.loginPassword.right - drawableBounds.width() - binding.loginPassword.paddingEnd
                    if (event.rawX >= clickArea) {
                        // Toggle password visibility
                        isPasswordVisible = !isPasswordVisible
                        if (isPasswordVisible) {
                            binding.loginPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                            binding.loginPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_show_pass, 0)
                        } else {
                            binding.loginPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                            binding.loginPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_show_pass, 0)
                        }
                        binding.loginPassword.setSelection(binding.loginPassword.text.length) // Jaga agar cursor tetap di akhir teks
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

        binding.btnGuest.setOnClickListener {
            viewModel.loginAsGuest()
        }
    }

    private fun setupObservers() {
        viewModel.loginState.observe(this) { isLoggedIn ->
            if (isLoggedIn) navigateToMain()
        }

        viewModel.errorMessage.observe(this) { error ->
            if (!error.isNullOrEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
