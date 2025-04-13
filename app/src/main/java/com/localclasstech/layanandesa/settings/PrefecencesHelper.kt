package com.localclasstech.layanandesa.settings

import android.content.Context
import android.content.SharedPreferences
import com.localclasstech.layanandesa.network.User

class PreferencesHelper(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
    private val sharedPreferencesLogin = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    // Untuk mengambil preferensi tema
    fun getThemePreference(): Boolean {
        return sharedPreferences.getBoolean("isModernTheme", true)
    }

    // Untuk menyimpan preferensi tema
    fun saveThemePreference(isModern: Boolean) {
        sharedPreferences.edit().putBoolean("isModernTheme", isModern).apply()
    }

    // Untuk menghapus preferensi tema
    fun clearPreferences() {
        sharedPreferences.edit().clear().apply()
    }

    // Fungsi untuk menghapus data login
    fun clearLogin() {
        sharedPreferencesLogin.edit().remove("login_mode").apply()
    }

    // Untuk mendapatkan status login
    fun getLoginMode(): String? {
        return sharedPreferencesLogin.getString("login_mode", null)
    }
    fun saveUser(user: User) {
        val editor = sharedPreferencesLogin.edit()
        editor.putInt("user_id", user.id)
        editor.putString("user_name", user.name)
        editor.putString("user_email", user.email)
        editor.putString("user_nik", user.nik)
        editor.putString("user_no_telp", user.no_telp)
        editor.putString("user_image", user.image)
        editor.apply()
    }
    fun saveToken(token: String) {
        sharedPreferencesLogin.edit().putString("auth_token", token).apply()
    }

    fun getToken(): String? {
        return sharedPreferencesLogin.getString("auth_token", null)
    }



}
