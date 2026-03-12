# ── Revest Catalog ProGuard Rules ─────────────────────────────────────────────

# Keep Kotlin Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** { *** Companion; }
-keepclasseswithmembers class kotlinx.serialization.** { kotlinx.serialization.KSerializer serializer(...); }
-keep,includedescriptorclasses class com.revest.**$$serializer { *; }
-keepclassmembers class com.revest.** {
    *** Companion;
}
-keepclasseswithmembers class com.revest.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep domain models (used in serialization)
-keep class com.revest.domain.model.** { *; }
-keep class com.revest.data.remote.**Dto { *; }

# Ktor
-keep class io.ktor.** { *; }
-dontwarn io.ktor.**

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# Koin
-keep class org.koin.** { *; }

# Coil
-keep class coil3.** { *; }
