# WiseCalA

Jetpack Compose 기반 Android 네이티브 캘린더 앱 — iOS WiseCal Android 포팅.

> **스캐폴딩 정보**: `wise-dev-standard` 플러그인 / 프로파일 `android-compose` / 도메인 `ict-saas` 으로 생성됨.

## 기술 스택

| 구분 | 도구 |
|------|------|
| 언어 | Kotlin |
| UI 프레임워크 | Jetpack Compose + Material 3 |
| 빌드 시스템 | Gradle (Kotlin DSL) + Version Catalog |
| 최소 SDK | API 26 (Android 8.0) |
| 캘린더 데이터 | Android CalendarProvider (ContentResolver) |
| 영속화 | DataStore Preferences |
| 날씨 | Open-Meteo API (Retrofit) |
| 배포 자동화 | Fastlane (supply) |
| 배포 채널 | Play Store internal → production |
| CI/CD | GitHub Actions |

## 사전 요구사항

- Android Studio (최신 안정 버전) 또는 JDK 17+ + Android SDK Command-line Tools
- Ruby & Bundler (`gem install bundler`)
- Fastlane (`bundle install`)
- Google Play 개발자 계정 + 서비스 계정 JSON _(배포 시)_

```bash
bundle install
```

## 빠른 시작

```bash
# dev 플레이버 디버그 빌드 + 에뮬레이터 설치
make dev
```

Android Studio에서 열기: `File > Open > Wise-Calendar-A`

## Make 사용법

| 명령 | 설명 |
|------|------|
| `make dev` | Dev 플레이버 디버그 빌드 및 설치 (`./gradlew installDevDebug`) |
| `make test` | Dev 플레이버 단위 테스트 (`./gradlew testDevDebugUnitTest`) |
| `make build` | Prod 플레이버 릴리스 번들 생성 (`./gradlew bundleProdRelease`) |
| `make deploy` | Fastlane `beta` lane — Play Store internal 트랙 업로드 |

### Gradle 직접 사용

```bash
./gradlew tasks --all                         # 전체 태스크 목록
./gradlew installDevDebug                     # 에뮬레이터/기기에 설치
./gradlew testDevDebugUnitTest                # 단위 테스트
./gradlew connectedDevDebugAndroidTest        # 인스트루멘테이션 테스트
./gradlew bundleProdRelease                   # 릴리스 AAB
./gradlew lintDevDebug                        # 린트
```

## 빌드 플레이버 (환경)

| 플레이버 | 용도 | API |
|----------|------|-----|
| `dev` | 로컬 개발 / 에뮬레이터 | `http://10.0.2.2:8000` |
| `staging` | QA / 내부 테스트 | `https://stg.api.example` |
| `prod` | Play Store 제출 | `https://api.example` |

## 환경 변수

| 파일 | 용도 |
|------|------|
| `.env.local` | 로컬 개발 (에뮬레이터) |
| `.env.dev` | 공유 dev 서버 |
| `.env.staging` | Play 내부 테스트 |
| `.env.prod` | Play 프로덕션 |

> 키스토어·서비스 계정 JSON은 **절대 커밋 금지** — CI 시크릿으로 주입.

## 빌드 & 배포

### 릴리스 AAB

```bash
make build
# 결과물: app/build/outputs/bundle/prodRelease/app-prod-release.aab
```

### Play Store internal 배포

```bash
make deploy
# 또는:
cd fastlane && bundle exec fastlane android beta
```

### CI (GitHub Actions) Secrets 등록

- `KEYSTORE_BASE64` — 키스토어 Base64 인코딩
- `KEYSTORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD`
- `PLAY_STORE_JSON_KEY` — 서비스 계정 JSON

## 필요 권한 (AndroidManifest)

| 권한 | 용도 |
|------|------|
| `READ_CALENDAR` / `WRITE_CALENDAR` | CalendarProvider 이벤트 CRUD |
| `POST_NOTIFICATIONS` | 알림 (Android 13+) |
| `SCHEDULE_EXACT_ALARM` | 정확한 알람 (Android 12+) |
| `ACCESS_FINE_LOCATION` | 날씨 위치 |
| `CAMERA` | 복용 이벤트 사진 첨부 |
| `READ_MEDIA_IMAGES` | 갤러리 사진 (Android 13+) |
| `INTERNET` | Open-Meteo API |

## 디렉터리 구조

