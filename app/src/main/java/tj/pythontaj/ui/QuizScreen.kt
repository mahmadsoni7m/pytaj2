package tj.pythontaj.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import tj.pythontaj.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(vm: MainViewModel, lessonId: String, onBack: () -> Unit) {
    ContentGate(vm) { cur ->
        val lesson = cur.lesson(lessonId)
        ScreenScaffold(T.quiz, onBack = onBack) { pad ->
            if (lesson == null || lesson.quiz.isEmpty()) {
                Text(T.nothingFound, Modifier.padding(pad).padding(20.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                return@ScreenScaffold
            }
            val questions = lesson.quiz
            var index by rememberSaveable(lessonId) { mutableStateOf(0) }
            var selected by rememberSaveable(lessonId) { mutableStateOf(-1) }
            var score by rememberSaveable(lessonId) { mutableStateOf(0) }
            var finished by rememberSaveable(lessonId) { mutableStateOf(false) }

            Column(
                Modifier.padding(pad).fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                if (finished) {
                    LaunchedEffect(lessonId) { vm.recordQuiz(lessonId, score, questions.size) }
                    val pct = score * 100 / questions.size
                    AppCard(accent = if (pct >= 80) Palette.Success else Palette.Accent) {
                        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(T.quizResult, style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(16.dp))
                            ProgressRing(pct, size = 132.dp, stroke = 11.dp, label = "$score/${questions.size}")
                            Spacer(Modifier.height(16.dp))
                            Text(
                                when {
                                    pct == 100 -> T.quizPerfect
                                    pct >= 60 -> T.quizGood
                                    else -> T.quizTry
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                    GradientButton(T.back, onClick = onBack, modifier = Modifier.fillMaxWidth())
                } else {
                    val q = questions[index]
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("${index + 1} / ${questions.size}", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.width(12.dp))
                        ProgressBar((index + if (selected >= 0) 1 else 0) / questions.size.toFloat(), Modifier.weight(1f), height = 6.dp)
                    }
                    Text(q.question, style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(2.dp))
                    val answered = selected >= 0
                    q.options.forEachIndexed { i, option ->
                        val tint = when {
                            !answered -> null
                            i == q.answerIndex -> Palette.Success
                            i == selected -> Palette.Error
                            else -> null
                        }
                        val shape = RoundedCornerShape(16.dp)
                        Card(
                            onClick = { if (!answered) { selected = i; if (i == q.answerIndex) score++ } },
                            shape = shape,
                            colors = CardDefaults.cardColors(containerColor = tint?.copy(alpha = 0.14f) ?: Palette.Card),
                            border = BorderStroke(1.dp, tint?.copy(alpha = 0.6f) ?: Palette.Outline),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                BadgeCircle(size = 32.dp, filled = tint != null && i == q.answerIndex) {
                                    Text(
                                        ('A' + i).toString(),
                                        fontWeight = FontWeight.Bold,
                                        color = if (tint != null && i == q.answerIndex) Color(0xFF00201C) else MaterialTheme.colorScheme.onSurface,
                                    )
                                }
                                Spacer(Modifier.width(12.dp))
                                Text(option, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                            }
                        }
                    }
                    if (answered) {
                        val ok = selected == q.answerIndex
                        Callout(color = if (ok) Palette.Success else Palette.Error, icon = if (ok) Icons.Filled.CheckCircle else Icons.Filled.Warning) {
                            Text(if (ok) T.correct else T.wrong, style = MaterialTheme.typography.titleSmall, color = if (ok) Palette.Success else Palette.Error)
                            Text(q.explanation, style = MaterialTheme.typography.bodyMedium)
                        }
                        GradientButton(
                            if (index + 1 < questions.size) T.next else T.finish,
                            onClick = { if (index + 1 < questions.size) { index++; selected = -1 } else finished = true },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}
