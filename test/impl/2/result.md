# impl/2 테스트 결과

**판정: PASS**

## 테스트 요약

| 클래스 | 테스트 | PASS | FAIL | SKIP |
|--------|--------|------|------|------|
| `EventTemplateTest` | 11 | 11 | 0 | 0 |
| `CalendarEventTest` | 7 | 7 | 0 | 0 |
| `TemplateStoreSerializationTest` | 8 | 8 | 0 | 0 |
| **합계** | **26** | **26** | **0** | **0** |

## 구현 완료 파일

| 파일 | 역할 |
|------|------|
| `model/AppColorScheme.kt` | 앱 색상 테마 enum (SYSTEM/DARK/LIGHT) |
| `model/TemplateCategory.kt` | 템플릿 카테고리 enum (MEDICATION/APPOINTMENT/FIXED) |
| `model/EventTemplate.kt` | 이벤트 템플릿 data class + 시드 9개 |
| `model/CalendarEvent.kt` | CalendarProvider 이벤트 매핑 data class |
| `data/AppSettings.kt` | DataStore Preferences 래퍼 (6개 키) |
| `data/TemplateStore.kt` | DataStore + Gson 직렬화 (EventTemplateDto 포함) |
| `data/CalendarRepository.kt` | CalendarProvider ContentResolver CRUD 인터페이스 + AndroidCalendarRepository |

## 비고

- `AppSettings` / `AndroidCalendarRepository` 는 Android Context 의존 → Instrumented 테스트(impl/5 이후 대상).
- `AndroidCalendarRepository.queryEvents` — `DTEND > 0` guard: CalendarProvider 일부 전일 이벤트가 0 반환 케이스 처리.
- TemplateStore는 deserialize 실패 시 `defaultTemplates` 폴백 처리 (`runCatching`).

## 실행 명령

```
JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" \
  ./gradlew testDevDebugUnitTest --tests "com.wisecal.android.data.*"
```

## 다음 impl/3 예정

- `CalendarViewModel.kt` (StateFlow + ViewModel, 달력 상태 관리)
- `WeatherRepository.kt` (Open-Meteo Retrofit, 현재 날씨)
- 메인 UI 컴포저블: `HeroDateBar`, `WeekStrip`, `MonthGrid`
