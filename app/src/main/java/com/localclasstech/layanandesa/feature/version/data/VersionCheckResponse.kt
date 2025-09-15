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
    // Sesuaikan dengan field yang BENAR-BENAR dikirim backend di Postman
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

    @SerializedName("changelog")
    val changelog: List<String> = emptyList(),

    @SerializedName("file_size")
    val fileSize: String = "",

    @SerializedName("release_date")
    val releaseDate: String = "",

    @SerializedName("platform")
    val platform: String = "android"
) {
    // Computed properties untuk kompatibilitas
    val needsUpdate: Boolean
        get() = true // Atau logika sesuai requirement

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