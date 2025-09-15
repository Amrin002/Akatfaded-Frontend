#########################################
# ✅ ViewModel dan LiveData
#########################################
-keep class androidx.lifecycle.** { *; }
-keepclassmembers class * {
    @androidx.lifecycle.* <methods>;
}

#########################################
# ✅ ViewModelFactory - PENTING!
#########################################
-keep class **ViewModelFactory { *; }
-keep class **Factory { *; }
-keepclassmembers class **ViewModelFactory {
    <init>(...);
}

#########################################
# ✅ SEMUA KELAS APLIKASI - FIX UTAMA!
#########################################
-keep class com.localclasstech.layanandesa.** { *; }
-keepclassmembers class com.localclasstech.layanandesa.** {
    <fields>;
    <methods>;
    <init>(...);
}

#########################################
# ✅ REPOSITORY & CALLBACK - CRITICAL FIX!
#########################################
-keep class **Repository { *; }
-keep class **Repository$* { *; }
-keepclassmembers class **Repository {
    <fields>;
    <methods>;
}
-keepclassmembers class **Repository$* {
    <fields>;
    <methods>;
}

# Keep callback methods
-keep interface retrofit2.Callback { *; }
-keep class * implements retrofit2.Callback {
    <methods>;
}

#########################################
# ✅ Retrofit + Gson - ENHANCED
#########################################
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes RuntimeVisibleAnnotations
-keepattributes RuntimeInvisibleAnnotations
-keepattributes RuntimeVisibleParameterAnnotations
-keepattributes RuntimeInvisibleParameterAnnotations
-keepattributes EnclosingMethod
-keepattributes InnerClasses

-keep class com.google.gson.** { *; }
-keep class retrofit2.** { *; }
-keep class okhttp3.** { *; }
-dontwarn retrofit2.**
-dontwarn okhttp3.**

# Gson specific rules
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Retrofit specific rules
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

# Keep semua model network agar tidak dihapus ProGuard
-keep class com.localclasstech.layanandesa.network.** { *; }
-keepclassmembers class com.localclasstech.layanandesa.network.** {
    <fields>;
    <methods>;
}

#########################################
# ✅ DATA MODELS & RESPONSE - CRITICAL!
#########################################
-keep class **Response { *; }
-keep class **Response$* { *; }
-keep class **Data { *; }
-keep class **Model { *; }
-keep class **Entity { *; }
-keepclassmembers class **Response {
    <fields>;
    <methods>;
}
-keepclassmembers class **Data {
    <fields>;
    <methods>;
}
-keepclassmembers class **Model {
    <fields>;
    <methods>;
}

#########################################
# ✅ Kotlin Coroutines
#########################################
-dontwarn kotlinx.coroutines.**
-keep class kotlinx.coroutines.** { *; }

#########################################
# ✅ Fragment dan Activity
#########################################
-keep public class * extends androidx.fragment.app.Fragment
-keep public class * extends androidx.appcompat.app.AppCompatActivity
-keepclassmembers class * extends androidx.fragment.app.Fragment {
    public <init>(...);
    <methods>;
}

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

#########################################
# ✅ RecyclerView Adapter
#########################################
-keep class **Adapter { *; }
-keep class **AdapterHelper { *; }
-keepclassmembers class **Adapter {
    <init>(...);
    <methods>;
}
-keepclassmembers class **AdapterHelper {
    <init>(...);
    <methods>;
}

#########################################
# ✅ Preferences dan SharedPreferences
#########################################
-keep class **PreferencesHelper { *; }
-keepclassmembers class **PreferencesHelper {
    <init>(...);
    <fields>;
    <methods>;
}

#########################################
# ✅ Data binding dan View binding
#########################################
-keep class **Binding { *; }
-keep class **DataBinding { *; }

#########################################
# ✅ Parcelize
#########################################
-keepclassmembers class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

#########################################
# ✅ Lambda dan Anonymous Classes
#########################################
-keepclassmembernames class * {
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
-keepclassmembers class * {
    synthetic <methods>;
}

# Keep lambda methods
-keepclassmembers class * {
    *** lambda$*(...);
}

#########################################
# ✅ Inner Classes dan Nested Classes
#########################################
-keepattributes InnerClasses
-keep class **.R
-keep class **.R$* {
    <fields>;
}

#############################################################
# ProGuard keep rules for optional SSL libraries (OkHttp)
#############################################################
-dontwarn org.conscrypt.**
-dontwarn org.openjsse.**

#########################################
# ✅ Debugging - jangan hapus stacktrace
#########################################
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

#########################################
# ✅ SPECIFIC FIX untuk ClassCastException
#########################################
# Keep semua yang berhubungan dengan casting
-keep class * extends java.lang.Object {
    <methods>;
}

# Keep enum values
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-ignorewarnings