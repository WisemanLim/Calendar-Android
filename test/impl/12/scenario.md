# impl/12 시험 시나리오

## 대상 모듈
- `ui/onboarding/AppVersionState.kt` — 온보딩 노출 조건 판정
- `ui/navigation/YearNavigationState.kt` — 유틸리티 바 `< 연도 >` 이동 상태

---

## AppVersionStateTest (12개)

| # | 시나리오 | 조건 | 기대 결과 |
|---|---------|------|----------|
| 1 | shouldShowOnboarding false — 정상 | completed=true, version일치, debug=false | false |
| 2 | shouldShowOnboarding true — DEBUG 빌드 | debug=true | true |
| 3 | shouldShowOnboarding true — 온보딩 미완료 | completed=false | true |
| 4 | shouldShowOnboarding true — 버전 불일치 | current=1.1, last=1.0 | true |
| 5 | shouldShowOnboarding true — 세 조건 모두 | debug+uncompleted+mismatch | true |
| 6 | shouldShowOnboarding true — completed지만 버전 불일치 | completed=true, current=2.0, last=1.0 | true |
| 7 | shouldShowOnboarding false — debug=false+completed+버전일치 | current=3.5, last=3.5 | false |
| 8 | versionToSave = currentVersionName | current="2.1.0" | "2.1.0" |
| 9 | versionToSave ≠ lastSeenVersion | current=2.0, last=1.9 | "2.0" |
| 10 | shouldShowOnboarding true — lastSeen 빈 문자열 | current="1.0", last="" | true |
| 11 | shouldShowOnboarding false — 둘 다 빈 문자열 + completed | current="", last="" | false |
| 12 | debug=true — completed+version일치 무시 | debug=true | true |

---

## YearNavigationStateTest (12개)

| # | 시나리오 | 조건 | 기대 결과 |
|---|---------|------|----------|
| 1 | displayLabel = 연도 문자열 | year=2026 | "2026" |
| 2 | canGoBack false — minYear | year=min | false |
| 3 | canGoBack true — minYear 초과 | year=min+1 | true |
| 4 | canGoForward false — maxYear | year=max | false |
| 5 | canGoForward true — max 미만 | year=2026 | true |
| 6 | goBack displayedYear 감소 | year=2026 | 2025 |
| 7 | goBack selectedDate year 감소 | year=2026, date=2026-06-11 | date.year=2025 |
| 8 | goBack 월/일 보존 | date=2026-06-11 | month=6, day=11 |
| 9 | goBack at minYear 유지 | year=min | min |
| 10 | goForward displayedYear 증가 | year=2026 | 2027 |
| 11 | goForward at maxYear 유지 | year=max | max |
| 12 | 윤년 2월29일→비윤년 → 2월28일 조정 | 2024-02-29 → goForward | 2025-02-28 |

---

## 실행 명령

```bash
make test
```
