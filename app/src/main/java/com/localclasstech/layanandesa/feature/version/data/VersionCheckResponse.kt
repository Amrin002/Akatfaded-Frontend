package com.localclasstech.layanandesa.feature.version.data

import com.google.gson.annotations.SerializedName

data class VersionCheckResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: VersionData?
)

data class VersionData(
    @SerializedName("version")
    val latestVersion: String = "",

    @SerializedName("version_code")
    val latestVersionCode: Int = 0,

    @SerializedName("minimum_version")
    val minimumVersion: String = "",

    @SerializedName("minimum_version_code")
    val minimumVersionCode: Int = 0,

    @SerializedName("download_url")
    val downloadUrl: String = "",

    @SerializedName("is_force_update")
    val isForceUpdate: Boolean = false,

    // SOLUTION: Handle object as changelog
    @SerializedName("changelog")
    private val _changelog_raw: com.google.gson.JsonElement? = null,

    @SerializedName("file_size")
    val fileSize: String = "",

    @SerializedName("release_date")
    val releaseDate: String = "",

    @SerializedName("platform")
    val platform: String = "android"
) {
    // Parse changelog object ke List<String>
    val changelog: List<String>
        get() = try {
            when {
                _changelog_raw == null || _changelog_raw.isJsonNull -> {
                    listOf("Perbaikan dan peningkatan performa")
                }
                _changelog_raw.isJsonArray -> {
                    // Jika array (format lama)
                    com.google.gson.Gson().fromJson(_changelog_raw, Array<String>::class.java).toList()
                }
                _changelog_raw.isJsonObject -> {
                    // Jika object (format baru dari backend)
                    val changelogObject = _changelog_raw.asJsonObject
                    val sortedKeys = changelogObject.keySet()
                        .mapNotNull { it.toIntOrNull() }
                        .sorted() // Sort berdasarkan key numerik

                    sortedKeys.map { key ->
                        changelogObject.get(key.toString()).asString
                    }.filter { it.isNotBlank() }
                }
                _changelog_raw.isJsonPrimitive -> {
                    // Jika string tunggal
                    listOf(_changelog_raw.asString)
                }
                else -> {
                    listOf("Update tersedia dengan fitur baru")
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("VersionData", "Error parsing changelog: ${e.message}")
            listOf("Perbaikan dan peningkatan performa")
        }

    // Rest of computed properties
    val needsUpdate: Boolean
        get() = true

    val currentVersion: String
        get() = com.localclasstech.layanandesa.BuildConfig.VERSION_NAME

    val isLatest: Boolean
        get() = !needsUpdate
}

data class VersionCheckRequest(
    @SerializedName("current_version")
    val currentVersion: String,

    @SerializedName("current_version_code")
    val currentVersionCode: Int,

    @SerializedName("platform")
    val platform: String = "android"
)