package tj.pythontaj.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tj.pythontaj.MainViewModel
import tj.pythontaj.domain.ProgressCalculator

@Composable
fun HomeScreen(vm: MainViewModel, onOpenLesson: (String) -> Unit, onGo: (String) -> Unit) {
    ScreenBackground {
        ContentGate(vm, Modifier.statusBarsPadding()) { cur ->
            val done by vm.completedLessons.collectAsState()
            val streak by vm.streak.collectAsState()
            val doneCount = cur.lessons.count { it.id in done }
            val percent = ProgressCalculator.percent(doneCount, cur.lessons.size)
            val (levelNo, levelName) = ProgressCalculator.level(percent)
            val next = cur.lessons.firstOrNull { it.id !in done }

            Column(
                Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // Сарлавҳа
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.size(52.dp).clip(RoundedCornerShape(16.dp)).background(Palette.Gradient),
                        contentAlignment = Alignment.Center,
                    ) { Text("Py", color = Color(0xFF00201C), fontSize = 22.sp, fontWeight = FontWeight.ExtraBold) }
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text(T.appName, style = MaterialTheme.typography.headlineMedium)
                        Text(
                            T.welcomeSub,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }

                // Корти пешрафт
                AppCard(accent = Palette.Accent) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        ProgressRing(percent, size = 104.dp)
                        Spacer(Modifier.width(18.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(T.yourProgress, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${T.level} $levelNo", style = MaterialTheme.typography.titleLarge)
                            Pill(levelName)
                            Text(
                                "${T.completedLessons}: $doneCount ${T.lessonsOf} ${cur.lessons.size}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }

                // Омор
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    StatTile("🔥", "$streak ${T.days}", T.streak, Palette.Warning, Modifier.weight(1f))
                    StatTile("✅", "$doneCount/${cur.lessons.size}", T.path, Palette.Success, Modifier.weight(1f))
                }

                // Идома додан
                if (next != null) {
                    val shape = RoundedCornerShape(22.dp)
                    Row(
                        Modifier.fillMaxWidth().clip(shape).background(Palette.Gradient).clickable { onOpenLesson(next.id) }.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(T.continueLearning, style = MaterialTheme.typography.labelLarge, color = Color(0xFF00201C).copy(alpha = 0.75f))
                            Spacer(Modifier.height(4.dp))
                            Text(next.title, style = MaterialTheme.typography.titleLarge, color = Color(0xFF00201C), maxLines = 2, overflow = TextOverflow.Ellipsis)
                        }
                        Spacer(Modifier.width(12.dp))
                        Box(Modifier.size(48.dp).background(Color(0xFF00201C), CircleShape), contentAlignment = Alignment.Center) {
                            Icon(Icons.Filled.PlayArrow, contentDescription = T.startNow, tint = Palette.Accent)
                        }
                    }
                } else {
                    Callout(color = Palette.Success, icon = Icons.Filled.CheckCircle) {
                        Text(T.allDone, style = MaterialTheme.typography.titleSmall, color = Palette.Success)
                    }
                }

                // Муҳаррири озод
                AppCard(Modifier.clickable { onGo("editor") }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        BadgeCircle(size = 44.dp, filled = false) { Icon(Icons.Filled.Edit, contentDescription = null, tint = Palette.AccentAlt) }
                        Spacer(Modifier.width(14.dp))
                        Column(Modifier.weight(1f)) {
                            Text(T.tryCode, style = MaterialTheme.typography.titleSmall)
                            Text(T.tryCodeSub, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Spacer(Modifier.height(4.dp))
            }
        }
    }
}

@Composable
private fun StatTile(emoji: String, value: String, label: String, color: Color, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(20.dp)
    Column(
        modifier.clip(shape).background(Palette.Card).border(1.dp, Palette.Outline, shape).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(emoji, fontSize = 22.sp)
        Spacer(Modifier.height(6.dp))
        Text(value, style = MaterialTheme.typography.titleLarge, color = color)
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}
