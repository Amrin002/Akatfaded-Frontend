package com.localclasstech.layanandesa.auth.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.localclasstech.layanandesa.R
import com.localclasstech.layanandesa.auth.viewmodel.RegisterViewModel
import com.localclasstech.layanandesa.databinding.ActivityRegisterBinding
import com.localclasstech.layanandesa.network.RegisterRequest

@SuppressLint("ClickableViewAccessibility")
class RegisterActivity : AppCompatActivity() {
    private lateinit var viewModel: RegisterViewModel
    private var isPasswordVisible = false
    private var isConfirmPasswordVisible = false
    private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        viewModel = RegisterViewModel(this)

        binding.backButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        val originalTypefacePassword = binding.etRegisterPassword.typeface
        val originalTypefaceConfirm = binding.etRegisterKonfirmasiPassword.typeface


        binding.etRegisterPassword.setOnTouchListener { v, event ->
            if (event.action == android.view.MotionEvent.ACTION_UP) {
                val drawableEnd = binding.etRegisterPassword.compoundDrawablesRelative[2]
                if (drawableEnd != null) {
                    val drawableWidth = drawableEnd.bounds.width()
                    val touchX = event.rawX.toInt()
                    val editTextRight = binding.etRegisterPassword.right
                    val clickArea = editTextRight - drawableWidth - binding.etRegisterPassword.paddingEnd
                    if (touchX >= clickArea) {
                        isPasswordVisible = !isPasswordVisible
                        if (isPasswordVisible) {
                            binding.etRegisterPassword.inputType = android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                            binding.etRegisterPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_show_pass, 0)
                        } else {
                            binding.etRegisterPassword.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
                            binding.etRegisterPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_eye_block, 0)
                        }
                        binding.etRegisterPassword.typeface = originalTypefacePassword
                        binding.etRegisterPassword.setSelection(binding.etRegisterPassword.text.length)
                        return@setOnTouchListener true
                    }
                }
            }
            false
        }

        binding.etRegisterKonfirmasiPassword.setOnTouchListener { v, event ->
            if (event.action == android.view.MotionEvent.ACTION_UP) {
                val drawableEnd = binding.etRegisterKonfirmasiPassword.compoundDrawablesRelative[2]
                if (drawableEnd != null) {
                    val drawableWidth = drawableEnd.bounds.width()
                    val touchX = event.rawX.toInt()
                    val editTextRight = binding.etRegisterKonfirmasiPassword.right
                    val clickArea = editTextRight - drawableWidth - binding.etRegisterKonfirmasiPassword.paddingEnd
                    if (touchX >= clickArea) {
                        isConfirmPasswordVisible = !isConfirmPasswordVisible
                        if (isConfirmPasswordVisible) {
                            binding.etRegisterKonfirmasiPassword.inputType = android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                            binding.etRegisterKonfirmasiPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_show_pass, 0)
                        } else {
                            binding.etRegisterKonfirmasiPassword.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
                            binding.etRegisterKonfirmasiPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_eye_block, 0)
                        }
                        binding.etRegisterKonfirmasiPassword.typeface = originalTypefaceConfirm
                        binding.etRegisterKonfirmasiPassword.setSelection(binding.etRegisterKonfirmasiPassword.text.length)
                        return@setOnTouchListener true
                    }
                }
            }
            false
        }


        binding.btnRegister.setOnClickListener {
            val nik = binding.etRegisterNik.text.toString()
            val email = binding.etRegisterEmail.text.toString()
            val password = binding.etRegisterPassword.text.toString()
            val confirmPassword = binding.etRegisterKonfirmasiPassword.text.toString()
            val phone = binding.etRegisterNoTelp.text.toString()

            viewModel.registerUser(
                email = email,
                nik = nik,
                noTelp = phone,
                password = password,
                confirmPassword = confirmPassword
            )
        }
        viewModel.registrationSuccess.observe(this) { success ->
            if (success) {
                // Pindah ke LoginActivity
                val intent = Intent(this, LoginActivity::class.java).apply {
                    // Tambahkan extra untuk menampilkan pesan sukses di LoginActivity
                    putExtra("REGISTRATION_SUCCESS", true)
                }
                startActivity(intent)
                finish() // Tutup RegisterActivity
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            // Update the button and progress bar visibility
            binding.btnRegister.isEnabled = !isLoading
            binding.progressBarRegister.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnRegister.text = if (isLoading) "" else "Daftar"

        }

        viewModel.errorMessage.observe(this) { message ->
            if (!message.isNullOrEmpty()) {
                binding.tvNikError.text = message
                binding.tvNikError.visibility = View.VISIBLE
            } else {
                binding.tvNikError.visibility = View.GONE
            }
        }




    }
}