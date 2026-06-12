# impl/11 시험 시나리오

## 대상 모듈
- `data/EventImageStore.kt` — 복용이벤트 사진 파일명/경로 규칙 (순수 로직)
- `ui/monthgrid/MonthGridState.kt` — 미니 달력 현재+다음 월 상태, 이동 경계

---

## EventImageStoreTest (13개)

| # | 시나리오 | 입력 | 기대 결과 |
|---|---------|------|----------|
| 1 | fileName "event_" 시작 | eventId=1, ts=1000 | starts with "event_" |
| 2 | fileName ".jpg" 끝 | eventId=1, ts=1000 | ends with ".jpg" |
| 3 | fileName eventId 포함 | eventId=42, ts=1000 | contains "42" |
| 4 | fileName timestamp 포함 | eventId=1, ts=9999999 | contains "9999999" |
| 5 | fileName 전체 형식 | eventId=7, ts=1234567890 | "event_7_1234567890.jpg" |
| 6 | 다른 eventId → 다른 이름 | id=1 vs id=2, ts=5000 | 두 값 다름 |
| 7 | extractEventId 유효 파일명 | "event_42_1000.jpg" | 42L |
| 8 | extractEventId 잘못된 prefix → null | "photo_42_1000.jpg" | null |
| 9 | extractEventId 잘못된 확장자 → null | "event_42_1000.png" | null |
| 10 | extractEventId 빈 문자열 → null | "" | null |
| 11 | isEventImage true — 유효 | "event_1_1000.jpg" | true |
| 12 | isEventImage false — prefix 틀림 | "img_1_1000.jpg" | false |
| 13 | isEventImage false — 확장자 틀림 | "event_1_1000.png" | false |

---

## MonthGridStateTest (13개)

| # | 시나리오 | 입력 | 기대 결과 |
|---|---------|------|----------|
| 1 | nextMonth = baseMonth+1 | base=2026-06 | 2026-07 |
| 2 | 12월 nextMonth = 다음해 1월 | base=2025-12 | 2026-01 |
| 3 | selectedDate 보존 | today=2026-06-11 | same |
| 4 | canGoBack false — minMonth | base=min | false |
| 5 | canGoBack true — minMonth 초과 | base=min+1 | true |
| 6 | canGoForward false — nextMonth==max | base=2031-11, max=2031-11 | false |
| 7 | canGoForward true — max 여유 있음 | base=2026-06, max=2031-12 | true |
| 8 | goBack baseMonth 감소 | base=2026-06 | 2026-05 |
| 9 | goBack at minMonth 유지 | base=min | min |
| 10 | goForward baseMonth 증가 | base=2026-06 | 2026-07 |
| 11 | goForward at max boundary 유지 | base=2031-11, max=2031-11 | 2031-11 |
| 12 | selectDate selectedDate 변경 | date=2026-07-04 | 2026-07-04 |
| 13 | selectDate baseMonth 불변 | date=2026-07-04 | baseMonth=2026-06 |

---

## 실행 명령

```bash
make test
```
