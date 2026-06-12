package com.wisecal.android.data

import java.io.File

/**
 * 복용 이벤트 사진 첨부 파일 경로/이름 규칙.
 * PRD §3.4-C: filesDir JPEG 저장, 외부 저장소 미사용.
 * 파일명: `event_{eventId}_{timestampMillis}.jpg`
 */
object EventImageStore {

    private const val EXTENSION = ".jpg"
    private const val PREFIX = "event_"

    fun fileName(eventId: Long, timestampMillis: Long): String =
        "${PREFIX}${eventId}_${timestampMillis}${EXTENSION}"

    fun file(filesDir: File, eventId: Long, timestampMillis: Long): File =
        File(filesDir, fileName(eventId, timestampMillis))

    /** 파일명에서 eventId 추출. 형식 불일치 시 null. */
    fun extractEventId(fileName: String): Long? {
        if (!isEventImage(fileName)) return null
        val body = fileName.removePrefix(PREFIX).removeSuffix(EXTENSION)
        return body.substringBefore("_").toLongOrNull()
    }

    fun isEventImage(fileName: String): Boolean =
        fileName.startsWith(PREFIX) && fileName.endsWith(EXTENSION)
}
