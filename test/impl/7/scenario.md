# impl/7 Test Scenario — TemplateState, WiseCalTheme, TemplateStore 연결

## 대상 모듈
- `ui/template/TemplateState.kt`
- `ui/theme/WiseCalTheme.kt`
- `ui/navigation/WiseCalNavGraph.kt` (TemplateStore 연결)
- `MainActivity.kt` (WiseCalTheme + TemplateStore)

## 시험 분류
Unit 시험 (JVM, 순수 상태 로직)

---

## TemplateStateTest (13 케이스)

| # | 케이스 | 입력 | 기대 |
|---|--------|------|------|
| 1 | 초기 templates | `TemplateState()` | `templates.isEmpty()` |
| 2 | 초기 selectedCategory | `TemplateState()` | `null` |
| 3 | hasTemplates(비어있음) | `templates=[]` | `false` |
| 4 | hasTemplates(있음) | `templates=[med1]` | `true` |
| 5 | filteredTemplates(카테고리 null) | `selectedCategory=null` | 전체 반환 |
| 6 | filteredTemplates(MEDICATION) | `selectedCategory=MEDICATION` | 의약 템플릿만 |
| 7 | filteredTemplates(APPOINTMENT) | `selectedCategory=APPOINTMENT` | 약속 템플릿만 |
| 8 | filteredTemplates(매칭 없음) | `FIXED 템플릿 없음` | 빈 리스트 |
| 9 | filteredTemplates 순서 유지 | `[med2, med1]` | 동일 순서 |
| 10 | filteredTemplates copy 시 업데이트 | `copy(selectedCategory=APPOINTMENT)` | 필터 적용 |
| 11 | categoryGroups(비어있음) | `templates=[]` | `{}` |
| 12 | categoryGroups MEDICATION 키 | `[med1, med2, apt1]` | `MEDICATION → [med1, med2]` |
| 13 | categoryGroups 키 수 | `[med, apt, fix]` 각 1개 | `size==3` |

---

## 실행 명령
```bash
./gradlew testDevDebugUnitTest --tests "com.wisecal.android.ui.template.*"
```
