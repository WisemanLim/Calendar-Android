# impl/1 시나리오 — 순수 로직 (CalendarLogic + PlannerLogic)

## 구현 범위
PRD §4.2 순수 로직 — 뷰 비의존, 단위 테스트 가능.

- `CalendarLogic.kt` — `monthGrid`, `dateKey` (iOS `CalendarLogic.swift` 대응)
- `PlannerLogic.kt` — `dayOfYear`, `fixedTitle`, `parseFixedTitle`, `encodeTodos`, `decodeTodos`, `substituteTitlePatterns`, `hasTitleDatePattern` (iOS `PlannerLogic.swift` 대응)
- `PlannerMode.kt` — enum (diary / todo / personalEvent)

## 검증 항목

| # | 테스트 | 합격 기준 |
|---|--------|-----------|
| 1 | `dateKey` 포맷 | `yyyy-MM-dd` 3케이스 |
| 2 | `monthGrid` 항상 42칸 | 1/2/6월 + 윤년 4케이스 |
| 3 | `monthGrid` SUN-first 첫 셀 | 2026-06: 첫 셀 = 2026-05-31 |
| 4 | `monthGrid` 일요일 시작 월 선행 없음 | 2026-11: 첫 셀 = 2026-11-01 |
| 5 | `monthGrid` 후행 셀 다음달 | isCurrentMonth=false |
| 6 | `monthGrid` 윤년 2월 29일 | currentMonth=29개 |
| 7 | `dayOfYear` Jan1=1, Dec31 비윤=365, 윤=366, Mar8=67 | 4케이스 |
| 8 | `fixedTitle` / `parseFixedTitle` 라운드트립 | 정상/비정상 입력 |
| 9 | `encodeTodos` / `decodeTodos` 라운드트립 | 3항목 인코딩·디코딩 |
| 10 | `substituteTitlePatterns` 6가지 패턴 + 복합 | 2026-03-08 기준 |
| 11 | `hasTitleDatePattern` 탐지/미탐지 | 패턴 있/없 |

## 명령
```bash
JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" \
  ./gradlew testDevDebugUnitTest
```
