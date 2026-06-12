# impl/9 시험 시나리오

## 대상 모듈
- `logic/OverdueChecker.kt` — TODO 이벤트 기한 초과 판정 순수 객체
- `ui/notification/NotificationState.kt` — 알림 권한+활성화 상태 data class

---

## OverdueCheckerTest (13개)

| # | 시나리오 | 입력 | 기대 결과 |
|---|---------|------|----------|
| 1 | DIARY 모드 → 항상 false | mode=DIARY, notes="- [ ] task", past | false |
| 2 | PERSONAL_EVENT 모드 → 항상 false | mode=PERSONAL_EVENT, notes="- [ ] task", past | false |
| 3 | TODO + 과거 + 미완료 todo → overdue | past, "- [ ] do something" | true |
| 4 | TODO + 과거 + 완료 todo → not overdue | past, "- [x] done" | false |
| 5 | TODO + 과거 + null notes → not overdue | past, notes=null | false |
| 6 | TODO + 과거 + empty notes → not overdue | past, notes="" | false |
| 7 | TODO + startTime == now → not overdue (경계) | startTime=now | false |
| 8 | TODO + 미래 이벤트 → not overdue | startTime=now+1min | false |
| 9 | TODO + 1초 전 → overdue | startTime=now-1sec | true |
| 10 | TODO + 종일 이벤트 → 항상 false | isAllDay=true, past | false |
| 11 | TODO + 혼합(완료+미완료) → overdue | "- [x] done\n- [ ] pending" | true |
| 12 | TODO + 일반 메모(마커 없음) → not overdue | "just a plain note" | false |
| 13 | TODO + 30일 전 미완료 → overdue | startTime=now-30days | true |

---

## NotificationStateTest (11개)

| # | 시나리오 | 입력 | 기대 결과 |
|---|---------|------|----------|
| 1 | 기본값 notificationsEnabled | default | false |
| 2 | 기본값 hasPermission | default | false |
| 3 | 기본값 isActive | default | false |
| 4 | shouldRequestPermission — 비활성화 시 false | enabled=false | false |
| 5 | shouldRequestPermission — 권한 있을 때 false | enabled=true, hasPermission=true, Android13+ | false |
| 6 | shouldRequestPermission — true 조건 (Android13+, 권한 없음) | enabled=true, hasPermission=false, requiresRuntime=true | true |
| 7 | shouldRequestPermission — pre-Android13 권한 없어도 false | enabled=true, hasPermission=false, requiresRuntime=false | false |
| 8 | isActive — 비활성화 시 false | enabled=false, hasPermission=true | false |
| 9 | isActive — 활성화+권한 있음 → true | enabled=true, hasPermission=true | true |
| 10 | isActive — pre-Android13 권한 없어도 active | enabled=true, hasPermission=false, requiresRuntime=false | true |
| 11 | canSchedule == isActive | active=true / inactive=false | 각각 true/false |

---

## 실행 명령

```bash
make test
# = ./gradlew testDevDebugUnitTest
```
