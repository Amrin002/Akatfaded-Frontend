package com.localclasstech.layanandesa.auth.viewmodel

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.localclasstech.layanandesa.network.ResetPasswordOtpRequest
import com.localclasstech.layanandesa.network.ResetPasswordOtpResponse
import com.localclasstech.layanandesa.network.RetrofitClient
import com.localclasstech.layanandesa.network.SendOtpRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgetPasswordViewModel : ViewModel() {

    companion object {
        private const val TAG = "ForgetPasswordVM"
    }

    private val _email = MutableLiveData<String>()
    val email: LiveData<String> get() = _email

    private val _verificationCode = MutableLiveData<String>()
    val verificationCode: LiveData<String> get() = _verificationCode

    private val _password = MutableLiveData<String>()
    val password: LiveData<String> get() = _password

    private val _confirmPassword = MutableLiveData<String>()
    val confirmPassword: LiveData<String> get() = _confirmPassword

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _isResettingPassword = MutableLiveData<Boolean>()
    val isResettingPassword: LiveData<Boolean> get() = _isResettingPassword

    private val _countdown = MutableLiveData<Int>()
    val countdown: LiveData<Int> get() = _countdown


    private val _isCodeSent = MutableLiveData<Boolean>()
    val isCodeSent: LiveData<Boolean> get() = _isCodeSent

    private val _resetSuccess = MutableLiveData<Boolean>()
    val resetSuccess: LiveData<Boolean> get() = _resetSuccess

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun onEmailChanged(value: String) {
        _email.value = value
        Log.d(TAG, "Email diubah: $value")
    }

    fun onVerificationCodeChanged(value: String) {
        _verificationCode.value = value
        Log.d(TAG, "Kode OTP diubah: $value")
    }

    fun onPasswordChanged(value: String) {
        _password.value = value
        Log.d(TAG, "Password diubah")
    }

    fun onConfirmPasswordChanged(value: String) {
        _confirmPassword.value = value
        Log.d(TAG, "Konfirmasi Password diubah")
    }
    private var countdownTimer: CountDownTimer? = null
    private fun startCountdown() {
        _isCodeSent.value = false
        _countdown.value = 30 // mulai dari 30 detik
        countdownTimer?.cancel()
        countdownTimer = object : CountDownTimer(30_000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                _countdown.value = (millisUntilFinished / 1000).toInt()
            }

            override fun onFinish() {
                _isCodeSent.value = true // enable button & ubah tulisan jadi "Kirim Ulang"
                _countdown.value = 0
            }
        }
        countdownTimer?.start()
    }

    fun sendVerificationCode() {
        val emailValue = _email.value.orEmpty()
        if (emailValue.isBlank()) {
            _errorMessage.value = "Email tidak boleh kosong"
            Log.e(TAG, "Email kosong saat kirim OTP")
            return
        }
        startCountdown()

        _isLoading.value = true
        Log.d(TAG, "Mengirim OTP ke $emailValue")

        val request = SendOtpRequest(email = emailValue)

        RetrofitClient.clientService.sendOtp(request)
            .enqueue(object : Callback<ResetPasswordOtpResponse> {
                override fun onResponse(
                    call: Call<ResetPasswordOtpResponse>,
                    response: Response<ResetPasswordOtpResponse>
                ) {
                    _isLoading.value = false
                    if (response.isSuccessful) {
                        _isCodeSent.value = true
                        Log.d(TAG, "OTP berhasil dikirim")

                    } else {
                        val msg = response.message()
                        _errorMessage.value = msg
                        Log.e(TAG, "Gagal kirim OTP: $msg")
                    }
                }

                override fun onFailure(call: Call<ResetPasswordOtpResponse>, t: Throwable) {
                    _isLoading.value = false
                    val msg = "Gagal mengirim OTP: ${t.localizedMessage}"
                    _errorMessage.value = msg
                    Log.e(TAG, msg)
                }
            })


    }


    fun resetPassword() {
        val emailValue = _email.value.orEmpty()
        val otpValue = _verificationCode.value.orEmpty()
        val passValue = _password.value.orEmpty()
        val confirmValue = _confirmPassword.value.orEmpty()

        // Menambahkan log untuk memeriksa nilai input sebelum validasi
        Log.d(TAG, "Email: $emailValue, OTP: $otpValue, Password: $passValue, Confirm Password: $confirmValue")

        if (emailValue.isBlank() || otpValue.isBlank() || passValue.isBlank() || confirmValue.isBlank()) {
            _errorMessage.value = "Semua field harus diisi"
            Log.e(TAG, "Form reset kosong")
            return
        }

        if (passValue != confirmValue) {
            _errorMessage.value = "Password tidak sama"
            Log.e(TAG, "Password dan konfirmasi tidak sama")
            return
        }

        _isResettingPassword.value = true
        Log.d(TAG, "Mengirim permintaan reset password untuk: $emailValue")

        // Proses request untuk reset password
        val request = ResetPasswordOtpRequest(
            email = emailValue,
            otp = otpValue,
            password = passValue,
            password_confirmation = confirmValue
        )

        RetrofitClient.clientService.resetPasswordWithOtp(request)
            .enqueue(object : Callback<ResetPasswordOtpResponse> {
                override fun onResponse(
                    call: Call<ResetPasswordOtpResponse>,
                    response: Response<ResetPasswordOtpResponse>
                ) {
                    _isResettingPassword.value = false
                    if (response.isSuccessful) {
                        _resetSuccess.value = true
                        Log.d(TAG, "Password berhasil direset")
                    } else {
                        val msg = response.message()
                        _errorMessage.value = msg
                        Log.e(TAG, "Gagal reset password: $msg")
                    }
                }

                override fun onFailure(call: Call<ResetPasswordOtpResponse>, t: Throwable) {
                    _isResettingPassword.value = false
                    val msg = "Gagal reset password: ${t.localizedMessage}"
                    _errorMessage.value = msg
                    Log.e(TAG, msg)
                }
            })
    }

}
