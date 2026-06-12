# impl/8 Test Scenario — DiaryTimerState, CalendarFilterState, SettingsViewModel

## 대상 모듈
- `ui/diary/DiaryTimerState.kt`
- `ui/settings/CalendarFilterState.kt`
- `ui/settings/SettingsViewModel.kt`
- `ui/settings/SettingsScreen.kt` (캘린더 목록 섹션 추가)
- `ui/navigation/WiseCalNavGraph.kt` (SettingsViewModel 연결)

## 시험 분류
Unit 시험 (JVM, 순수 상태 로직)

---

## DiaryTimerStateTest (13 케이스)

| # | 케이스 | 입력 | 기대 |
|---|--------|------|------|
| 1 | 초기 elapsedSeconds | `DiaryTimerState(base)` | `0L` |
| 2 | displayTime at 0s | `elapsed=0` | `"00:00"` |
| 3 | displayTime at 1s | `elapsed=1` | `"00:01"` |
| 4 | displayTime at 90s | `elapsed=90` | `"01:30"` |
| 5 | displayTime at 3599s | `elapsed=3599` | `"59:59"` |
| 6 | displayTime leading zeros | `elapsed=65` | `"01:05"` |
| 7 | h:mm:ss at 3600s | `elapsed=3600` | `"1:00:00"` |
| 8 | h:mm:ss at 3665s | `elapsed=3665` | `"1:01:05"` |
| 9 | hours no leading zero | `elapsed=3601` | starts with "1:" not "01:" |
| 10 | displayTime at 36000s | `elapsed=36000` | `"10:00:00"` |
| 11 | endTime equals startedAt at 0s | — | `startedAt` |
| 12 | endTime = startedAt + 60s | `elapsed=60` | `startedAt.plusSeconds(60)` |
| 13 | endTime = startedAt + 1h | `elapsed=3600` | `startedAt.plusHours(1)` |

---

## CalendarFilterStateTest (13 케이스)

| # | 케이스 | 입력 | 기대 |
|---|--------|------|------|
| 1 | 초기 availableCalendars | — | empty |
| 2 | 초기 enabledIds | — | empty |
| 3 | isAllEnabled=true when empty | `enabledIds={}` | `true` |
| 4 | isAllEnabled=false when non-empty | `enabledIds={1}` | `false` |
| 5 | effectiveIds all when isAllEnabled | `[cal1,cal2], ids={}` | `{1,2}` |
| 6 | effectiveIds only enabledIds | `[1,2,3], ids={1}` | `{1}` |
| 7 | effectiveIds empty when isAllEnabled + no calendars | — | `{}` |
| 8 | toggle adds id | `toggle(1)` | `1 in enabledIds` |
| 9 | toggle removes id | `ids={1,2}, toggle(1)` | `1 not in, 2 in` |
| 10 | toggle preserves others | `ids={2,3}, toggle(1)` | `{1,2,3}` |
| 11 | isEnabled true when isAllEnabled | `enabledIds={}` | `isEnabled(1)=true` |
| 12 | isEnabled true when id in set | `ids={1}` | `isEnabled(1)=true` |
| 13 | isEnabled false when not in set | `ids={2}` | `isEnabled(1)=false` |

---

## 실행 명령
```bash
./gradlew testDevDebugUnitTest \
  --tests "com.wisecal.android.ui.diary.*" \
  --tests "com.wisecal.android.ui.settings.CalendarFilterStateTest"
```
