plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-parcelize")
}

android {
    namespace = "com.localclasstech.layanandesa"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.localclasstech.layanandesa"
        minSdk = 26
        targetSdk = 34
        versionCode = 5
        versionName = "1.5"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "BASE_URL", "\"https://akatfadedo.com/\"")
    }

    buildTypes {
        debug {
//            isMinifyEnabled = true
            buildConfigField("String", "BASE_URL", "\"https://akatfadedo.com/\"")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true

            // Untuk debugging ProGuard issues
            isDebuggable = false


            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "BASE_URL", "\"https://akatfadedo.com/\"")

            // Untuk menjaga stack trace yang berguna saat crash
            packagingOptions {
                resources {
                    excludes += "/META-INF/{AL2.0,LGPL2.1}"
                }
            }
        }
        // Tambahkan build type baru untuk testing
        create("releaseTest") {
            initWith(getByName("release"))
            isMinifyEnabled = false     // Tanpa obfuscation
            isShrinkResources = false   // Tanpa resource shrinking
            isDebuggable = true         // Bisa di-debug
            applicationIdSuffix = ".test"  // Package name berbeda
            versionNameSuffix = "-test"
            signingConfig = signingConfigs.getByName("debug")// Version name berbeda
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.androidx.fragment.ktx)
    implementation (libs.androidx.navigation.fragment.ktx)
    implementation( libs.androidx.navigation.ui.ktx)

//    implementation(libs.firebase.appdistribution.gradle)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //MVVM

    //AndroidX Livecycle
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)

    // Kotlin Coroutines
    implementation(libs.kotlinx.coroutines.android)

    //Viewmodel dan Live data
    implementation(libs.androidx.lifecycle.runtime.ktx)
    // Retrofit (REST API)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation (libs.converter.scalars)

    // OkHttp (Logging Interceptor untuk debug)
    implementation(libs.logging.interceptor)

    //glide
    implementation(libs.glide)
    annotationProcessor(libs.compiler)
    implementation(libs.photoview)

//    Loading Skleton Shimer fb
    implementation(libs.shimmer)

    //debuging
    testImplementation("com.squareup.okhttp3:mockwebserver:4.9.2")
    testImplementation ("org.junit.jupiter:junit-jupiter-api:5.7.0")
    testImplementation ("org.junit.jupiter:junit-jupiter-engine:5.7.0")
//    implementation(libs.html.textview)

}