```
Wise-Calendar-A/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/wisecal/android/   # Kotlin 소스
│   │   │   ├── res/                         # 리소스
│   │   │   └── AndroidManifest.xml
│   │   ├── test/                            # 단위 테스트
│   │   └── androidTest/                     # 인스트루멘테이션 테스트
│   └── build.gradle.kts
├── gradle/
│   └── libs.versions.toml                   # Version Catalog
├── fastlane/
│   └── Fastfile                             # 배포 레인
├── .github/workflows/
│   └── ci.yml                               # CI 파이프라인
├── test/                                    # 시험 증빙 (시나리오/결과)
│   └── dev-env/
├── .docs/
│   └── PRD.md                               # Android PRD
├── .env.local / .env.dev / .env.staging / .env.prod
├── STACK.md                                 # 스택 결정 기록
├── build.gradle.kts
├── settings.gradle.kts
└── Makefile
```

## 빌드 문제 해결

### `make dev` — `./gradlew: No such file or directory`

Gradle wrapper 미생성. 최초 1회 실행:

```bash
gradle wrapper --gradle-version 8.10.2
```

### `JavaVersion.parse 25.0.3` 오류 (Kotlin 컴파일러)

시스템 JDK 25/26은 Kotlin 2.x 컴파일러가 버전 파싱을 지원하지 않음.
**Android Studio 내장 JBR 21 사용** — Makefile에 이미 설정됨:

```makefile
JAVA_HOME ?= /Applications/Android Studio.app/Contents/jbr/Contents/Home
```

Android Studio 미설치 시 대안:

```bash
brew install --cask temurin@17   # JDK 17 설치
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
make dev
```

### `SDK location not found`

`local.properties` 누락 (`.gitignore`에 포함됨). 직접 생성:

```bash
echo "sdk.dir=$HOME/Library/Android/sdk" > local.properties
```

### `Theme.AppCompat / Theme.Material3 not found`

`gradle.properties`에 `android.useAndroidX=true` 확인. 이미 설정되어 있어야 함.
없으면:

```bash
echo "android.useAndroidX=true" >> gradle.properties
```

---

## 구현 현황

| impl | 내용 | 상태 | 테스트 |
|------|------|------|--------|
| impl/1 | 순수 로직 (CalendarLogic · PlannerLogic · PlannerMode) | DONE | 26/26 PASS |
| impl/2 | AppSettings · EventTemplate · CalendarRepository | DONE | 26/26 PASS |
| impl/3 | WeatherInfo · CalendarUiState · CalendarViewModel + 메인 UI | DONE | 26/26 PASS |
| impl/4 | EventEditorState · EventEditorViewModel + 3모드 에디터 UI | DONE | 27/27 PASS |
| impl/5 | 온보딩 · 검색 · 설정 화면 | DONE | 26/26 PASS |
| impl/6 | 이벤트 복사 · Navigation Compose 전체 라우팅 | DONE | 12/12 PASS |
| impl/7 | TemplateState · WiseCalTheme(다크모드) · TemplateStore 연결 | DONE | 13/13 PASS |
| impl/8 | DiaryTimerState · CalendarFilterState · SettingsViewModel(캘린더소스) | DONE | 26/26 PASS |
| impl/9 | OverdueChecker(TODO 기한초과) · NotificationState(알림권한) | DONE | 24/24 PASS |
| impl/10 | DiaryTitleBuilder(이모지·기본제목) · WeekStripState(일~토 7셀·날씨매핑) | DONE | 28/28 PASS |
| impl/11 | EventImageStore(사진파일명 규칙) · MonthGridState(현재+다음월·이동경계) | DONE | 26/26 PASS |
| impl/12 | AppVersionState(온보딩노출조건) · YearNavigationState(연도이동·윤년경계) | DONE | 24/24 PASS |
| impl/13 | iOS 디자인 일치: 온보딩 화이트 리디자인 · 히어로 워터마크 수정 · WeekStrip SUN/SAT 색상 · MonthGrid 카드 래핑 · 탭 전환 수정 · 반복 기간 DatePickerDialog | DONE | — |

## 아키텍처

