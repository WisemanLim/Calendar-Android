package com.wisecal.android.ui.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wisecal.android.model.CalendarEvent
import java.time.format.DateTimeFormatter
import java.util.Locale

private val dateFormatter = DateTimeFormatter.ofPattern("yyyy.M.d (E)", Locale.KOREAN)
private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.KOREAN)

/**
 * 이벤트 검색 화면.
 * 제목 키워드 기반. 검색어 2글자 이상부터 검색.
 * iOS searchEvents 대응.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onEventClick: (CalendarEvent) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsState()

    Column(modifier = modifier.fillMaxSize()) {
        SearchBar(
            query = state.query,
            onQueryChange = viewModel::onQueryChange,
            onClear = viewModel::clearQuery,
            onBack = onBack,
        )

        when {
            state.isSearching -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            !state.hasQuery -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("검색어를 입력하세요 (2자 이상)",
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            state.displayResults.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("검색 결과 없음", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            else -> {
                LazyColumn {
                    items(state.displayResults, key = { it.id }) { event ->
                        SearchResultRow(event = event, onClick = { onEventClick(event) })
                        HorizontalDivider(thickness = 0.5.dp)
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
    onBack: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.Default.Search, contentDescription = "검색")
        }
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text("이벤트 검색") },
            singleLine = true,
            modifier = Modifier.weight(1f),
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = onClear) {
                        Icon(Icons.Default.Close, contentDescription = "지우기")
                    }
                }
            },
        )
    }
}

@Composable
private fun SearchResultRow(event: CalendarEvent, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(event.title, style = MaterialTheme.typography.bodyMedium)
            Text(
                buildString {
                    append(event.startTime.format(dateFormatter))
                    if (!event.isAllDay) append(" ${event.startTime.format(timeFormatter)}")
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
