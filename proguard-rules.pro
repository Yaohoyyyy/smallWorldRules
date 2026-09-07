# R8 full mode: keep line numbers for stack traces in debug-style output
-keepattributes SourceFile,LineNumberTable

# Compose: keep @Composable functions' metadata (Compose ships consumer rules,
# but these help keep things consistent).
-keep class androidx.compose.runtime.** { *; }

# For native/reflection-based services that might be stripped.
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
