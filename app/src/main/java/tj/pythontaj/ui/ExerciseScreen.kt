package tj.pythontaj.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tj.pythontaj.MainViewModel
import tj.pythontaj.domain.VerdictKind

@Composable
fun ExerciseScreen(vm: MainViewModel, exerciseId: String, onBack: () -> Unit) {
    // Ҳолати охирини машқи пештара тоза мекунем.
    DisposableEffect(exerciseId) {
        vm.resetExercise()
        onDispose { vm.resetExercise() }
    }
    ContentGate(vm) { cur ->
        val ex = cur.exercise(exerciseId)
        val lesson = cur.lessonOfExercise(exerciseId)
        ScreenScaffold(ex?.title ?: T.exercises, onBack = onBack) { pad ->
            if (ex == null || lesson == null) {
                Text(T.nothingFound, Modifier.padding(pad).padding(20.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                return@ScreenScaffold
            }
            var code by rememberSaveable(ex.id) { mutableStateOf(ex.starterCode) }
            var hintsShown by rememberSaveable(ex.id) { mutableStateOf(0) }
            var showSolution by rememberSaveable(ex.id) { mutableStateOf(false) }
            val state by vm.exercise.collectAsState()
            val attempts by remember(ex.id) { vm.attempts(ex.id) }.collectAsState(initial = emptyList())
            val passedBefore = attempts.any { it.passed }

            Column(
                Modifier.padding(pad).fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                AppCard {
                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        DifficultyDots(ex.difficulty)
                        Pill("${T.attempts}: ${attempts.size}", Palette.AccentAlt)
                        if (passedBefore) Pill("✓ ${T.passed}", Palette.Success)
                    }
                    Spacer(Modifier.height(10.dp))
                    Text(ex.prompt, style = MaterialTheme.typography.bodyLarge)
                }
                CodeEditor(
                    code, { code = it },
                    headerActions = { TextButton(onClick = { code = ex.starterCode }) { Text(T.reset, color = MaterialTheme.colorScheme.onSurfaceVariant) } },
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    GradientButton(
                        T.check,
                        onClick = { vm.submitExercise(ex, lesson.id, code) },
                        enabled = !state.console.running,
                        icon = Icons.Filled.Check,
                        modifier = Modifier.weight(1.3f),
                    )
                    if (state.console.running) {
                        SoftButton(T.stop, onClick = { vm.stopExercise() }, color = Palette.Error, modifier = Modifier.weight(1f))
                    } else {
                        SoftButton(T.run, onClick = { vm.runExercise(code) }, modifier = Modifier.weight(1f))
                    }
                }
                state.verdict?.let { v ->
                    val ok = v.kind == VerdictKind.CORRECT
                    VerdictCard(ok, if (ok) T.correct else T.wrong, v.message)
                }
                ConsoleView(state.console)

                if (hintsShown < ex.hints.size) {
                    SoftButton("💡 ${T.hint} (${hintsShown + 1}/${ex.hints.size})", onClick = { hintsShown++ }, color = Palette.Warning, modifier = Modifier.fillMaxWidth())
                }
                ex.hints.take(hintsShown).forEach { hint ->
                    Callout(color = Palette.Warning) { Text("💡 $hint", style = MaterialTheme.typography.bodyMedium) }
                }

                val failed = attempts.count { !it.passed }
                if (!showSolution && (passedBefore || failed >= 2)) {
                    SoftButton(T.solution, onClick = { showSolution = true }, modifier = Modifier.fillMaxWidth())
                }
                if (showSolution) {
                    AppCard {
                        Text(T.solution, style = MaterialTheme.typography.titleSmall, color = Palette.Accent)
                        Spacer(Modifier.height(8.dp))
                        CodeBlock(ex.solution)
                        Spacer(Modifier.height(10.dp))
                        Text(ex.explanation, style = MaterialTheme.typography.bodyMedium)
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}
