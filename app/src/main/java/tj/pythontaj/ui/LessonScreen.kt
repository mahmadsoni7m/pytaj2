package tj.pythontaj.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tj.pythontaj.MainViewModel

@Composable
fun LessonScreen(
    vm: MainViewModel,
    lessonId: String,
    onBack: () -> Unit,
    onOpenExercise: (String) -> Unit,
    onOpenQuiz: (String) -> Unit,
    onOpenEditor: () -> Unit,
) {
    ContentGate(vm) { cur ->
        val lesson = cur.lesson(lessonId)
        val marks by vm.bookmarks.collectAsState()
        val done by vm.completedLessons.collectAsState()
        val passed by vm.passedExercises.collectAsState()
        ScreenScaffold(
            lesson?.title ?: T.path,
            onBack = onBack,
            actions = {
                if (lesson != null) {
                    val marked = lesson.id in marks
                    IconButton(onClick = { vm.toggleBookmark(lesson.id) }) {
                        Icon(
                            if (marked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = T.bookmark,
                            tint = if (marked) Palette.Error else MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            },
        ) { pad ->
            if (lesson == null) {
                Text(T.nothingFound, Modifier.padding(pad).padding(20.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                return@ScreenScaffold
            }
            LazyColumn(
                Modifier.padding(pad).padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 28.dp),
            ) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        DifficultyDots(lesson.difficulty)
                        Pill("${T.exercises}: ${lesson.exercises.size}", Palette.AccentAlt)
                        if (lesson.id in done) Pill("✓ ${T.lessonDone}", Palette.Success)
                    }
                    Spacer(Modifier.height(10.dp))
                    Text(lesson.introduction, style = MaterialTheme.typography.bodyLarge)
                    Spacer(Modifier.height(12.dp))
                    AppCard(accent = Palette.Accent) {
                        Text(T.objectives, style = MaterialTheme.typography.titleSmall, color = Palette.Accent)
                        Spacer(Modifier.height(8.dp))
                        BulletList(lesson.objectives)
                    }
                    if (lesson.prerequisites.isNotEmpty()) {
                        SectionTitle(T.prerequisites)
                        BulletList(lesson.prerequisites.map { cur.lesson(it)?.title ?: it }, Palette.AccentAlt)
                    }
                }
                lesson.sections.forEach { s ->
                    item {
                        SectionTitle(s.heading)
                        Text(s.body, style = MaterialTheme.typography.bodyLarge)
                    }
                }
                item { SectionTitle(T.examples) }
                lesson.examples.forEach { e ->
                    item {
                        AppCard {
                            Text(e.title, style = MaterialTheme.typography.titleSmall)
                            Spacer(Modifier.height(10.dp))
                            CodeBlock(e.code)
                            Spacer(Modifier.height(10.dp))
                            Text(e.explanation, style = MaterialTheme.typography.bodyMedium)
                            Spacer(Modifier.height(12.dp))
                            Text(T.expectedOutput, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.height(6.dp))
                            CodeBlock(e.expectedOutput, highlight = false)
                            Spacer(Modifier.height(8.dp))
                            GradientButton(
                                T.openInEditor,
                                onClick = { vm.editorCode.value = e.code; onOpenEditor() },
                                modifier = Modifier.fillMaxWidth(),
                                icon = Icons.Filled.PlayArrow,
                            )
                        }
                    }
                }
                item {
                    if (lesson.commonMistakes.isNotEmpty()) {
                        SectionTitle(T.commonMistakes)
                        Callout(color = Palette.Warning, icon = Icons.Filled.Warning) { BulletList(lesson.commonMistakes, Palette.Warning) }
                    }
                    SectionTitle(T.summary)
                    Text(lesson.summary, style = MaterialTheme.typography.bodyLarge)
                    SectionTitle(T.completion)
                    Callout(color = Palette.Success, icon = Icons.Filled.CheckCircle) { Text(lesson.completionCriteria, style = MaterialTheme.typography.bodyMedium) }
                    if (lesson.exercises.isNotEmpty()) SectionTitle(T.exercises)
                }
                lesson.exercises.forEach { ex ->
                    item {
                        val ok = ex.id in passed
                        AppCard(Modifier.clickable { onOpenExercise(ex.id) }, accent = if (ok) Palette.Success else null) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (ok) {
                                    BadgeCircle(size = 36.dp, filled = false) { Icon(Icons.Filled.Check, contentDescription = T.passed, tint = Palette.Success) }
                                } else {
                                    BadgeCircle(size = 36.dp) { Icon(Icons.Filled.PlayArrow, contentDescription = null, tint = androidx.compose.ui.graphics.Color(0xFF00201C)) }
                                }
                                Spacer(Modifier.width(12.dp))
                                Text(ex.title, style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
                                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
                item {
                    Spacer(Modifier.height(8.dp))
                    if (lesson.quiz.isNotEmpty()) {
                        SoftButton(T.quiz, onClick = { onOpenQuiz(lesson.id) }, modifier = Modifier.fillMaxWidth(), color = Palette.AccentAlt)
                    }
                    if (lesson.exercises.isEmpty() && lesson.id !in done) {
                        Spacer(Modifier.height(10.dp))
                        GradientButton(T.finish, onClick = { vm.completeLesson(lesson.id) }, modifier = Modifier.fillMaxWidth(), icon = Icons.Filled.Check)
                    }
                }
            }
        }
    }
}
