# impl/2 테스트 시나리오

## 범위

| 파일 | 분류 |
|------|------|
| `model/AppColorScheme.kt` | enum |
| `model/TemplateCategory.kt` | enum |
| `model/EventTemplate.kt` + `defaultTemplates` | data class + 시드 |
| `model/CalendarEvent.kt` | data class |
| `data/AppSettings.kt` | DataStore 래퍼 (플로우 선언) |
| `data/TemplateStore.kt` + `EventTemplateDto` | DataStore + Gson |
| `data/CalendarRepository.kt` | ContentResolver 인터페이스 + 구현 |

## 단위 테스트 대상 (JVM 호스트)

`AppSettings` / `AndroidCalendarRepository` 는 Android Context 필요 → Instrumented 테스트 대상.
JVM 호스트 테스트로 검증 가능한 로직만 대상.

### EventTemplateTest (11개)

| # | 케이스 | 기대 |
|---|--------|------|
| 1 | MEDICATION + dayOfYear → fixedDisplayTitle null | null |
| 2 | FIXED + dayOfYear null → null | null |
| 3 | FIXED + dayOfYear=162 → `[#162] 정기점검` | 문자열 일치 |
| 4 | defaultTemplates 크기 | 9 |
| 5 | defaultTemplates id 유일성 | distinct == total |
| 6 | 카테고리 분포 (MEDICATION=5, APPOINTMENT=3, FIXED=1) | 카운트 일치 |
| 7 | MEDICATION 기간 = 0 | 전부 0 |
| 8 | APPOINTMENT 기간 > 0 | 전부 양수 |
| 9 | 동일 id data class 동등성 | == |
| 10 | fromString("UNKNOWN") → MEDICATION | 기본값 |
| 11 | fromString round-trip 전체 값 | entries == |

### CalendarEventTest (7개)

| # | 케이스 | 기대 |
|---|--------|------|
| 1 | date = startTime.toLocalDate() | 날짜 일치 |
| 2 | displayTime = "HH:mm" (09:05) | "09:05" |
| 3 | isAllDay → displayTime = "" | 빈 문자열 |
| 4 | isEmpty = true (제목 blank, notes null) | true |
| 5 | isEmpty = false (제목 있음) | false |
| 6 | isEmpty = false (notes 있음) | false |
| 7 | 자정(00:00) displayTime | "00:00" |

### TemplateStoreSerializationTest (8개)

| # | 케이스 | 기대 |
|---|--------|------|
| 1 | defaultTemplates round-trip 크기 유지 | 9 |
| 2 | round-trip id 보존 | 일치 |
| 3 | round-trip category 보존 | 일치 |
| 4 | round-trip titleFormat 보존 | 일치 |
| 5 | FIXED + dayOfYear=162 round-trip + fixedDisplayTitle | "[#162] 정기점검" |
| 6 | 빈 리스트 round-trip | empty |
| 7 | 비정상 JSON → defaultTemplates 폴백 | size=9 |
| 8 | EventTemplateDto from/toEventTemplate 역변환 | == orig |

**합계: 26개**

## 컴파일 확인

```
./gradlew testDevDebugUnitTest --tests "com.wisecal.android.data.*"
```
