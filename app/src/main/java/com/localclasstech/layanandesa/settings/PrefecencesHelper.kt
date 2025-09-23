package com.localclasstech.layanandesa.settings

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.localclasstech.layanandesa.network.User

class PreferencesHelper(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
    private val sharedPreferencesLogin = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    // Tambahkan listener untuk mendeteksi perubahan
    private val loginPreferencesListener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
        when (key) {
            "user_name", "login_mode" -> {
                Log.d("PreferencesHelper", "Login mode or username updated: $key")
            }
        }
    }

    init {
        // Daftarkan listener
        sharedPreferencesLogin.registerOnSharedPreferenceChangeListener(loginPreferencesListener)
    }

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


    fun edit(block: SharedPreferences.Editor.() -> Unit) {
        val editor = sharedPreferencesLogin.edit()
        block(editor)
        editor.apply()
    }

    fun updateUserProfile(name: String?, image: String?) {
        val editor = sharedPreferencesLogin.edit()

        // Gunakan nilai default jika null
        name?.let {
            editor.putString("user_name", it)
            editor.putString("login_mode", it)
        }
        image?.let {
            editor.putString("user_profile_image", it)
        }

        Log.d("PreferencesHelper", "Updating profile: name=$name, image=$image")
        editor.apply()
    }
    fun getUserId(): Int? {
        return sharedPreferences.getInt("user_id", -1).takeIf { it != -1 }
    }

    // Method tambahan untuk memastikan konsistensi
    fun syncUserProfile(name: String, image: String?) {
        val editor = sharedPreferencesLogin.edit()
        editor.putString("user_name", name)
        editor.putString("login_mode", name)
        editor.putString("user_profile_image", image ?: "")
        editor.apply()

        Log.d("PreferencesHelper", "Syncing profile: name=$name, image=$image")
    }

    // Tambahkan method untuk membersihkan listener jika diperlukan
    fun cleanup() {
        sharedPreferencesLogin.unregisterOnSharedPreferenceChangeListener(loginPreferencesListener)
    }

    fun getToken(): String? {
        return sharedPreferencesLogin.getString("auth_token", null)
    }
}
