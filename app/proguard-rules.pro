# WiseCalA R8/ProGuard 규칙
# 릴리스(minify) 빌드에서 Retrofit/Gson 제네릭 시그니처 제거로 인한
# ClassCastException(앱 즉시 종료) 방지.

# ── 제네릭/애너테이션 메타데이터 보존 (Retrofit·Gson 필수) ──
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes RuntimeVisibleAnnotations
-keepattributes RuntimeVisibleParameterAnnotations
-keepattributes EnclosingMethod
-keepattributes InnerClasses

# ── Retrofit ──────────────────────────────────────────────
# 서비스 인터페이스(제네릭 반환형·suspend Continuation) 보존
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>
-keepclasseswithmembers,includedescriptorclasses interface * { @retrofit2.http.* <methods>; }
# 앱 API 인터페이스 직접 보존
-keep interface com.wisecal.android.data.WeatherApiService { *; }
-keep interface com.wisecal.android.data.WeatherForecastService { *; }

# ── Gson (DTO 필드명 반사 직렬화) ─────────────────────────
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
# 날씨 응답·템플릿 DTO 보존
-keep class com.wisecal.android.data.WeatherResponse { *; }
-keep class com.wisecal.android.data.CurrentWeatherDto { *; }
-keep class com.wisecal.android.data.DailyForecastResponse { *; }
-keep class com.wisecal.android.data.DailyDto { *; }
-keep class com.wisecal.android.data.EventTemplateDto { *; }

# ── OkHttp / Okio ─────────────────────────────────────────
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn org.conscrypt.**
-dontwarn javax.annotation.**

# ── Coil ──────────────────────────────────────────────────
-dontwarn coil.**
