package tj.pythontaj.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tj.pythontaj.ConsoleState
import tj.pythontaj.ContentState
import tj.pythontaj.MainViewModel
import tj.pythontaj.content.Curriculum
import tj.pythontaj.domain.RunStatus

// ───────────────────────── Фон ва сохтор ─────────────────────────

/** Фони умумӣ: ранги торик бо дурахши мулоим дар кунҷи болоӣ. */
@Composable
fun ScreenBackground(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier
            .fillMaxSize()
            .background(Palette.Background)
            .drawBehind {
                drawRect(
                    Brush.radialGradient(
                        colors = listOf(Palette.Accent.copy(alpha = 0.13f), Color.Transparent),
                        center = Offset(size.width * 0.9f, 380f),
                        radius = 640f,
                    ),
                )
                drawRect(
                    Brush.radialGradient(
                        colors = listOf(Palette.AccentAlt.copy(alpha = 0.10f), Color.Transparent),
                        center = Offset(0f, size.height * 0.85f),
                        radius = 620f,
                    ),
                )
            },
        content = content,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenScaffold(
    title: String,
    onBack: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    ScreenBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text(title, maxLines = 1, overflow = TextOverflow.Ellipsis, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        if (onBack != null) {
                            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = T.back) }
                        }
                    },
                    actions = actions,
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Transparent,
                    ),
                )
            },
            content = content,
        )
    }
}

/** Ҳолатҳои боркунӣ / хато / тайёр барои мазмуни курс. */
@Composable
fun ContentGate(vm: MainViewModel, modifier: Modifier = Modifier, ready: @Composable (Curriculum) -> Unit) {
    val state by vm.content.collectAsState()
    when (val s = state) {
        is ContentState.Loading -> Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = Palette.Accent, strokeWidth = 3.dp)
                Spacer(Modifier.height(14.dp))
                Text(T.loading, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        is ContentState.Error -> Column(
            modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Callout(color = Palette.Error, icon = Icons.Filled.Warning) { Text(s.message) }
            Spacer(Modifier.height(16.dp))
            GradientButton(T.retry, onClick = { vm.loadContent() }, modifier = Modifier.fillMaxWidth())
        }
        is ContentState.Ready -> Box(modifier) { ready(s.curriculum) }
    }
}

// ───────────────────────── Корт ва матн ─────────────────────────

@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    accent: Color? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape = RoundedCornerShape(20.dp)
    val border = if (accent != null) accent.copy(alpha = 0.45f) else Palette.Outline
    val container = if (accent != null) accent.copy(alpha = 0.08f).compositeOver(Palette.Card) else Palette.Card
    Column(
        modifier
            .fillMaxWidth()
            .clip(shape)
            .background(container)
            .border(1.dp, border, shape)
            .padding(16.dp),
        content = content,
    )
}

private fun Color.compositeOver(background: Color): Color {
    val a = alpha
    return Color(
        red = red * a + background.red * (1 - a),
        green = green * a + background.green * (1 - a),
        blue = blue * a + background.blue * (1 - a),
        alpha = 1f,
    )
}

@Composable
fun SectionTitle(text: String) {
    Row(Modifier.padding(top = 18.dp, bottom = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.width(4.dp).height(18.dp).clip(RoundedCornerShape(2.dp)).background(Palette.Gradient))
        Spacer(Modifier.width(10.dp))
        Text(text, style = MaterialTheme.typography.titleMedium)
    }
}

/** Блоки ранги хабар: хато, маслиҳат, натиҷа. */
@Composable
fun Callout(
    color: Color,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape = RoundedCornerShape(16.dp)
    Row(
        modifier
            .fillMaxWidth()
            .clip(shape)
            .background(color.copy(alpha = 0.12f))
            .border(1.dp, color.copy(alpha = 0.35f), shape)
            .padding(14.dp),
        verticalAlignment = Alignment.Top,
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(10.dp))
        }
        Column(Modifier.weight(1f), content = content)
    }
}

