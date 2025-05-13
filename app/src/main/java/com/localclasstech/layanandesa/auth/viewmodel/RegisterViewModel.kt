package com.localclasstech.layanandesa.auth.viewmodel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Callback
import com.localclasstech.layanandesa.network.RegisterRequest
import com.localclasstech.layanandesa.network.RegisterResponse
import com.localclasstech.layanandesa.network.RetrofitClient
import retrofit2.Call
import retrofit2.Response

class RegisterViewModel(private val context: Context) : ViewModel() {
    //

    private val _registerResult = MutableLiveData<RegisterResponse?>()
    val registerResult: LiveData<RegisterResponse?> = _registerResult

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _registrationSuccess = MutableLiveData<Boolean>()
    val registrationSuccess: LiveData<Boolean> = _registrationSuccess


    fun registerUser(
        name: String,
        email: String,
        nik: String,
        noTelp: String,
        password: String,
        confirmPassword: String
    ) {
        // Validasi input
        when {
            name.isBlank() || email.isBlank() || nik.isBlank() || noTelp.isBlank() || password.isBlank() || confirmPassword.isBlank() -> {
                _errorMessage.value = "Semua field harus diisi"
                return
            }

            password != confirmPassword -> {
                _errorMessage.value = "Password dan konfirmasi tidak cocok"
                return
            }

            password.length < 8 -> {
                _errorMessage.value = "Password minimal 8 karakter"
                return
            }
        }

        // Request ke API
        val request = RegisterRequest(
            name = name,
            email = email,
            nik = nik,
            no_telp = noTelp,
            password = password
        )
        _isLoading.value = true

        RetrofitClient.clientService.register(request).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                _isLoading.value = false // sembunyikan loading

                if (response.isSuccessful) {
                    _registerResult.value = response.body()
                    _errorMessage.value = null // bersihkan error
                    _registrationSuccess.value = true
                    Toast.makeText(context, "Berhasil Register!", Toast.LENGTH_SHORT).show()
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("RegisterViewModel", "Gagal: $errorBody")
                    _registrationSuccess.value = false

                    // Cek apakah error-nya tentang NIK
                    if (errorBody?.contains("NIK") == true || errorBody?.contains("nik") == true) { //Only safe (?.) or non-null asserted (!!.) calls are allowed on a nullable receiver of type String?
                        _errorMessage.value = "Maaf NIK Anda belum terdaftar di desa ini"
                    } else {
                        _errorMessage.value = "Register gagal. Cek kembali data Anda."
                    }
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                _isLoading.value = false // sembunyikan loading
                _registrationSuccess.value = false
                Log.e("RegisterViewModel", "Error: ${t.message}", t)
                _errorMessage.value = "Terjadi Kesalahan. Silahkan coba lagi."
            }
        })
    }




}