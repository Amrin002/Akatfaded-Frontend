package com.localclasstech.layanandesa.auth.view

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

class ForgetPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgetPasswordBinding
    private lateinit var viewModel: ForgetPasswordViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityForgetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
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