@Composable
fun BulletList(items: List<String>, color: Color = Palette.Accent) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items.forEach {
            Row(verticalAlignment = Alignment.Top) {
                Box(Modifier.padding(top = 9.dp).size(6.dp).background(color, CircleShape))
                Spacer(Modifier.width(12.dp))
                Text(it, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun Pill(text: String, color: Color = Palette.Accent, modifier: Modifier = Modifier) {
    Text(
        text,
        style = MaterialTheme.typography.labelMedium,
        color = color,
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.14f))
            .padding(horizontal = 10.dp, vertical = 4.dp),
    )
}

@Composable
fun DifficultyDots(level: Int) {
    val max = maxOf(3, level)
    Row(horizontalArrangement = Arrangement.spacedBy(3.dp), verticalAlignment = Alignment.CenterVertically) {
        for (i in 1..max) {
            Box(
                Modifier.size(7.dp).background(
                    if (i <= level) Palette.Warning else Palette.Outline,
                    CircleShape,
                ),
            )
        }
    }
}

@Composable
fun BadgeCircle(modifier: Modifier = Modifier, size: Dp = 40.dp, filled: Boolean = true, content: @Composable BoxScope.() -> Unit) {
    val base = modifier.size(size).clip(CircleShape)
    Box(
        if (filled) base.background(Palette.Gradient) else base.background(Palette.CardHigh).border(1.dp, Palette.Outline, CircleShape),
        contentAlignment = Alignment.Center,
        content = content,
    )
}

// ───────────────────────── Тугмаҳо ─────────────────────────

@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
) {
    val shape = RoundedCornerShape(16.dp)
    val bg = if (enabled) Modifier.background(Palette.Gradient) else Modifier.background(Palette.CardHigh)
    Row(
        modifier
            .heightIn(min = 52.dp)
            .clip(shape)
            .then(bg)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val tint = if (enabled) Color(0xFF00201C) else MaterialTheme.colorScheme.onSurfaceVariant
        if (icon != null) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(text, color = tint, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
fun SoftButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    color: Color = MaterialTheme.colorScheme.onSurface,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.heightIn(min = 48.dp),
        enabled = enabled,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, if (enabled) Palette.Outline else Palette.Outline.copy(alpha = 0.5f)),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = color),
    ) { Text(text, maxLines = 1, overflow = TextOverflow.Ellipsis) }
}

// ───────────────────────── Пешрафт ─────────────────────────

@Composable
fun ProgressRing(percent: Int, modifier: Modifier = Modifier, size: Dp = 96.dp, stroke: Dp = 9.dp, label: String? = null) {
    val target = percent.coerceIn(0, 100) / 100f
    val sweep by animateFloatAsState(target, tween(900), label = "ring")
    Box(modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val px = stroke.toPx()
            val d = this.size.minDimension - px
            val topLeft = Offset(px / 2, px / 2)
            drawArc(Palette.Outline, 0f, 360f, false, topLeft, Size(d, d), style = Stroke(px, cap = StrokeCap.Round))
            if (sweep > 0f) {
                drawArc(
                    Brush.sweepGradient(listOf(Palette.Accent, Palette.AccentAlt, Palette.Accent)),
                    -90f, 360f * sweep, false, topLeft, Size(d, d), style = Stroke(px, cap = StrokeCap.Round),
                )
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label ?: "$percent%", style = MaterialTheme.typography.titleLarge)
        }
    }
}

@Composable
fun ProgressBar(fraction: Float, modifier: Modifier = Modifier, height: Dp = 8.dp) {
    val f by animateFloatAsState(fraction.coerceIn(0f, 1f), tween(700), label = "bar")
    Box(modifier.fillMaxWidth().height(height).clip(RoundedCornerShape(50)).background(Palette.Outline)) {
        if (f > 0f) Box(Modifier.fillMaxWidth(f).fillMaxHeight().clip(RoundedCornerShape(50)).background(Palette.Gradient))
    }
}

// ───────────────────────── Код ─────────────────────────

