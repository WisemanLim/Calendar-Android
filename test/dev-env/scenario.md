# dev-env 시험 시나리오 — WiseCalA (android-compose)

## 목적
Android 빌드 환경 기본 동작 확인.

## 환경
- OS: macOS (Darwin 25.x, Apple Silicon)
- 시스템 JDK: OpenJDK 25.0.3 / 26.0.1 (Kotlin 컴파일러 미지원)
- 빌드 JDK: Android Studio JBR 21.0.10 (JAVA_HOME override)
- Android SDK: ~/Library/Android/sdk (API 35 compileSdk)
- Gradle: 8.10.2 (wrapper)
- AGP: 8.7.0 / Kotlin: 2.2.0

## 시나리오

| # | 검증 항목 | 명령 | 합격 기준 |
|---|-----------|------|-----------|
| 1 | Gradle wrapper 존재 | `ls gradlew` | 파일 있음 |
| 2 | JDK 21 (AS JBR) 사용 | `JAVA_HOME=".../jbr/..." java -version` | `21.x` |
| 3 | SDK 인식 | `local.properties` 존재 + `sdk.dir` 설정 | 파일 있음 |
| 4 | devDebug 빌드 성공 | `./gradlew assembleDevDebug` | `BUILD SUCCESSFUL` + APK 생성 |
| 5 | 단위 테스트 실행 가능 | `./gradlew testDevDebugUnitTest` | `BUILD SUCCESSFUL` (테스트 0개 이상) |
