#########################################
# ✅ ViewModel dan LiveData
#########################################
-keep class androidx.lifecycle.** { *; }
-keepclassmembers class * {
    @androidx.lifecycle.* <methods>;
}

#########################################
# ✅ Retrofit + Gson
#########################################
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.google.gson.** { *; }
-keep class retrofit2.** { *; }
-keep class okhttp3.** { *; }
-dontwarn retrofit2.**

#########################################
# ✅ Kotlin Coroutines
#########################################
-dontwarn kotlinx.coroutines.**
-keep class kotlinx.coroutines.** { *; }

#########################################
# ✅ Glide
#########################################
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}
-dontwarn com.bumptech.glide.**

#########################################
# ✅ PhotoView
#########################################
-keep class uk.co.senab.photoview.** { *; }

#########################################
# ✅ Facebook Shimmer
#########################################
-keep class com.facebook.shimmer.** { *; }
-dontwarn com.facebook.shimmer.**

#############################################################
# ProGuard keep rules for optional SSL libraries (OkHttp)
# Supaya tidak error karena kelas opsional yang tidak dipakai
#############################################################
-dontwarn org.conscrypt.**
-dontwarn org.openjsse.**


-ignorewarnings