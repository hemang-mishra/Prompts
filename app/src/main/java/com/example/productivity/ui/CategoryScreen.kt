package com.example.productivity.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.productivity.viewmodel.CategoryViewModel
import com.example.productivity.data.CategoryEntity

@Composable
fun CategoryScreen(viewModel: CategoryViewModel) {
    val categories by viewModel.categories.collectAsState()
    var newCategory by remember { mutableStateOf(TextFieldValue("")) }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Manage Categories", style = MaterialTheme.typography.titleLarge)
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = newCategory,
                onValueChange = { newCategory = it },
                label = { Text("New Category") },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                if (newCategory.text.isNotBlank()) {
                    viewModel.addCategory(CategoryEntity(name = newCategory.text.trim()))
                    newCategory = TextFieldValue("")
                    errorMessage = ""
                } else {
                    errorMessage = "Category name cannot be empty"
                }
            }) {
                Text("Add")
            }
        }
        if (errorMessage.isNotEmpty()) {
            Text(errorMessage, color = MaterialTheme.colorScheme.error)
        }
        HorizontalDivider()
        Text("Categories:", style = MaterialTheme.typography.titleMedium)
        categories.forEach { category ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(category.name, modifier = Modifier.weight(1f))
                IconButton(onClick = { viewModel.deleteCategory(category) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }
            HorizontalDivider()
        }
    }
}

