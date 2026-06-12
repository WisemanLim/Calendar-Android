# STACK.md — WiseCalA

> `wise-dev-standard` / 프로파일 `android-compose` / 도메인 `ict-saas` 기준 생성 (2026-06-11)

## 결정 요약

| 항목 | 결정값 | 근거 |
|------|--------|------|
| 플랫폼 | Android (단독) | iOS WiseCal 포팅 목적. iOS/Android 동시 출시는 현 단계 불필요 |
| 언어 | Kotlin | Android 네이티브 표준 |
| UI | Jetpack Compose + Material 3 | 최신 Android UI 표준, iOS SwiftUI와 선언형 패러다임 일치 |
| 빌드 | Gradle Kotlin DSL + Version Catalog | libs.versions.toml 중앙 버전 관리 |
| 로컬 영속화 | DataStore Preferences (JSON) | iOS UserDefaults 대응. Room은 캘린더 데이터가 CalendarProvider에 있으므로 미사용 |
| 캘린더 | Android CalendarProvider (ContentResolver) | iOS EventKit 대응. 시스템 Google/로컬 캘린더 통합 |
| 날씨 | Open-Meteo API (Retrofit) | iOS WeatherKit 대체. 무료·무키·RFC 표준 WMO 코드 |
| 위치 | FusedLocationProviderClient (Play Services) | 날씨 위치 정보 취득 |
| 알림 | AlarmManager + NotificationManager | iOS UNUserNotificationCenter 대응 |
| 이미지 | Photo Picker API + Camera Intent | iOS PhotosPicker + UIImagePickerController 대응 |
| 의존성 관리 | Maven Central (AAR 직접) | CocoaPods/SwiftPM 없음. Gradle 의존성만 사용 |
| CI | GitHub Actions (ubuntu-latest) | Android 빌드는 macOS 러너 불필요 |
| 배포 | Fastlane supply → Play Store | iOS Fastlane과 동일 도구 |
| 최소 SDK | API 26 (Android 8.0) | iOS 17+ 대응. 시장 커버리지 95%+ |

## iOS → Android 핵심 매핑

| iOS (Swift) | Android (Kotlin) |
|-------------|-----------------|
| `EventKit / EKEvent` | `CalendarContract` ContentResolver |
| `UserDefaults` | `DataStore<Preferences>` |
| `WeatherKit` | Open-Meteo REST API |
| `UNUserNotificationCenter` | `NotificationManager` + `AlarmManager` |
| `PhotosPicker` | `ActivityResultContracts.PickVisualMedia` |
| `UIImagePickerController` | `ActivityResultContracts.TakePicture` |
| `EKRecurrenceRule` | RRULE 문자열 (`FREQ=YEARLY;INTERVAL=1`) |
| `@Published` / `ObservableObject` | `StateFlow` / `ViewModel` |
| `SF Symbols` | Material Icons Extended |
| `NavigationStack` | Navigation Compose |
| `.sheet` / `.fullScreenCover` | ModalBottomSheet / fullscreen Composable |
| 음력 `Calendar(identifier:.chinese)` | `ULocale("ko_KR@calendar=chinese")` |

## 프로파일 기본값과의 차이

- **데이터베이스**: 프로파일 기본값 Room(SQLite) → **미사용** (CalendarProvider가 데이터 소스)
- **DataStore**: 설정·템플릿 영속화만 사용 (이벤트는 CalendarProvider)
- **백엔드 없음**: 서버 없음, CalendarProvider + Open-Meteo만 외부 연결
