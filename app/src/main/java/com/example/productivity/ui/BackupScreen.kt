package com.example.productivity.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.productivity.viewmodel.PromptViewModel
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString

@Composable
fun BackupScreen(viewModel: PromptViewModel) {
    val context = LocalContext.current
    var exportJson by remember { mutableStateOf("") }
    var importJson by remember { mutableStateOf(TextFieldValue("")) }
    var restoreMessage by remember { mutableStateOf("") }
    var copyMessage by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val clipboardManager: ClipboardManager = LocalClipboardManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Backup & Restore", style = MaterialTheme.typography.titleLarge)
        Button(onClick = {
            scope.launch {
                exportJson = viewModel.exportBackupAsJson()
                copyMessage = ""
            }
        }) {
            Text("Export Backup (Prompts + Categories)")
        }
        if (exportJson.isNotEmpty()) {
            OutlinedTextField(
                value = exportJson,
                onValueChange = {},
                label = { Text("Exported Backup JSON") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                maxLines = 10
            )
            Button(onClick = {
                clipboardManager.setText(AnnotatedString(exportJson))
                copyMessage = "Copied to clipboard!"
            }, modifier = Modifier.padding(top = 8.dp)) {
                Text("Copy")
            }
            if (copyMessage.isNotEmpty()) {
                Text(copyMessage, color = MaterialTheme.colorScheme.primary)
            }
        }
        Divider()
        Text("Restore from Backup JSON", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(
            value = importJson,
            onValueChange = { importJson = it },
            label = { Text("Paste Backup JSON here") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 10
        )
        Button(onClick = {
            scope.launch {
                try {
                    viewModel.importBackupFromJson(importJson.text) {
                        restoreMessage = "Restore successful!"
                    }
                } catch (e: Exception) {
                    restoreMessage = "Restore failed: ${e.localizedMessage}"
                }
            }
        }) {
            Text("Restore Prompts & Categories from Backup")
        }
        if (restoreMessage.isNotEmpty()) {
            Text(restoreMessage, color = if (restoreMessage.contains("success")) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
        }
    }
}
