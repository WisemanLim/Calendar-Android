# impl/6 Test Scenario — EventCopy, Navigation

## 대상 모듈
- `ui/copy/EventCopyState.kt`
- `ui/copy/EventCopyViewModel.kt`
- `ui/copy/EventCopySheet.kt`
- `ui/navigation/NavRoute.kt`
- `ui/navigation/WiseCalNavGraph.kt`
- `MainActivity.kt` (NavGraph 연결)
- `MainScreen.kt` (nav 콜백 + ContextMenu 추가)

## 시험 분류
Unit 시험 (JVM, 순수 상태 로직)

---

## EventCopyStateTest (12 케이스)

| # | 케이스 | 입력 | 기대 |
|---|--------|------|------|
| 1 | 초기 includeNotes | `EventCopyState(event, date)` | `true` |
| 2 | 초기 isSaving | `EventCopyState(event, date)` | `false` |
| 3 | 초기 canSave | `EventCopyState(event, date)` | `true` |
| 4 | canSave=false when isSaving | `isSaving=true` | `false` |
| 5 | resolvedNotes includeNotes=true | `includeNotes=true, notes="노트"` | `"노트"` |
| 6 | resolvedNotes includeNotes=false | `includeNotes=false` | `null` |
| 7 | resolvedNotes when source.notes=null | `notes=null, includeNotes=true` | `null` |
| 8 | resolvedStartTime = targetDate + source time | `startHour=14` | `date:14:00` |
| 9 | resolvedEndTime = targetDate + source time | `endHour=15` | `date:15:00` |
| 10 | resolvedTitle no pattern → unchanged | `title="회의"` | `"회의"` |
| 11 | resolvedTitle yyyy-mm-dd → substituted | `title="기록 yyyy-mm-dd", date=2026-06-11` | `"기록 2026-06-11"` |
| 12 | targetDate 변경 → resolvedStartTime 업데이트 | `copy(targetDate=2026-12-25)` | `2026-12-25:09:00` |

---

## 실행 명령
```bash
./gradlew testDevDebugUnitTest --tests "com.wisecal.android.ui.copy.*"
```
