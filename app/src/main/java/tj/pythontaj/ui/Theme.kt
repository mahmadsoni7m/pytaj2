package tj.pythontaj.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/** Рангҳои барнома. Барои тағйири намуди зоҳирӣ ин ҷоро таҳрир кунед. */
object Palette {
    val Accent = Color(0xFF2DD4BF)
    val AccentAlt = Color(0xFF818CF8)
    val Background = Color(0xFF0B0F14)
    val Card = Color(0xFF141A22)
    val CardHigh = Color(0xFF1C2430)
    val Outline = Color(0xFF263140)
    val NavBar = Color(0xFF0E131A)
    val Code = Color(0xFF090C11)
    val CodeText = Color(0xFFD6E4EA)
    val Error = Color(0xFFFF7B7B)
    val Success = Color(0xFF4ADE80)
    val Warning = Color(0xFFFBBF24)

    // Рангҳои ҳайлайти Python
    val SynKeyword = Color(0xFFC792EA)
    val SynString = Color(0xFFA5E075)
    val SynNumber = Color(0xFFF78C6C)
    val SynComment = Color(0xFF6B7A8C)
    val SynBuiltin = Color(0xFF82AAFF)
    val SynFunc = Color(0xFF2DD4BF)

    val Gradient = Brush.linearGradient(listOf(Accent, AccentAlt))
}

private val Dark = darkColorScheme(
    primary = Palette.Accent,
    onPrimary = Color(0xFF00201C),
    primaryContainer = Color(0xFF123B39),
    onPrimaryContainer = Color(0xFFB8F5EC),
    secondary = Palette.AccentAlt,
    tertiary = Palette.Success,
    background = Palette.Background,
    onBackground = Color(0xFFE8ECF0),
    surface = Palette.Background,
    onSurface = Color(0xFFE8ECF0),
    surfaceVariant = Palette.Card,
    onSurfaceVariant = Color(0xFFA9B4BF),
    outline = Palette.Outline,
    outlineVariant = Palette.Outline,
    error = Palette.Error,
)

private val AppTypography = Typography(
    headlineMedium = TextStyle(fontSize = 28.sp, lineHeight = 34.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.4).sp),
    titleLarge = TextStyle(fontSize = 22.sp, lineHeight = 28.sp, fontWeight = FontWeight.Bold),
    titleMedium = TextStyle(fontSize = 17.sp, lineHeight = 24.sp, fontWeight = FontWeight.SemiBold),
    titleSmall = TextStyle(fontSize = 15.sp, lineHeight = 22.sp, fontWeight = FontWeight.SemiBold),
    bodyLarge = TextStyle(fontSize = 16.sp, lineHeight = 25.sp),
    bodyMedium = TextStyle(fontSize = 14.sp, lineHeight = 21.sp),
    bodySmall = TextStyle(fontSize = 12.sp, lineHeight = 18.sp),
    labelLarge = TextStyle(fontSize = 14.sp, lineHeight = 20.sp, fontWeight = FontWeight.SemiBold),
    labelMedium = TextStyle(fontSize = 12.sp, lineHeight = 16.sp, fontWeight = FontWeight.Medium, letterSpacing = 0.3.sp),
)

@Composable
fun PythonTajTheme(content: @Composable () -> Unit) =
    MaterialTheme(colorScheme = Dark, typography = AppTypography, content = content)
