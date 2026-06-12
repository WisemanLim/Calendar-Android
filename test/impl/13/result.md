# impl/13 결과

## 판정: PASS ✅

## 컴파일
```
BUILD SUCCESSFUL in 975ms
```
경고 없음.

## 수정/생성 파일

| 파일 | 변경 내용 |
|------|-----------|
| `data/WeatherApiService.kt` | `WeatherForecastService` + `DailyForecastResponse` / `DailyDto` DTO 추가 |
| `data/WeatherRepository.kt` | `getWeeklyForecast()` 구현, `WeatherForecastService` 주입 |
| `logic/LunarDateHelper.kt` | **신규** — `android.icu.util.ChineseCalendar` 기반 음력 변환 |
| `ui/CalendarUiState.kt` | `weekWeatherMap` 추가, `nextMonthGrid` 이벤트 count 반영 수정 |
| `ui/CalendarViewModel.kt` | `loadMonth` → 현재+다음 달 동시 로드, `loadWeather` → 주간 예보 포함 |
| `ui/components/WeekStrip.kt` | `weatherByDate` 파라미터 추가, dot → 이모티콘(날씨/fallback) |
| `ui/components/MonthGrid.kt` | `yearMonth` 파라미터 추가, 월/연도 헤더 표시 |
| `ui/MainScreen.kt` | 레이아웃 개선(Hero=weight(1f), 달력 하단 고정), 음력 표시, WeekStrip 날씨 전달 |
| `ui/settings/SettingsScreen.kt` | 캘린더 선택 항상 노출(빈 경우 안내), 고객문의 섹션 추가 |
| `ui/navigation/WiseCalNavGraph.kt` | `AppVersionState.shouldShowOnboarding` 기반 시작화면 결정, 완료 시 버전 저장 |
| `ui/onboarding/OnboardingScreen.kt` | iOS 스타일 완전 재설계 (다크 그라디언트, 카드형 선택, 애니메이션) |

## TC 결과

| TC | 결과 | 비고 |
|----|------|------|
| TC-1 캘린더 선택 | ✅ | 항상 섹션 표시, 빈 경우 안내 문구 |
| TC-2 고객문의 | ✅ | 이메일 + 앱 버전 섹션 추가 |
| TC-3 하단 달력 헤더 | ✅ | yearMonth 파라미터로 월 헤더 표시 |
| TC-4 이벤트 색깔 | ✅ | loadMonth에서 두 달 병합, nextMonthGrid count 반영 |
| TC-5 주간 이모티콘 | ✅ | 날씨 있으면 날씨 emoji, 없으면 DiaryTitleBuilder.emojiFor |
| TC-6 음력 날짜 | ✅ | android.icu ChineseCalendar 사용 |
| TC-7 하단 달력 고정 | ✅ | Hero=weight(1f), 달력=wrapContentHeight |
| TC-8 debug 온보딩 | ✅ | AppVersionState.isDebugBuild → shouldShowOnboarding |
| TC-9 iOS 스타일 | ✅ | 다크 퍼플 그라디언트 배경, 진행 점, 카드형 UI |
| TC-10 버전 저장 | ✅ | onComplete에서 setLastSeenVersion 호출 |