```
app/src/main/java/com/wisecal/android/
├── logic/
│   ├── CalendarLogic.kt       # monthGrid · dateKey (순수 함수)
│   ├── PlannerLogic.kt        # 투두 인코딩 · 날짜 패턴 치환 (순수 함수)
│   ├── OverdueChecker.kt      # TODO 기한 초과 판정 (순수 객체)
│   └── DiaryTitleBuilder.kt   # 이모지(이벤트 수) + 날짜 → 일기 기본 제목
├── model/
│   ├── PlannerMode.kt         # enum: DIARY · TODO · PERSONAL_EVENT
│   ├── AppColorScheme.kt      # enum: SYSTEM · DARK · LIGHT
│   ├── TemplateCategory.kt    # enum: MEDICATION · APPOINTMENT · FIXED
│   ├── EventTemplate.kt       # data class + 시드 9개
│   └── CalendarEvent.kt       # CalendarProvider 이벤트 매핑
├── data/
│   ├── AppSettings.kt         # DataStore Preferences (6개 키)
│   ├── TemplateStore.kt       # DataStore + Gson 직렬화
│   ├── CalendarRepository.kt  # CalendarProvider CRUD 인터페이스 + AndroidCalendarRepository
│   ├── EventImageStore.kt     # 사진 파일명(`event_{id}_{ts}.jpg`) 생성·파싱 규칙
│   ├── WeatherApiService.kt   # Retrofit Open-Meteo 인터페이스
│   └── WeatherRepository.kt   # 날씨 인터페이스 + OpenMeteoWeatherRepository
├── ui/
│   ├── CalendarUiState.kt     # UI 상태 (eventsForSelectedDate · monthGrid 파생)
│   ├── CalendarViewModel.kt   # StateFlow ViewModel
│   ├── MainScreen.kt          # 루트 컴포저블 (Hero + WeekStrip + MonthGrid + 목록)
│   ├── theme/
│   │   └── WiseCalColors.kt   # 레인보우 이벤트 수 색상 (7단계)
│   ├── components/
│   │   ├── HeroDateBar.kt     # 히어로 날짜 + 날씨
│   │   ├── WeekStrip.kt       # 주간 스트립 7칸
│   │   └── MonthGrid.kt       # 6×7 월 그리드
│   ├── editor/
│   │   ├── EventEditorState.kt   # 에디터 상태 (canSave · resolvedTitle · rrule)
│   │   ├── EventEditorViewModel.kt
│   │   ├── EventEditorScreen.kt
│   │   ├── DiaryEditorSection.kt
│   │   ├── TodoEditorSection.kt
│   │   └── PersonalEventEditorSection.kt
│   ├── onboarding/
│   │   ├── OnboardingState.kt    # 4단계 상태 (canProceed · nextStep · isLastStep)
│   │   ├── AppVersionState.kt    # 온보딩 노출 조건 (debug · 미완료 · 버전불일치)
│   │   └── OnboardingScreen.kt   # WELCOME→PERMISSION→PLANNER_MODE→COMPLETE
│   ├── search/
│   │   ├── SearchState.kt        # 검색 상태 (hasQuery ≥2자 · displayResults)
│   │   ├── SearchViewModel.kt    # 300ms debounce · Job 취소
│   │   └── SearchScreen.kt
│   ├── settings/
│   │   └── SettingsScreen.kt     # 플래너모드 · 테마 · 알림 설정
│   ├── copy/
│   │   ├── EventCopyState.kt     # 복사 상태 (resolvedTitle · resolvedNotes · resolvedTime)
│   │   ├── EventCopyViewModel.kt # save() → CalendarRepository.addEvent
│   │   └── EventCopySheet.kt     # ModalBottomSheet 복사 UI
│   ├── template/
│   │   └── TemplateState.kt      # 필터 상태 (filteredTemplates · categoryGroups · hasTemplates)
│   ├── notification/
│   │   └── NotificationState.kt  # 알림 권한+활성화 상태 (shouldRequestPermission · isActive)
│   ├── weekstrip/
│   │   └── WeekStripState.kt     # 주간 7셀 상태 (일~토 · WeatherInfo 매핑 · fallbackEmoji)
│   ├── monthgrid/
│   │   └── MonthGridState.kt     # 미니 달력 현재+다음 월 상태 (goBack/goForward 경계)
│   ├── theme/
│   │   ├── WiseCalColors.kt      # 레인보우 이벤트 수 색상 (7단계)
│   │   └── WiseCalTheme.kt       # AppColorScheme → DarkTheme/LightTheme 적용
│   └── navigation/
│       ├── NavRoute.kt              # sealed class: Main/Onboarding/Search/Settings/Editor
│       ├── YearNavigationState.kt   # `< 연도 >` 이동 상태, 윤년 2월29일 경계 보정
│       └── WiseCalNavGraph.kt       # NavHost 전체 라우팅 + 온보딩 분기 + TemplateStore
└── MainActivity.kt               # WiseCalTheme + WiseCalNavGraph 진입점
```

## 관련 문서

- [.docs/PRD.md](.docs/PRD.md) — Android WiseCal PRD
- [STACK.md](STACK.md) — 스택 결정 근거
- [test/impl/1/result.md](test/impl/1/result.md) — impl/1 시험 결과
- [test/impl/2/result.md](test/impl/2/result.md) — impl/2 시험 결과
- [test/impl/3/result.md](test/impl/3/result.md) — impl/3 시험 결과
- [test/impl/4/result.md](test/impl/4/result.md) — impl/4 시험 결과
- [test/impl/5/result.md](test/impl/5/result.md) — impl/5 시험 결과
- [test/impl/6/result.md](test/impl/6/result.md) — impl/6 시험 결과
- [test/impl/7/result.md](test/impl/7/result.md) — impl/7 시험 결과
- [test/impl/8/result.md](test/impl/8/result.md) — impl/8 시험 결과
- iOS 원본: `../Wise-Calendar/`
