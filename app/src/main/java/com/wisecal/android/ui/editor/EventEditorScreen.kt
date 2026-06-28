package com.wisecal.android.ui.editor

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.wisecal.android.BuildConfig
import com.wisecal.android.model.EventTemplate
import com.wisecal.android.model.PlannerMode
import java.io.File

/**
 * 이벤트 추가/편집 전체 화면.
 * iOS NavigationStack + EventEditorView 대응.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventEditorScreen(
    viewModel: EventEditorViewModel,
    templates: List<EventTemplate>,
    defaultCalendarId: Long,
    onDismiss: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val s = state ?: return
    val context = LocalContext.current

    // 카메라 촬영용 임시 URI (촬영 시마다 갱신)
    var cameraUri by remember { mutableStateOf<Uri?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri -> uri?.let { viewModel.update { st -> st.copy(photoUri = it) } } }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) cameraUri?.let { viewModel.update { st -> st.copy(photoUri = it) } }
    }

    fun launchCamera() {
        val file = File(context.cacheDir, "cam_${System.currentTimeMillis()}.jpg")
        val uri = FileProvider.getUriForFile(context, "${BuildConfig.APPLICATION_ID}.fileprovider", file)
        cameraUri = uri
        cameraLauncher.launch(uri)
    }

    LaunchedEffect(viewModel.result) {
        viewModel.result.collect { result ->
            if (result is EditorResult.Success) onDismiss()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("${if (s.isEditMode) "편집" else "새"} ${s.mode.title}") },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "닫기")
                    }
                },
                actions = {
                    TextButton(
                        onClick = { viewModel.save(defaultCalendarId) },
                        enabled = s.canSave,
                    ) {
                        Text("저장")
                    }
                },
            )
        },
    ) { padding ->
        // DIARY 모드: 화면 전체를 채워야 하므로 verticalScroll 제거 + fillMaxSize.
        // 나머지 모드: 항목이 늘어날 수 있으므로 verticalScroll 유지.
        val scrollModifier = if (s.mode != PlannerMode.DIARY)
            Modifier.verticalScroll(rememberScrollState()) else Modifier
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(vertical = 8.dp)
                .fillMaxSize()
                .then(scrollModifier),
        ) {
            when (s.mode) {
                PlannerMode.DIARY -> DiaryEditorSection(
                    state = s,
                    onTitleChange = { v -> viewModel.update { st -> st.copy(title = v) } },
                    onBodyChange  = { v -> viewModel.update { st -> st.copy(diaryBody = v) } },
                    modifier = Modifier.weight(1f),
                )
                PlannerMode.TODO -> TodoEditorSection(
                    state = s,
                    onTitleChange       = { v  -> viewModel.update { st -> st.copy(title = v) } },
                    onTodosChange       = { v  -> viewModel.update { st -> st.copy(todos = v) } },
                    onDueDateTimeChange = { v  -> viewModel.update { st -> st.copy(dueDateTime = v) } },
                )
                PlannerMode.PERSONAL_EVENT -> PersonalEventEditorSection(
                    state = s,
                    templates = templates,
                    onCategoryChange  = { cat -> viewModel.update { st -> st.copy(selectedCategory = cat, selectedTemplate = null) } },
                    onTemplateSelect  = { tmpl ->
                        val start = s.date.atTime(tmpl.defaultStartHour, 0)
                        val end   = if (tmpl.defaultDurationMinutes > 0)
                            start.plusMinutes(tmpl.defaultDurationMinutes.toLong())
                        else start
                        viewModel.update { st -> st.copy(
                            selectedTemplate = tmpl,
                            title = tmpl.fixedDisplayTitle ?: tmpl.titleFormat,
                            startTime = start,
                            endTime = end,
                        ) }
                    },
                    onTitleChange     = { v -> viewModel.update { st -> st.copy(title = v) } },
                    onStartTimeChange = { v -> viewModel.update { st -> st.copy(startTime = v) } },
                    onEndTimeChange   = { v -> viewModel.update { st -> st.copy(endTime = v) } },
                    onLocationChange  = { v -> viewModel.update { st -> st.copy(location = v) } },
                    onRepeatToggle                  = { v -> viewModel.update { st -> st.copy(repeatEnabled = v) } },
                    onIndividualRegistrationChange  = { v -> viewModel.update { st -> st.copy(individualRegistration = v) } },
                    onRepeatFrequencyChange         = { v -> viewModel.update { st -> st.copy(repeatFrequency = v) } },
                    onRepeatDaysOfWeekChange    = { v -> viewModel.update { st -> st.copy(repeatDaysOfWeek = v) } },
                    onRepeatStartDateChange     = { v -> viewModel.update { st -> st.copy(repeatStartDate = v) } },
                    onRepeatEndDateChange       = { v -> viewModel.update { st -> st.copy(repeatEndDate = v) } },
                    onMemoChange                = { v -> viewModel.update { st -> st.copy(memo = v) } },
                    onMedicationPresetChange    = { v -> viewModel.update { st -> st.copy(medicationPreset = v) } },
                    onPickPhotoFromGallery      = { galleryLauncher.launch(androidx.activity.result.PickVisualMediaRequest()) },
                    onTakePhoto                 = { launchCamera() },
                    onRemovePhoto               = { viewModel.update { st -> st.copy(photoUri = null) } },
                )
            }
        }
    }
}
