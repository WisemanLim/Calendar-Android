# PRD — WiseCal Android (위클리 플래너 Android 앱)

> One-Page PRD · 애자일 · 작성일 2026-06-11 · 도메인: ict-saas (소비자 Android 앱)
> iOS 버전(`Wise-Calendar`) 기준 Android 포팅. 모르는 값은 `가정:` 으로 표시.

---

## 1. Why — 목표 / 문제

iOS WiseCal 앱(`Wise-Calendar`)의 **핵심 UX를 Android에서 동일하게 제공**한다.
큰 날짜 히어로 + 스와이프 + 주간 스트립 + 다개월 그리드로 일정을 빠르게 조회하고,
**세 가지 플래너 모드(일기·할일·개인이벤트)** 로 이벤트를 추가하는 미니멀 위클리 플래너.
Android 기기의 시스템 캘린더(Google Calendar 포함)를 읽고 쓰는 것이 목표.
가능하면 **Google Play Store 출시**.

---

## 2. Who — 사용자

- 주 사용자: 개인 일정을 관리하는 **일반 Android 사용자**.
- 가정: Android 8.0(API 26)+ 단일 디바이스(스마트폰). 한국어 우선, 영어 지원.
- iOS 사용자와 동일한 UX 기대값 — 플래너 모드·템플릿·온보딩 동일 적용.

---

## 3. What — 핵심 기능

### 3.0 iOS → Android 기술 매핑

| iOS | Android |
|-----|---------|
| SwiftUI | Jetpack Compose |
| EventKit (EKEvent) | Android CalendarProvider (ContentResolver) |
| UserDefaults | DataStore Preferences (또는 SharedPreferences) |
| WeatherKit + Open-Meteo | Open-Meteo API 전용 (WeatherKit 없음). WMO 코드 → iOS WeatherCondition 기준 이모지 매핑: 코드 1→☀️(clear), 2→⛅(partlyCloudy), else→☀️(iOS default) |
| UNUserNotificationCenter | NotificationManager + AlarmManager / WorkManager |
| PhotosPicker | Android Photo Picker (`ActivityResultContracts.PickVisualMedia`) |
| UIImagePickerController (카메라) | `ActivityResultContracts.TakePicture` (CameraX 선택적) |
| EKRecurrenceRule | CalendarContract RRULE 문자열 |
| SF Symbols | Material Icons (Extended) |
| NavigationStack | Navigation Compose |
| `.sheet` / `.fullScreenCover` | ModalBottomSheet / Dialog / fullscreen Composable |
| CocoaTouch 음력 Calendar | `java.util.Calendar` 음력 (`ULocale("ko_KR@calendar=chinese")`) |

### 3.1 화면 구조 (iOS CalendarView 1:1 대응)

**Row 1 — 유틸리티 바**
- 왼쪽: 설정 아이콘(⚙) → SettingsScreen
- 중앙: `[Today]`(선택일이 오늘 아닐 때만) + `< 연도 >`(년 이동)
- 오른쪽: 검색(🔍) + 추가(＋, 롱프레스 = 1회성 모드 오버라이드)

**Row 2 — 월 헤더**
- `<` | `MONTH YEAR` (대문자, minimumScaleFactor 대응 = `autoSizeText`) | `>`

**Row 3 — 주간 스트립 (WeekStrip)**
- 선택일 포함 주(일~토) 7셀. 각 셀: 요일 약어 + 날짜 원형 강조 + 날씨 아이콘
- 요일 색상: 일요일 = `SundayRed(#E03131)`, 토요일 = `primary(iOS blue)`, 평일 = `onSurfaceVariant`
- 선택 강조: 날짜 숫자 셀만 원형 클리핑 (요일 텍스트는 원형 밖)
- 날씨: Open-Meteo WMO 코드 → Material Icon + tint. 실패 시 이모지 대체 (0~1개=😀, 2~3개=😊, 4+개=🤯)
- 스와이프 좌우: ±7일 이동. Rubber-band 피드백 0.25× offset

