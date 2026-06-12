# impl/5 Test Scenario — Onboarding, Search, Settings

## 대상 모듈
- `ui/onboarding/OnboardingState.kt`
- `ui/onboarding/OnboardingScreen.kt`
- `ui/search/SearchState.kt`
- `ui/search/SearchViewModel.kt`
- `ui/search/SearchScreen.kt`
- `ui/settings/SettingsScreen.kt`

## 시험 분류
Unit 시험 (JVM, 순수 로직 대상)

---

## OnboardingStateTest (13 케이스)

| # | 케이스 | 입력 | 기대 |
|---|--------|------|------|
| 1 | 초기 step | `OnboardingState()` | `currentStep == WELCOME` |
| 2 | 초기 권한 | `OnboardingState()` | `calendarPermissionGranted == false` |
| 3 | WELCOME canProceed | `currentStep=WELCOME` | `true` |
| 4 | PERMISSION canProceed(거부) | `currentStep=PERMISSION, grant=false` | `false` |
| 5 | PERMISSION canProceed(허용) | `currentStep=PERMISSION, grant=true` | `true` |
| 6 | PLANNER_MODE canProceed(미선택) | `currentStep=PLANNER_MODE, mode=null` | `false` |
| 7 | PLANNER_MODE canProceed(선택) | `currentStep=PLANNER_MODE, mode=DIARY` | `true` |
| 8 | COMPLETE canProceed | `currentStep=COMPLETE` | `false` |
| 9 | nextStep WELCOME→PERMISSION | `nextStep()` on WELCOME | `PERMISSION` |
| 10 | nextStep PERMISSION→PLANNER_MODE | `nextStep()` on PERMISSION | `PLANNER_MODE` |
| 11 | nextStep PLANNER_MODE→COMPLETE | `nextStep()` on PLANNER_MODE | `COMPLETE` |
| 12 | nextStep COMPLETE stays | `nextStep()` on COMPLETE | `COMPLETE` |
| 13 | isLastStep PLANNER_MODE만 | step별 비교 | PLANNER_MODE=true, 나머지=false |

---

## SearchStateTest (13 케이스)

| # | 케이스 | 입력 | 기대 |
|---|--------|------|------|
| 1 | 초기 query | `SearchState()` | `query == ""` |
| 2 | 초기 isSearching | `SearchState()` | `isSearching == false` |
| 3 | 초기 results | `SearchState()` | `results.isEmpty()` |
| 4 | hasQuery(빈 문자열) | `query=""` | `false` |
| 5 | hasQuery(1글자) | `query="a"` | `false` |
| 6 | hasQuery(공백만) | `query="  "` | `false` |
| 7 | hasQuery(2글자) | `query="ab"` | `true` |
| 8 | hasQuery(긴 문자열) | `query="회의"` | `true` |
| 9 | displayResults(query 없음) | `query="", results=[e]` | `empty` |
| 10 | displayResults(1글자) | `query="가", results=[e]` | `empty` |
| 11 | displayResults(hasQuery) | `query="이벤트", results=[e1,e2]` | `size==2` |
| 12 | hasResults(비어있음) | `results=[]` | `false` |
| 13 | hasResults(있음) | `results=[e]` | `true` |

---

## 실행 명령
```bash
./gradlew testDevDebugUnitTest \
  --tests "com.wisecal.android.ui.onboarding.*" \
  --tests "com.wisecal.android.ui.search.*"
```