private val pyKeywords = setOf(
    "False", "None", "True", "and", "as", "assert", "async", "await", "break", "class", "continue", "def", "del",
    "elif", "else", "except", "finally", "for", "from", "global", "if", "import", "in", "is", "lambda", "nonlocal",
    "not", "or", "pass", "raise", "return", "try", "while", "with", "yield",
)
private val pyBuiltins = setOf(
    "print", "input", "len", "range", "int", "float", "str", "bool", "list", "dict", "set", "tuple", "sum", "min",
    "max", "abs", "round", "sorted", "enumerate", "zip", "map", "filter", "type", "open", "isinstance", "reversed",
)

/** Ҳайлайти оддии синтаксиси Python (калимаҳои калидӣ, сатр, адад, шарҳ). Дарозии матн тағйир намеёбад. */
fun highlightPython(src: String): AnnotatedString = buildAnnotatedString {
    val n = src.length
    var i = 0
    var prev = ""
    while (i < n) {
        val c = src[i]
        if (c == '#') {
            val nl = src.indexOf('\n', i)
            val e = if (nl < 0) n else nl
            withStyle(SpanStyle(color = Palette.SynComment)) { append(src.substring(i, e)) }
            i = e
        } else if (c == '"' || c == '\'') {
            val triple = src.startsWith("$c$c$c", i)
            var j = if (triple) i + 3 else i + 1
            while (j < n) {
                if (src[j] == '\\') { j += 2; continue }
                if (triple) {
                    if (src.startsWith("$c$c$c", j)) { j += 3; break }
                } else {
                    if (src[j] == c) { j++; break }
                    if (src[j] == '\n') break
                }
                j++
            }
            val e = minOf(j, n)
            withStyle(SpanStyle(color = Palette.SynString)) { append(src.substring(i, e)) }
            i = e
        } else if (c.isDigit()) {
            var j = i
            while (j < n && (src[j].isLetterOrDigit() || src[j] == '.' || src[j] == '_')) j++
            withStyle(SpanStyle(color = Palette.SynNumber)) { append(src.substring(i, j)) }
            i = j
        } else if (c.isLetter() || c == '_') {
            var j = i
            while (j < n && (src[j].isLetterOrDigit() || src[j] == '_')) j++
            val w = src.substring(i, j)
            val color = when {
                w in pyKeywords -> Palette.SynKeyword
                prev == "def" || prev == "class" -> Palette.SynFunc
                w in pyBuiltins -> Palette.SynBuiltin
                else -> null
            }
            if (color != null) withStyle(SpanStyle(color = color)) { append(w) } else append(w)
            prev = w
            i = j
        } else {
            append(c)
            if (!c.isWhitespace()) prev = ""
            i++
        }
    }
}

private val codeStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 14.sp, lineHeight = 21.sp, color = Palette.CodeText)

@Composable
private fun WindowDots() {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        listOf(Palette.Error, Palette.Warning, Palette.Success).forEach {
            Box(Modifier.size(9.dp).background(it.copy(alpha = 0.75f), CircleShape))
        }
    }
}

