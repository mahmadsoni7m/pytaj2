package tj.pythontaj.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import tj.pythontaj.MainViewModel
import tj.pythontaj.domain.Lesson

@Composable
fun LessonRow(l: Lesson, done: Boolean, bookmarked: Boolean, onClick: () -> Unit) {
    AppCard(Modifier.clickable(onClick = onClick), accent = if (done) Palette.Success else null) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (done) {
                BadgeCircle(size = 40.dp, filled = false) { Icon(Icons.Filled.Check, contentDescription = T.lessonDone, tint = Palette.Success) }
            } else {
                BadgeCircle(size = 40.dp) { Text(l.id.takeLast(2).trimStart('0').ifEmpty { "0" }, color = Color(0xFF00201C), fontWeight = FontWeight.Bold) }
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(l.title, style = MaterialTheme.typography.titleSmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    DifficultyDots(l.difficulty)
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "${T.exercises}: ${l.exercises.size}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            if (bookmarked) {
                Icon(Icons.Filled.Star, contentDescription = T.bookmark, tint = Palette.Warning, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(6.dp))
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun PathScreen(vm: MainViewModel, onOpenModule: (String) -> Unit, onOpenLesson: (String) -> Unit) {
    ScreenScaffold(T.path) { pad ->
        ContentGate(vm, Modifier.padding(pad)) { cur ->
            val done by vm.completedLessons.collectAsState()
            val marks by vm.bookmarks.collectAsState()
            var query by rememberSaveable { mutableStateOf("") }
            Column(Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    placeholder = { Text(T.search) },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Palette.Accent,
                        unfocusedBorderColor = Palette.Outline,
                        focusedContainerColor = Palette.Card,
                        unfocusedContainerColor = Palette.Card,
                        cursorColor = Palette.Accent,
                    ),
                )
                Spacer(Modifier.height(12.dp))
                if (query.isNotBlank()) {
                    val found = cur.search(query)
                    if (found.isEmpty()) Text(T.nothingFound, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(bottom = 20.dp)) {
                        items(found, key = { it.id }) { l -> LessonRow(l, l.id in done, l.id in marks) { onOpenLesson(l.id) } }
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(bottom = 20.dp)) {
                        items(cur.modules.sortedBy { it.order }, key = { it.id }) { m ->
                            val ls = cur.lessonsOf(m.id)
                            val doneN = ls.count { it.id in done }
                            AppCard(Modifier.clickable { onOpenModule(m.id) }, accent = if (ls.isNotEmpty() && doneN == ls.size) Palette.Success else null) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    BadgeCircle(size = 44.dp) { Text("${m.order}", color = Color(0xFF00201C), fontWeight = FontWeight.ExtraBold) }
                                    Spacer(Modifier.width(14.dp))
                                    Column(Modifier.weight(1f)) {
                                        Text(m.title, style = MaterialTheme.typography.titleSmall)
                                        Text(m.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                    }
                                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Spacer(Modifier.height(12.dp))
                                ProgressBar(if (ls.isEmpty()) 0f else doneN / ls.size.toFloat(), height = 6.dp)
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    if (ls.isEmpty()) "Дарсҳо ҳанӯз нестанд" else "$doneN/${ls.size} дарс анҷом ёфт",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ModuleScreen(vm: MainViewModel, moduleId: String, onBack: () -> Unit, onOpenLesson: (String) -> Unit) {
    ContentGate(vm) { cur ->
        val module = cur.modules.firstOrNull { it.id == moduleId }
        val done by vm.completedLessons.collectAsState()
        val marks by vm.bookmarks.collectAsState()
        ScreenScaffold(module?.title ?: T.path, onBack = onBack) { pad ->
            val lessons = cur.lessonsOf(moduleId)
            if (lessons.isEmpty()) {
                Text(T.noLessonsInModule, Modifier.padding(pad).padding(20.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                LazyColumn(
                    Modifier.padding(pad).padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 20.dp),
                ) {
                    item {
                        Text(module?.description.orEmpty(), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 6.dp))
                        ProgressBar(lessons.count { it.id in done } / lessons.size.toFloat())
                        Spacer(Modifier.height(6.dp))
                    }
                    items(lessons, key = { it.id }) { l -> LessonRow(l, l.id in done, l.id in marks) { onOpenLesson(l.id) } }
                }
            }
        }
    }
}
