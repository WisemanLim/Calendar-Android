# impl/3 테스트 시나리오

## 범위

| 파일 | 분류 |
|------|------|
| `model/WeatherInfo.kt` | WMO 날씨 코드 매핑 |
| `data/WeatherApiService.kt` | Retrofit 인터페이스 + DTO |
| `data/WeatherRepository.kt` | 인터페이스 + OpenMeteo 구현 |
| `ui/CalendarUiState.kt` | UI 상태 data class + 파생 프로퍼티 |
| `ui/CalendarViewModel.kt` | ViewModel + StateFlow |
| `ui/theme/WiseCalColors.kt` | 레인보우 색상 정의 |
| `ui/components/HeroDateBar.kt` | 히어로 날짜 표시 컴포저블 |
| `ui/components/WeekStrip.kt` | 주간 스트립 컴포저블 |
| `ui/components/MonthGrid.kt` | 6×7 월 그리드 컴포저블 |
| `ui/MainScreen.kt` | 루트 컴포저블 |
| `MainActivity.kt` | ViewModel 팩토리 연결 |

## 단위 테스트 (JVM 호스트)

UI 컴포저블 / CalendarViewModel(viewModelScope=Main 필요) → Instrumented 테스트 대상.
JVM 호스트로 검증 가능한 순수 로직만 대상.

### WeatherInfoTest (13개)

| # | 케이스 | 기대 |
|---|--------|------|
| 1 | code 0 → iconEmoji "☀️" | 일치 |
| 2 | code 1 → "🌤️" | 일치 |
| 3 | code 2 → "🌤️" | 일치 |
| 4 | code 3 → "☁️" | 일치 |
| 5 | code 45 → "🌫️" | 일치 |
| 6 | code 51 → "🌦️" | 일치 |
| 7 | code 61 → "🌧️" | 일치 |
| 8 | code 71 → "❄️" | 일치 |
| 9 | code 95 → "⛈️" | 일치 |
| 10 | code 999 → "🌡️" | 폴백 |
| 11 | displayText 22.9 → "22°" | 정수 절삭 |
| 12 | displayText -5.9 → "-5°" | 음수 절삭 |
| 13 | description code 0 → "맑음" | 일치 |

### CalendarUiStateTest (13개)

| # | 케이스 | 기대 |
|---|--------|------|
| 1 | 초기 plannerMode = TODO | 일치 |
| 2 | 초기 isLoading = false | false |
| 3 | 초기 weather = null | null |
| 4 | eventsForSelectedDate 빈 경우 | empty |
| 5 | eventsForSelectedDate 3개 | size=3 |
| 6 | 다른 날짜 이벤트는 포함 안 됨 | empty |
| 7 | selectedDate 변경 시 eventsForSelectedDate 갱신 | 변경 반영 |
| 8 | monthGrid size = 42 | 42 |
| 9 | 이벤트 없는 날 eventCount = 0 | 0 |
| 10 | 이벤트 5개인 날 eventCount = 5 | 5 |
| 11 | monthGrid는 currentMonth 기준 (selectedDate 무관) | 6월 셀 = 6월 날짜 |
| 12 | monthGrid 선행 셀 isCurrentMonth = false | false |
| 13 | monthGrid 후행 셀 isCurrentMonth = false | false |

**합계: 26개**

## 컴파일 확인

```
./gradlew testDevDebugUnitTest --tests "com.wisecal.android.ui.*"
```
