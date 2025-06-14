package com.example.productivity.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.example.productivity.data.PromptEntity
import com.example.productivity.viewmodel.PromptViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromptListScreen(
    viewModel: PromptViewModel,
    onPromptClick: (PromptEntity) -> Unit,
    onAddPrompt: () -> Unit,
    onPromptSend: (PromptEntity) -> Unit
) {
    val prompts by viewModel.prompts.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val selectedCategoryId by viewModel.selectedCategoryId.collectAsState()
    val scrollState = rememberScrollState()
    Box(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            Text("Prompts:")
            Spacer(modifier = Modifier.height(8.dp))
            // Category filter dropdown
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Filter by Category:", modifier = Modifier.padding(end = 8.dp))
                var expanded by remember { mutableStateOf(false) }
                Box {
                    Button(onClick = { expanded = true }) {
                        Text(categories.find { it.id == selectedCategoryId }?.name ?: "All")
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        DropdownMenuItem(text = { Text("All") }, onClick = {
                            viewModel.setCategoryFilter(null)
                            expanded = false
                        })
                        categories.forEach { category ->
                            DropdownMenuItem(text = { Text(category.name) }, onClick = {
                                viewModel.setCategoryFilter(category.id)
                                expanded = false
                            })
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            prompts.sortedByDescending { it.frequency }.forEach { prompt ->
                val categoryName = categories.find { it.id == prompt.categoryId }?.name ?: "Default"
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(
                        modifier = Modifier.weight(1f)
                            .clickable { onPromptSend(prompt) }
                    ) {
                        Text(prompt.title, style = MaterialTheme.typography.titleMedium)
                        Text(
                            "Frequency: ${prompt.frequency}",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            "Category: $categoryName",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    OutlinedButton(onClick = { onPromptClick(prompt) }) {
                        Text("Edit")
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
            }
            Spacer(modifier = Modifier.height(42.dp))
        }
        FloatingActionButton(onClick = onAddPrompt,
            modifier = Modifier.align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 32.dp)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = null)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromptDetailScreen(
    prompt: PromptEntity,
    onSave: (PromptEntity) -> Unit,
    onClose: () -> Unit,
    viewModel: PromptViewModel
) {
    var title by remember { mutableStateOf(prompt.title) }
    var text by remember { mutableStateOf(prompt.text) }
    var frequency by remember { mutableStateOf(prompt.frequency) }
    var categoryId by remember { mutableStateOf(prompt.categoryId) }
    val categories = viewModel.categories.collectAsState().value
    ModalBottomSheet(
        onDismissRequest = onClose,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text("Edit Prompt", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Title:")
                BasicTextField(
                    value = title,
                    onValueChange = { title = it },
                    modifier = Modifier.padding(8.dp),
                    textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.onBackground),
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Prompt Text:")
                BasicTextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier.padding(8.dp),
                    textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.onBackground),
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Frequency: $frequency")
                Spacer(modifier = Modifier.height(8.dp))
                Text("Category:")
                var expanded by remember { mutableStateOf(false) }
                Box {
                    Button(onClick = { expanded = true }) {
                        Text(categories.find { it.id == categoryId }?.name ?: "Select Category")
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        categories.forEach { category ->
                            DropdownMenuItem(text = { Text(category.name) }, onClick = {
                                categoryId = category.id
                                expanded = false
                            })
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row {
                    Button(onClick = {
                        onSave(prompt.copy(title = title, text = text, frequency = frequency, categoryId = categoryId))
                    }) { Text("Save") }
                    Spacer(modifier = Modifier.weight(1f))
                    Button(onClick = onClose) { Text("Cancel") }
                }
            }
        }
    }
}