**Hero 영역 (화면 48%)**
- 큰 날짜 숫자 + 요일 문자열 (워터마크 컬러: `onSurface.copy(alpha = 0.07f)` 고정)
  - 주의: `EventCountColor.forCount(0) = Color.Transparent` 을 워터마크에 사용하면 이벤트 0개일 때 히어로가 공백으로 보임 → 항상 고정 alpha 사용
- 왼쪽/오른쪽 스와이프 → 전일/익일 이동 (1/4 화면 임계값)
- 더블탭 / 롱프레스 → 이벤트 추가 에디터
- 음력+날씨 단일 Row: 음력 날짜 좌측(회색, 작은 폰트) / 날씨 이모지+온도 우측 (기존 별도 Row 통합)
- SCHEDULE 리스트: 해당일 이벤트 목록. overdue 항목 빨간색.
- 이벤트 아이템 롱프레스 ContextMenu: 복사(내용포함) / 복사(내용제외) / 삭제 (편집은 이벤트 탭 진입으로 대체)

**하단 미니 달력 (MonthGrid ×2)**
- 현재월 + 다음월 나란히 표시. Surface 카드(RoundedCornerShape 16dp)로 래핑.
- SUN-first 6주(42칸) 그리드
- 헤더 색상: 일(index 0) = `SundayRed(#E03131)`, 토(index 6) = `primary`, 평일 = 기본
- 날짜 셀 색상: 일요일 = SundayRed, 토요일 = primary (선택/오늘이 아닐 때)
- 이벤트 수 무지개 7단계 색상: 0=기본, 1=노(yellow), 2=주(orange), 3=초(green), 4=남(navy), 5=보(purple), 6+=빨(red)
- 선택일: accent 원형 강조. 오늘: accent 테두리.
- 스와이프 좌→다음달, 우→이전달

### 3.2 이벤트 색상 규칙 (iOS 동일)

```
eventCountColor(n):
  0 → 기본(검정)
  1 → yellow   (R:0.95 G:0.85 B:0.00)
  2 → orange   (R:1.00 G:0.55 B:0.00)
  3 → green    (R:0.15 G:0.72 B:0.25)
  4 → navy     (R:0.08 G:0.15 B:0.65)
  5 → purple   (R:0.55 G:0.10 B:0.80)
  6+→ red      (R:0.85 G:0.10 B:0.10)
```

히어로 날짜 숫자: 해당 색상 opacity 0.28 (워터마크).
미니 달력 날짜 숫자: 해당 색상 직접 적용.

### 3.3 설정 (SettingsScreen)

영속화: DataStore Preferences. 필드:
- `plannerMode` (diary / todo / personalEvent)
- `notificationsEnabled` (Boolean)
- `onboardingCompleted` (Boolean)
- `enabledCalendarIDs` (Set\<String\>) — 표시할 캘린더 ID 화이트리스트. 빈 = 전체.
- `lastSeenVersion` (String)
- `appColorScheme` (system / dark / light)

설정 항목:
1. **연동** — Android CalendarProvider 경유 시스템 계정(Google/로컬) 캘린더 소스 선택.
   가정: 자체 Google OAuth 미포함. Android 기기에 추가된 계정 기반.
2. **사용자 테마(플래너 모드)** — diary / todo / personalEvent 중 택1.
3. **알림** — 로컬 알림 on/off + 권한 요청(Android 13+ POST_NOTIFICATIONS).
4. **화면 테마(앱 색상 모드)** — system → dark → light 롤링 버튼.
   `AppCompatDelegate.setDefaultNightMode()` 또는 Compose `darkTheme` 파라미터.

### 3.4 플래너 모드별 이벤트 추가 UX

