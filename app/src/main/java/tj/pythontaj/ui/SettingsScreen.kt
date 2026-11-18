package tj.pythontaj.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tj.pythontaj.MainViewModel

@Composable
fun SettingsScreen(vm: MainViewModel) {
    var confirm by rememberSaveable { mutableStateOf(false) }
    ScreenScaffold(T.settings) { pad ->
        Column(
            Modifier.padding(pad).fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            AppCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BadgeCircle(size = 40.dp, filled = false) { Icon(Icons.Filled.Info, contentDescription = null, tint = Palette.Accent) }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(T.about, style = MaterialTheme.typography.titleSmall)
                        Text("${T.version}: 0.1.0", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Spacer(Modifier.height(10.dp))
                Text(T.aboutBody, style = MaterialTheme.typography.bodyMedium)
            }
            AppCard(accent = Palette.Warning) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Warning, contentDescription = null, tint = Palette.Warning)
                    Spacer(Modifier.width(10.dp))
                    Text(T.limits, style = MaterialTheme.typography.titleSmall)
                }
                Spacer(Modifier.height(8.dp))
                Text(T.limitsBody, style = MaterialTheme.typography.bodyMedium)
            }
            SectionTitle(T.dangerZone)
            SoftButton(T.resetProgress, onClick = { confirm = true }, color = Palette.Error, modifier = Modifier.fillMaxWidth())
        }
    }
    if (confirm) {
        AlertDialog(
            onDismissRequest = { confirm = false },
            containerColor = Palette.CardHigh,
            title = { Text(T.resetConfirmTitle) },
            text = { Text(T.resetConfirmBody) },
            confirmButton = { TextButton(onClick = { vm.resetProgress(); confirm = false }) { Text(T.delete, color = Palette.Error) } },
            dismissButton = { TextButton(onClick = { confirm = false }) { Text(T.cancel) } },
        )
    }
}
