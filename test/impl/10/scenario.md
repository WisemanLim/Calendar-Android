# impl/10 시험 시나리오

## 대상 모듈
- `logic/DiaryTitleBuilder.kt` — 일기 기본 제목 생성 (이모지 + 날짜 형식)
- `ui/weekstrip/WeekStripState.kt` — 주간 스트립 7셀 상태 (일요일 시작 + 날씨 매핑)

---

## DiaryTitleBuilderTest (13개)

| # | 시나리오 | 입력 | 기대 결과 |
|---|---------|------|----------|
| 1 | emojiFor 0개 → 😀 | eventCount=0 | "😀" |
| 2 | emojiFor 1개 → 😀 | eventCount=1 | "😀" |
| 3 | emojiFor 2개 → 😊 | eventCount=2 | "😊" |
| 4 | emojiFor 3개 → 😊 | eventCount=3 | "😊" |
| 5 | emojiFor 4개 → 🤯 | eventCount=4 | "🤯" |
| 6 | emojiFor 100개 → 🤯 | eventCount=100 | "🤯" |
| 7 | defaultTitle 날짜 포함 | 2026-06-11, 0 | "2026.06.11" 포함 |
| 8 | defaultTitle 대괄호 날짜 시작 | 2026-06-11, 0 | "[2026.06.11]" 시작 |
| 9 | defaultTitle "수고했어요." 끝 | 2026-06-11, 0 | "수고했어요." 끝 |
| 10 | defaultTitle 공백 없음 (정규식 검증) | 2026-06-11, 0 | `[yyyy.MM.dd]{emoji}수고했어요.` 패턴 |
| 11 | defaultTitle 전체 문자열 (1월 5일, 0개) | 2026-01-05, 0 | "[2026.01.05]😀수고했어요." |
| 12 | defaultTitle 2개 → 😊 포함 | 2026-06-11, 2 | "😊" 포함 |
| 13 | defaultTitle 5개 → 🤯 포함 | 2026-06-11, 5 | "🤯" 포함 |

---

## WeekStripStateTest (15개)

| # | 시나리오 | 입력 | 기대 결과 |
|---|---------|------|----------|
| 1 | weekDays 항상 7개 | selectedDate=목요일 | size==7 |
| 2 | weekDays[0] 일요일 | selectedDate=목요일 | SUNDAY |
| 3 | weekDays[6] 토요일 | selectedDate=목요일 | SATURDAY |
| 4 | sundayStart 정확성 (목요일→일요일) | 2026-06-11 | 2026-06-07 |
| 5 | saturdayEnd = sundayStart+6 | 2026-06-11 | 2026-06-13 |
| 6 | 같은 주 일요일/목요일 → 동일 weekDays | sun+thu | same list |
| 7 | weekDays에 selectedDate 포함 | 목요일 | contains(thursday) |
| 8 | 연도 경계 1월 1일(목) → 이전 주 일요일 시작 | 2026-01-01 | SUNDAY, contains(1/1) |
| 9 | isSelected true — selectedDate | thursday | true |
| 10 | isSelected false — 다른 날짜 | sunday | false |
| 11 | isToday true — today 파라미터로 전달 | today=thursday | true |
| 12 | weatherFor null — weatherMap 없음 | default | null |
| 13 | weatherFor 매핑된 날씨 반환 | weather@thursday | WeatherInfo(22,0) |
| 14 | fallbackEmoji 0개 → 😀 | 0 | "😀" |
| 15 | fallbackEmoji 4개 → 🤯 | 4 | "🤯" |

---

## 실행 명령

```bash
make test
# = ./gradlew testDevDebugUnitTest
```