**(A) diary(일기)**
- 전체화면 에디터 (`fullscreen` Composable)
- 제목 TextField + 본문 TextField(multiline)
- 우상단 경과 시간 실시간 표시 (`mm:ss`, 1시간 이상 시 `h:mm:ss`)
- 에디터 열린 시각 = startDate, 저장 버튼 누른 시각 = endDate 자동 기록
- 제목 기본값: `[yyyy.MM.dd]😀수고했어요.` (이벤트 수 기반 이모지)

**(B) todo(일정관리)**
- BottomSheet (또는 일반 화면)
- 제목 + Due Date DateTimePicker (기본값 = 선택일 23:59) 필수
- 체크리스트 항목 (동적 추가/삭제, 완료 토글)
- CalendarContract notes 에 `- [ ]` / `- [x]` 인코딩
- `isTodo && startDate < now && notes에 "- [ ]" 존재` → 캘린더 뷰 빨간색 표시 (`isOverdue`)

**(C) personalEvent(개인이벤트)**
- 카테고리 선택(Segmented: 복용 / 약속 / 고정) → 템플릿 목록 → 시간 설정

  **medication(복용)**
  - 프리셋 칩: 아침(7시) / 점심(12시) / 저녁(18시) / 직접 — 선택 시 해당 시각 자동 세팅
  - 직접 선택 시 TimePicker 다이얼로그 표시
  - `MedicationPreset` enum(아침/점심/저녁/직접), `EventEditorState.medicationPreset` 필드
  - "기본 시간으로 저장" — DataStore 내 템플릿 `defaultStartHour` 갱신
  - 반복 토글 + 시작/종료일 DatePicker → yearly RRULE
  - 사진 섹션: 갤러리 버튼(`ActivityResultContracts.PickVisualMedia`) + 카메라 버튼(`ActivityResultContracts.TakePicture`)
    → 카메라 임시파일 `cacheDir` 저장 → 최종 저장 시 `filesDir` JPEG 복사
    → `EventEditorState.photoUri: Uri?` 필드로 상태 관리, `EventEditorViewModel`에서 저장 처리
    → FileProvider(`${applicationId}.fileprovider`) AndroidManifest 등록 필수

  **appointment(약속)**
  - 시작/종료 TimePicker 각각
  - 장소 TextField (선택) → CalendarContract.Events.EVENT_LOCATION
  - 반복일정(RRULE) 없음 — 반복 UI 미표시

  **fixed(고정)**
  - 시작/종료 TimePicker
  - 반복 토글 + 시작/종료일 DatePicker → yearly RRULE
  - 구현: `DatePickerDialog` 두 개(시작일·종료일). 상태: `EventEditorState.repeatStartDate` / `repeatEndDate`

### 3.5 타이틀 날짜 패턴 치환 (PlannerLogic)

개인이벤트 저장 시 제목 내 패턴 → 실제 날짜 문자열 치환:

| 패턴 | 예시 (2026-03-08) |
|------|-------------------|
| `yyyy-mm-dd` | `2026-03-08` |
| `yyyy/mm/dd` | `2026/03/08` |
| `yy-mm-dd` | `26-03-08` |
| `yyyy.mm.dd` | `2026.03.08` |
| `dd-mm-yyyy` | `08-03-2026` |
| `d###` | `067` (연중 일련일 3자리) |

날짜 패턴 없는 복용(medication) 카테고리 → 저장 시 auto yearly RRULE 적용. 약속(appointment)은 RRULE 미적용.

### 3.6 템플릿 (EventTemplate)

DataStore(JSON) 영속. 카테고리: medication / appointment / fixed.
필드: `id`, `name`, `category`, `titleFormat`, `dayOfYear`, `defaultStartHour`, `defaultDurationMinutes`.

