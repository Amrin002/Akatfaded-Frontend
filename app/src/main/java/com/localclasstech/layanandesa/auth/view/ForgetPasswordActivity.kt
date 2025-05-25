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
import androidx.core.widget.addTextChangedListener
import com.localclasstech.layanandesa.R
import com.localclasstech.layanandesa.auth.viewmodel.ForgetPasswordViewModel
import com.localclasstech.layanandesa.databinding.ActivityForgetPasswordBinding

@SuppressLint("ClickableViewAccessibility")
class ForgetPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgetPasswordBinding
    private lateinit var viewModel: ForgetPasswordViewModel
    private var isPasswordVisible = false
    private var isConfirmPasswordVisible = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityForgetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val originalTypefacePassword = binding.etFpPassword.typeface
        val originalTypefaceConfirm = binding.etFpKonfirmasiPassword.typeface
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        viewModel = ForgetPasswordViewModel()

        // Input Listener (opsional, atau bisa langsung ambil saat tombol ditekan)
        binding.etFpEmail.addTextChangedListener {
            viewModel.onEmailChanged(it.toString())
        }

        binding.etFpKode.addTextChangedListener {
            viewModel.onVerificationCodeChanged(it.toString())
        }
        binding.etFpPassword.addTextChangedListener {
            viewModel.onPasswordChanged(it.toString())
        }

        binding.etFpKonfirmasiPassword.addTextChangedListener {
            viewModel.onConfirmPasswordChanged(it.toString())
        }


        // Handle eye icon visibility toggle for Password
        binding.etFpPassword.setOnTouchListener { v, event ->
            if (event.action == android.view.MotionEvent.ACTION_UP) {
                val drawableEnd = binding.etFpPassword.compoundDrawablesRelative[2]
                if (drawableEnd != null) {
                    val drawableWidth = drawableEnd.bounds.width()
                    val touchX = event.rawX.toInt()
                    val editTextRight = binding.etFpPassword.right
                    val clickArea = editTextRight - drawableWidth - binding.etFpPassword.paddingEnd
                    if (touchX >= clickArea) {
                        isPasswordVisible = !isPasswordVisible
                        if (isPasswordVisible) {
                            binding.etFpPassword.inputType = android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                            binding.etFpPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_show_pass, 0)
                        } else {
                            binding.etFpPassword.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
                            binding.etFpPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_eye_block, 0)
                        }
                        binding.etFpPassword.typeface = originalTypefacePassword
                        binding.etFpPassword.setSelection(binding.etFpPassword.text.length)
                        return@setOnTouchListener true
                    }
                }
            }
            false
        }

        // Handle eye icon visibility toggle for Confirm Password
        binding.etFpKonfirmasiPassword.setOnTouchListener { v, event ->
            if (event.action == android.view.MotionEvent.ACTION_UP) {
                val drawableEnd = binding.etFpKonfirmasiPassword.compoundDrawablesRelative[2]
                if (drawableEnd != null) {
                    val drawableWidth = drawableEnd.bounds.width()
                    val touchX = event.rawX.toInt()
                    val editTextRight = binding.etFpKonfirmasiPassword.right
                    val clickArea = editTextRight - drawableWidth - binding.etFpKonfirmasiPassword.paddingEnd
                    if (touchX >= clickArea) {
                        isConfirmPasswordVisible = !isConfirmPasswordVisible
                        if (isConfirmPasswordVisible) {
                            binding.etFpKonfirmasiPassword.inputType = android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                            binding.etFpKonfirmasiPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_show_pass, 0)
                        } else {
                            binding.etFpKonfirmasiPassword.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
                            binding.etFpKonfirmasiPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_eye_block, 0)
                        }
                        binding.etFpKonfirmasiPassword.typeface = originalTypefaceConfirm
                        binding.etFpKonfirmasiPassword.setSelection(binding.etFpKonfirmasiPassword.text.length)
                        return@setOnTouchListener true
                    }
                }
            }
            false
        }

        // Tombol Kembali
        binding.backButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        // Kirim Kode OTP
        binding.btnKirimKode.setOnClickListener {
            viewModel.sendVerificationCode()
        }

        // Reset Password
        binding.btnUbahPassword.setOnClickListener {
            viewModel.resetPassword()
        }


        // Observe hasil dari ViewModel
        observeViewModel()
    }

    private fun observeViewModel() {
//        viewModel.isLoading.observe(this) { isLoading ->
//            // bisa tambahkan progress bar
//            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
//        }
        viewModel.countdown.observe(this) { second ->
            if (second > 0) {
                binding.btnKirimKode.text = "$second"
                binding.btnKirimKode.isEnabled = false
            } else {
                binding.btnKirimKode.text = "Kirim Ulang"
                binding.btnKirimKode.isEnabled = true
            }
        }


        viewModel.errorMessage.observe(this) { errorMsg ->
            if (!errorMsg.isNullOrBlank()) {
                binding.tvNikError.text = errorMsg
                binding.tvNikError.visibility = View.VISIBLE
            } else {
                binding.tvNikError.visibility = View.INVISIBLE
            }
        }
        viewModel.isResettingPassword.observe(this) { isLoading ->
            if (isLoading) {
                binding.btnUbahPassword.isEnabled = false
                binding.btnUbahPassword.text = ""
                binding.progressBarBtn.visibility = View.VISIBLE
            } else {
                binding.btnUbahPassword.isEnabled = true
                binding.btnUbahPassword.text = "Ubah Password"
                binding.progressBarBtn.visibility = View.GONE
            }
        }


        viewModel.isCodeSent.observe(this) { sent ->
            if (sent) {
                Toast.makeText(this, "Kode verifikasi berhasil dikirim ke email", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.resetSuccess.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Password berhasil diubah", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }
}