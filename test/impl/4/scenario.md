# impl/4 테스트 시나리오

## 범위

| 파일 | 분류 |
|------|------|
| `ui/editor/EventEditorState.kt` | 에디터 상태 + 순수 로직 |
| `ui/editor/EventEditorViewModel.kt` | 저장/편집/삭제/복사 ViewModel |
| `ui/editor/DiaryEditorSection.kt` | 일기 모드 에디터 컴포저블 |
| `ui/editor/TodoEditorSection.kt` | 할일 모드 에디터 컴포저블 |
| `ui/editor/PersonalEventEditorSection.kt` | 개인이벤트 모드 에디터 컴포저블 |
| `ui/editor/EventEditorScreen.kt` | 에디터 루트 컴포저블 |
| `data/CalendarRepository.kt` | addEvent에 rrule 파라미터 추가 |

## 단위 테스트 (JVM 호스트)

### EventEditorStateTest (14개)

| # | 케이스 | 기대 |
|---|--------|------|
| 1 | DIARY canSave — 제목 있음 | true |
| 2 | DIARY canSave — 본문만 있음 | true |
| 3 | DIARY canSave — 둘 다 없음 | false |
| 4 | TODO canSave — 항목 있음 | true |
| 5 | TODO canSave — 항목 전부 blank | false |
| 6 | PERSONAL_EVENT canSave — 템플릿+제목 | true |
| 7 | PERSONAL_EVENT canSave — 템플릿 없음 | false |
| 8 | DIARY 빈 제목 resolvedTitle → "일기" | 일치 |
| 9 | TODO 빈 제목 resolvedTitle → "할 일" | 일치 |
| 10 | PERSONAL_EVENT yyyy-mm-dd 패턴 치환 | 날짜 치환 |
| 11 | DIARY diaryBody → resolvedNotes | 일치 |
| 12 | TODO todos → encodeTodos 형식 | 일치 |
| 13 | TODO 빈 todos → resolvedNotes null | null |
| 14 | PERSONAL_EVENT resolvedNotes → null | null |

### EventEditorSaveLogicTest (12개)

| # | 케이스 | 기대 |
|---|--------|------|
| 1 | MEDICATION 날짜패턴 없음 → yearly RRULE | "FREQ=YEARLY;INTERVAL=1" |
| 2 | MEDICATION 날짜패턴 있음 → null | null |
| 3 | MEDICATION repeat=true → UNTIL 포함 | "…UNTIL=…" |
| 4 | MEDICATION 날짜패턴 있어도 repeat=true → RRULE | not null |
| 5 | APPOINTMENT 날짜패턴 없음 → yearly | "FREQ=YEARLY;INTERVAL=1" |
| 6 | APPOINTMENT 날짜패턴 있음 → null | null |
| 7 | FIXED 날짜패턴 없음 → yearly | "FREQ=YEARLY;INTERVAL=1" |
| 8 | FIXED repeat=true → UNTIL 포함 | "…UNTIL=…" |
| 9 | FIXED 날짜패턴 있음 repeat=false → null | null |
| 10 | DIARY 모드 → rrule null | null |
| 11 | TODO 모드 → rrule null | null |
| 12 | d### 패턴 감지 | hasTitleDatePattern=true |
| +1| no pattern → false | false |

실제 테스트 합계: **26개** (EventEditorStateTest 14 + EventEditorSaveLogicTest 12 + 패턴 2개 포함)

## 컴파일 확인

```
./gradlew testDevDebugUnitTest --tests "com.wisecal.android.ui.editor.*"
```
