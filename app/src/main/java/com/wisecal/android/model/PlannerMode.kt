package com.wisecal.android.model

enum class PlannerMode(val title: String, val detail: String) {
    DIARY("일기", "이벤트 추가 시 일기장처럼 전체 화면으로 자유롭게 작성합니다."),
    TODO("일정관리", "체크리스트로 작성하고 완료 여부를 토글할 수 있습니다."),
    PERSONAL_EVENT("개인 이벤트", "복용·약속 등 템플릿을 선택해 빠르게 입력합니다.");

    companion object {
        fun fromString(value: String): PlannerMode =
            entries.firstOrNull { it.name == value } ?: TODO
    }
}
