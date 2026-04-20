# Keep source file names + line numbers in stack traces (rename to obscure src paths)
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# --- kotlinx.serialization ---
# Keep @Serializable classes' members so plugin-generated serializers resolve correctly.
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keep,includedescriptorclasses class **$$serializer { *; }
-keepclassmembers class * {
    *** Companion;
}
-keepclasseswithmembers class * {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep all DTO/model classes in the feature packages (they're @Serializable).
-keep class org.example.fakeshop_clients.**.data.models.** { *; }
-keep class org.example.fakeshop_clients.**.domain.models.** { *; }
-keep class org.example.fakeshop_clients.core.auth.data.models.** { *; }

# --- Kotlin metadata (reflection-free usage is the default, but safe to keep) ---
-keep class kotlin.Metadata { *; }

# --- Coroutines ---
# ServiceLoader support (MainDispatcherFactory etc.)
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