기본 시드:
- 복용: 아침(7h)/점심(12h)/저녁(18h) 약 복용, 알레르기 약(d###), 날짜기록(yyyy-mm-dd)
- 약속: 연인(19h/120m), 친구(14h/120m), 회식(18h/180m)
- 고정: 정기작업 `[정기작업] (d###) yyyy-mm-dd`

### 3.7 이벤트 복사 (EventCopySheet)

ContextMenu "복사(내용포함/제외)" → BottomSheet:
- DatePicker (목표 날짜) + 제목/시간/메모 미리보기 → 저장 시 CalendarProvider에 새 이벤트 삽입.

### 3.8 이벤트 검색 (EventSearchScreen)

- 검색 쿼리 → CalendarProvider 또는 인메모리 캐시 검색
- 결과 탭 → 해당 날짜로 메인 화면 이동

### 3.9 온보딩 (OnboardingScreen)

노출 조건: 최초 설치 / `onboardingCompleted == false` / `lastSeenVersion ≠ currentVersionName` / DEBUG 매회.

4단계 (iOS 화이트/라이트그레이 디자인):
1. **WELCOME** — 앱 아이콘(📅) + "Wise Calendar" + 기능 요약 + 플래너 모드 선택 카드(✓ 체크마크)
2. **INPUT_MODE** — 일기 + 일정관리 모드 소개 (데모 채팅버블 카드)
3. **PERSONAL_EVENT** — 복용/약속/고정 템플릿 타입 카드
4. **INTEGRATION** — 캘린더 권한 + 알림 권한 요청 버튼

색상: `IosBlue(#007AFF)` 파란 버튼, `IosLightGray(#F2F2F7)` 배경, `IosDarkText(#1C1C1E)`, `IosGrayText(#8E8E93)`

완료 시: `onboardingCompleted = true`, `lastSeenVersion = BuildConfig.VERSION_NAME`.

---

## 4. How — 기술 접근

- **플랫폼**: Android (Kotlin + Jetpack Compose). 단일 앱, 서버 없음.
- **최소 SDK**: API 26 (Android 8.0). 타깃: API 35+.
- **아키텍처**: MVVM + Repository 패턴.
  - `CalendarViewModel` — UI 상태 관리 (StateFlow).
  - `CalendarRepository` — CalendarProvider ContentResolver 추상화.
  - `TemplateRepository` — DataStore JSON 직렬화.
  - `WeatherRepository` — Open-Meteo HTTP(Retrofit/Ktor).
- **영속화**: DataStore Preferences (설정·템플릿), CalendarProvider (이벤트), filesDir (이미지).
- **날씨**: Open-Meteo API (`api.open-meteo.com/v1/forecast`), 위치: FusedLocationProviderClient.
- **알림**: WorkManager + AlarmManager (exact alarm, Android 12+ 권한), NotificationChannel.
- **빌드**: Gradle (Kotlin DSL), 의존성 매니저 없음(AAR/Maven Central 직접).
- 가정: Google Play Services 없이도 동작 (Pure Android). FusedLocation은 선택적.

### 4.1 CalendarProvider 권한

```
READ_CALENDAR   — 이벤트 조회
WRITE_CALENDAR  — 이벤트 추가/수정/삭제
READ_CONTACTS   — 가정: 미사용
POST_NOTIFICATIONS — Android 13+ 알림
READ_MEDIA_IMAGES — Android 13+ 사진
CAMERA          — 카메라 촬영
ACCESS_FINE_LOCATION (또는 COARSE) — 날씨 위치
SCHEDULE_EXACT_ALARM — Android 12+ 정확한 알람
```

### 4.2 순수 로직 (테스트 가능)

iOS `CalendarLogic` / `PlannerLogic` 대응:

- `CalendarLogic.monthGrid(year, month)` → 42칸 List\<DayInfo\>, SUN-first.
- `CalendarLogic.dateKey(date)` → `"yyyy-MM-dd"` 문자열.
- `PlannerLogic.dayOfYear(date)` → Int.
- `PlannerLogic.encodeTodos(items)` → notes 문자열.
- `PlannerLogic.decodeTodos(notes)` → List\<TodoItem\>.
- `PlannerLogic.substituteTitlePatterns(title, date)` → 치환된 제목.
- `PlannerLogic.hasTitleDatePattern(title)` → Boolean.

---

## 5. Success — 성공 기준 / 검증

| 기준 | 측정 |
|------|------|
| 빌드 성공 | `./gradlew assembleDebug` 경고/에러 없음 |
| 동작 완성도 | 히어로·그리드·스와이프·이벤트 표시 무크래시 |
| CalendarProvider 연동 | 권한 허용/거부 분기, 이벤트 조회·추가·수정·삭제 정상 |
| 단위 테스트 | `monthGrid`·`dateKey`·day-of-year·모드/템플릿 인코딩 JUnit 통과 |
| 설정/모드 | DataStore 영속, 플래너 모드별 추가 UX 분기, 모드 전환 즉시 반영 |
| 템플릿 | 기본 시드, `[#ddd]` 패턴 직렬화, `defaultStartHour` 저장 |
| 온보딩 | 최초 실행/DEBUG 매회 노출, 완료 후 미노출(릴리스) |
| diary 시간 | 에디터 오픈=startDate, 저장=endDate 자동 기록 |
| todo 기한 | due-date DateTimePicker 필수, 기한 초과 빨간색 표시 |
| medication 시간 | 아침/점심/저녁/직접 프리셋 칩 + TimePicker 다이얼로그, 사진 첨부(갤러리+카메라), 기본값 템플릿 저장 |
| appointment 시간+장소 | TimePicker×2 + 장소 TextField → CalendarContract.EVENT_LOCATION (반복 없음) |
| fixed 반복 | 시작·종료 시간, 반복 토글 + 기간 DatePicker, yearly RRULE |
| 이벤트 색상 | 무지개 7단계 미니달력+히어로 동시 적용 |
| 이벤트 복사 | ContextMenu → BottomSheet → 저장 |
| 이벤트 검색 | 쿼리 → 결과 탭 → 날짜 이동 |
| 날씨 | Open-Meteo 조회 성공 시 아이콘, 실패 시 이모지 대체 |
| 다크/라이트/시스템 테마 | 롤링 버튼 → 앱 전체 즉시 반영 |
| (확장 목표) 출시 | 가정: Google Play 정책·권한 심사 통과 |

---

## 6. Android 포팅 시 주요 차이점 / 리스크

| 항목 | iOS | Android | 비고 |
|------|-----|---------|------|
| 날씨 | WeatherKit + Open-Meteo 폴백 | Open-Meteo 전용 | WeatherKit은 Apple 전용 |
| 캘린더 쓰기 | EKEventStore | CalendarProvider ContentResolver | Intent로도 가능 (앱 없으면 fallback) |
| 반복이벤트 | EKRecurrenceRule 객체 | RRULE 문자열 (`FREQ=YEARLY;INTERVAL=1`) | RFC 5545 호환 |
| 사진 저장 | EventImageStore (앱 Documents) | `filesDir` JPEG (외부 저장소 미사용). FileProvider(`${applicationId}.fileprovider`) 등록, 카메라 임시파일 `cacheDir` 경유 | 앱 삭제 시 소멸. FileProvider 미등록 시 카메라 `TakePicture` 크래시 |
| 음력 | `Calendar(identifier: .chinese)` | `ULocale("ko_KR@calendar=chinese")` | ICU4J 기반 |
| SF Symbols | 내장 | Material Icons Extended 대체 | 1:1 매핑 필요 |
| 정확한 알람 | 제약 없음 | Android 12+ `SCHEDULE_EXACT_ALARM` 권한 필요 | |
| 포어그라운드 서비스 | 불필요 | WorkManager로 충분 (가정) | |
| 잔여 리스크 | — | CalendarProvider `hasAccess` 변경 구독 (iOS 동일 이슈) | 권한 늦은 허용 시 화면 미갱신 → StateFlow + re-query로 해결 |

---

다음 단계: 검토·수정 후 `/wise-dev-standard:recommend` 로 스택·도메인 추천을 이어가세요.
