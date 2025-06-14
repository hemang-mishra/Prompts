package com.example.productivity.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.productivity.data.CategoryEntity
import com.example.productivity.data.CategoryRepository
import com.example.productivity.data.PromptEntity
import com.example.productivity.data.PromptRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PromptViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = PromptRepository(application)
    private val categoryRepository = CategoryRepository(application)
    val categories: StateFlow<List<CategoryEntity>> = categoryRepository.getCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    private val _selectedCategoryId = MutableStateFlow<Int?>(null)
    val selectedCategoryId: StateFlow<Int?> = _selectedCategoryId
    val prompts: StateFlow<List<PromptEntity>> = _selectedCategoryId
        .flatMapLatest { repository.getPromptsByCategory(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setCategoryFilter(categoryId: Int?) {
        _selectedCategoryId.value = categoryId
    }

    fun addPrompt(prompt: PromptEntity) {
        viewModelScope.launch { repository.addPrompt(prompt) }
    }

    fun updatePrompt(prompt: PromptEntity) {
        viewModelScope.launch { repository.updatePrompt(prompt) }
    }

    fun incrementFrequency(id: Int) {
        viewModelScope.launch { repository.incrementFrequency(id) }
    }

    suspend fun exportPromptsAsJson(): String {
        return repository.exportPromptsAsJson()
    }

    fun importPromptsFromJson(json: String, onComplete: (() -> Unit)? = null) {
        viewModelScope.launch {
            repository.importPromptsFromJson(json)
            onComplete?.invoke()
        }
    }

    suspend fun exportBackupAsJson(): String {
        return repository.exportBackupAsJson()
    }

    fun importBackupFromJson(json: String, onComplete: (() -> Unit)? = null) {
        viewModelScope.launch {
            repository.importBackupFromJson(json)
            onComplete?.invoke()
        }
    }
}
