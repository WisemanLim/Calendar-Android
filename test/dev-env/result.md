# dev-env 시험 결과 — WiseCalA (android-compose)

**판정: PASS**  
시험일: 2026-06-11

## 결과

| # | 항목 | 결과 | 비고 |
|---|------|------|------|
| 1 | Gradle wrapper 존재 | PASS | `gradlew` + `gradlew.bat` 생성됨 |
| 2 | JDK 21 (AS JBR) 사용 | PASS | `JAVA_HOME=/Applications/Android Studio.app/Contents/jbr/Contents/Home` (21.0.10) |
| 3 | SDK 인식 | PASS | `local.properties` sdk.dir=~/Library/Android/sdk |
| 4 | devDebug 빌드 성공 | PASS | `app/build/outputs/apk/dev/debug/app-dev-debug.apk` 생성 |
| 5 | 단위 테스트 실행 가능 | PASS | `BUILD SUCCESSFUL` |

## 해결한 이슈

| 이슈 | 원인 | 조치 |
|------|------|------|
| `gradlew not found` | wrapper 미생성 | `gradle wrapper --gradle-version 8.10.2` |
| `JavaVersion.parse 25.0.3` | Kotlin 2.2.0이 JDK 25 미지원 | Android Studio JBR 21 (`JAVA_HOME` override, Makefile 영속화) |
| `SDK location not found` | `local.properties` 누락 | `sdk.dir=...` 생성 |
| `android.useAndroidX not set` | `gradle.properties` 누락 | `android.useAndroidX=true` 추가 |
| `ic_launcher/app_name not found` | `res/` 리소스 누락 | `strings.xml`, `themes.xml`, adaptive icon 추가 |
| `Theme.AppCompat not found` | `appcompat` 미포함 | `libs.versions.toml` + `build.gradle.kts` 추가 |