@Composable
fun CodeBlock(code: String, modifier: Modifier = Modifier, highlight: Boolean = true, title: String? = null) {
    val shape = RoundedCornerShape(14.dp)
    val text = remember(code, highlight) { if (highlight) highlightPython(code) else AnnotatedString(code) }
    Column(modifier.fillMaxWidth().clip(shape).background(Palette.Code).border(1.dp, Palette.Outline, shape)) {
        if (highlight) {
            Row(
                Modifier.fillMaxWidth().background(Palette.Card).padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                WindowDots()
                Spacer(Modifier.width(10.dp))
                Text(title ?: "python", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        SelectionContainer {
            Text(text, style = codeStyle, modifier = Modifier.horizontalScroll(rememberScrollState()).padding(12.dp))
        }
    }
}

@Composable
fun CodeEditor(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    title: String = "main.py",
    headerActions: @Composable RowScope.() -> Unit = {},
) {
    val shape = RoundedCornerShape(16.dp)
    val highlighter = remember {
        VisualTransformation { t -> TransformedText(highlightPython(t.text), OffsetMapping.Identity) }
    }
    Column(modifier.fillMaxWidth().clip(shape).background(Palette.Code).border(1.dp, Palette.Outline, shape)) {
        Row(
            Modifier.fillMaxWidth().background(Palette.Card).padding(start = 12.dp, end = 4.dp).heightIn(min = 40.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            WindowDots()
            Spacer(Modifier.width(10.dp))
            Text(title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
            headerActions()
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth().heightIn(min = 200.dp).padding(14.dp),
            textStyle = codeStyle,
            cursorBrush = SolidColor(Palette.Accent),
            visualTransformation = highlighter,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.None, autoCorrect = false),
        )
    }
}

private fun statusLabel(s: RunStatus): String = when (s) {
    RunStatus.OK -> "Иҷро шуд"
    RunStatus.SYNTAX_ERROR -> "Хатои синтаксис"
    RunStatus.RUNTIME_ERROR -> "Хатои иҷро"
    RunStatus.TIMEOUT -> "Вақт тамом шуд: барнома хеле дароз кор кард"
    RunStatus.STOPPED -> "Қатъ карда шуд"
    RunStatus.OUTPUT_LIMIT -> "Баромад аз ҳад зиёд аст ва бурида шуд"
    RunStatus.UNSUPPORTED -> "Ин имконият дастгирӣ намешавад"
    RunStatus.ENGINE_ERROR -> "Хатои муҳаррики Python"
}

@Composable
fun ConsoleView(state: ConsoleState, modifier: Modifier = Modifier) {
    AppCard(modifier) {
        val r = state.result
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("›_", color = Palette.Accent, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
            Spacer(Modifier.width(8.dp))
            Text(T.output, style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
            if (!state.running && r != null) {
                val ok = r.status == RunStatus.OK
                Pill(if (ok) T.passedShort else T.errorShort, if (ok) Palette.Success else Palette.Error)
            }
        }
        Spacer(Modifier.height(10.dp))
        when {
            state.running -> Row(verticalAlignment = Alignment.CenterVertically) {
                CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp, color = Palette.Accent)
                Spacer(Modifier.width(10.dp)); Text(T.running)
            }
            r == null -> Text(T.consoleEmpty, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
            else -> {
                Text(statusLabel(r.status), color = if (r.status == RunStatus.OK) Palette.Success else Palette.Error, style = MaterialTheme.typography.labelLarge)
                if (r.stdout.isNotEmpty()) { Spacer(Modifier.height(8.dp)); CodeBlock(r.stdout, highlight = false) }
                if (r.stderr.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    Text(r.stderr, fontFamily = FontFamily.Monospace, fontSize = 12.sp, color = Palette.Error)
                }
                state.help?.let { h ->
                    Spacer(Modifier.height(12.dp))
                    Callout(color = Palette.Warning, icon = Icons.Filled.Warning) {
                        Text(h.title, style = MaterialTheme.typography.titleSmall, color = Palette.Warning)
                        Spacer(Modifier.height(4.dp))
                        Text(h.explanation, style = MaterialTheme.typography.bodyMedium)
                        if (h.causes.isNotEmpty()) { Spacer(Modifier.height(6.dp)); BulletList(h.causes, Palette.Warning) }
                        if (h.badExample.isNotBlank()) {
                            Spacer(Modifier.height(10.dp)); Text("Нодуруст:", style = MaterialTheme.typography.labelLarge); Spacer(Modifier.height(4.dp)); CodeBlock(h.badExample)
                            Spacer(Modifier.height(8.dp)); Text("Дуруст:", style = MaterialTheme.typography.labelLarge); Spacer(Modifier.height(4.dp)); CodeBlock(h.goodExample)
                        }
                    }
                }
            }
        }
    }
}

/** Блоки муваффақият/хато барои натиҷаи санҷиш. */
@Composable
fun VerdictCard(ok: Boolean, title: String, message: String) {
    val color = if (ok) Palette.Success else Palette.Error
    Callout(color = color, icon = if (ok) Icons.Filled.CheckCircle else Icons.Filled.Warning) {
        Text(title, style = MaterialTheme.typography.titleSmall, color = color)
        if (message.isNotBlank()) Text(message, style = MaterialTheme.typography.bodyMedium)
    }
}
