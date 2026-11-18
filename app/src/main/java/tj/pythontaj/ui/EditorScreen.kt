package tj.pythontaj.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import tj.pythontaj.MainViewModel

@Composable
fun EditorScreen(vm: MainViewModel) {
    val code by vm.editorCode.collectAsState()
    val console by vm.console.collectAsState()
    val snippets by vm.snippets.collectAsState()
    val context = LocalContext.current

    ScreenScaffold(T.editor) { pad ->
        Column(
            Modifier.padding(pad).fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            CodeEditor(code, { vm.editorCode.value = it })
            if (console.running) {
                SoftButton(T.stop, onClick = { vm.stop() }, color = Palette.Error, modifier = Modifier.fillMaxWidth())
            } else {
                GradientButton(T.run, onClick = { vm.runEditor() }, icon = Icons.Filled.PlayArrow, modifier = Modifier.fillMaxWidth())
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                SoftButton(T.copy, onClick = {
                    val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    cm.setPrimaryClip(ClipData.newPlainText("PythonTAJ", code))
                }, modifier = Modifier.weight(1f))
                SoftButton(T.save, onClick = { vm.saveSnippet() }, modifier = Modifier.weight(1f))
                SoftButton(T.reset, onClick = { vm.editorCode.value = ""; vm.clearConsole() }, modifier = Modifier.weight(1f))
            }
            ConsoleView(console)
            SectionTitle(T.mySnippets)
            if (snippets.isEmpty()) Text(T.noSnippets, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
            snippets.forEach { s ->
                AppCard {
                    Text(s.title, style = MaterialTheme.typography.titleSmall)
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TextButton(onClick = { vm.editorCode.value = s.code }) { Text(T.openInEditor, color = Palette.Accent) }
                        TextButton(onClick = { vm.deleteSnippet(s.id) }) { Text(T.delete, color = Palette.Error) }
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}
