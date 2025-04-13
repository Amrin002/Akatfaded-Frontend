package com.localclasstech.layanandesa.network

import android.content.Context
import android.content.SharedPreferences

object ApiConfig {
    private const val PREFS_NAME = "ApiPrefs"
    private const val KEY_HOST = "host"
    private const val KEY_PORT = "port"

    // Default host dan port (bisa diubah sesuai kebutuhan)
    private const val DEFAULT_HOST = "192.168.56.1"
    private const val DEFAULT_PORT = "8000"

    fun getBaseUrl(context: Context): String {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        val host = sharedPreferences.getString(KEY_HOST, DEFAULT_HOST) ?: DEFAULT_HOST
        val port = sharedPreferences.getString(KEY_PORT, DEFAULT_PORT) ?: DEFAULT_PORT

        return "http://$host:$port/"
    }

    fun setBaseUrl(context: Context, host: String, port: String) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        sharedPreferences.edit()
            .putString(KEY_HOST, host)
            .putString(KEY_PORT, port)
            .apply()
    }
}