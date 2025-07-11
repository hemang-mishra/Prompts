package com.example.productivity.data

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import com.example.productivity.data.BackupData

class PromptRepository(context: Context) {
    private val promptDao = PromptDatabase.getDatabase(context).promptDao()
    private val categoryDao = PromptDatabase.getDatabase(context).categoryDao()
    private val json = Json { ignoreUnknownKeys = true }

    fun getPrompts(): Flow<List<PromptEntity>> = promptDao.getAllPrompts()

    fun getPromptsByCategory(categoryId: Int?): Flow<List<PromptEntity>> = promptDao.getPromptsByCategory(categoryId)

    // Reminder-related function to get prompts for review
    fun getPromptsForReview(): Flow<List<PromptEntity>> = promptDao.getPromptsForReview()

    suspend fun addPrompt(prompt: PromptEntity) = withContext(Dispatchers.IO) {
        promptDao.insertPrompt(prompt)
    }

    suspend fun updatePrompt(prompt: PromptEntity) = withContext(Dispatchers.IO) {
        promptDao.updatePrompt(prompt)
    }

    // New method to delete a prompt
    suspend fun deletePrompt(id: Int) = withContext(Dispatchers.IO) {
        promptDao.deletePrompt(id)
    }

    suspend fun incrementFrequency(id: Int) = withContext(Dispatchers.IO) {
        promptDao.incrementFrequency(id)
    }

    // Reminder-related methods
    suspend fun updateLastReviewTimestamp(id: Int, timestamp: Long) = withContext(Dispatchers.IO) {
        promptDao.updateLastReviewTimestamp(id, timestamp)
    }

    suspend fun updateReviewFrequency(id: Int, days: Int) = withContext(Dispatchers.IO) {
        promptDao.updateReviewFrequency(id, days)
    }

    suspend fun reviewPrompt(id: Int) = withContext(Dispatchers.IO) {
        val currentTime = System.currentTimeMillis()
        promptDao.reviewPrompt(id, currentTime)
    }

    suspend fun exportPromptsAsJson(): String = withContext(Dispatchers.IO) {
        val prompts = promptDao.getAllPromptsOnce()
        json.encodeToString(prompts)
    }

    suspend fun importPromptsFromJson(jsonString: String) = withContext(Dispatchers.IO) {
        val prompts: List<PromptEntity> = json.decodeFromString(jsonString)
        promptDao.clearAll()
        prompts.forEach { promptDao.insertPrompt(it) }
    }

    suspend fun exportBackupAsJson(): String = withContext(Dispatchers.IO) {
        val prompts = promptDao.getAllPromptsOnce()
        val categories = categoryDao.getAllCategoriesOnce()
        val backup = BackupData(prompts, categories)
        json.encodeToString(backup)
    }

    suspend fun importBackupFromJson(jsonString: String) = withContext(Dispatchers.IO) {
        val backup: BackupData = json.decodeFromString(jsonString)
        promptDao.clearAll()
        categoryDao.clearAll()
        backup.categories.forEach { categoryDao.insertCategory(it) }
        backup.prompts.forEach { promptDao.insertPrompt(it) }
    }
}
