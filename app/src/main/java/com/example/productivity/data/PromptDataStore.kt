package com.example.productivity.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

val Context.promptDataStore by preferencesDataStore(name = "prompts")

@Serializable
data class Prompt(
    val title: String,
    val text: String,
    val frequency: Int = 0
)

// DataStore and PromptDataStore are now deprecated. Use Room and PromptRepository instead.
// This file can be deleted after migration is complete.
object PromptDataStore {
    private val PROMPTS_KEY = stringPreferencesKey("prompts_list")
    private val DEFAULT_PROMPTS = listOf(
        Prompt("Daily Accomplishment", "What did you accomplish today?", 0),
        Prompt("Tomorrow's Goal", "What is your main goal for tomorrow?", 0),
        Prompt("Distraction", "What distracted you most today?", 0),
        Prompt("Gratitude", "What are you grateful for?", 0),
        Prompt("Improvement", "What will you improve next week?", 0),
        Prompt("Improvement", "What will you improve next week?", 0),
        Prompt("Improvement", "What will you improve next week?", 0),
        Prompt("Improvement", "What will you improve next week?", 0),
        Prompt("Improvement", "What will you improve next week?", 0),
        Prompt("Improvement", "What will you improve next week?", 0),
        Prompt("Improvement", "What will you improve next week?", 0),
        Prompt("Improvement", "What will you improve next week?", 0),
        Prompt("Improvement", "What will you improve next week?", 0),
        Prompt("Improvement", "What will you improve next week?", 0),
        Prompt("Improvement", "What will you improve next week?", 0),
        Prompt("Improvement", "What will you improve next week?", 0),
        Prompt("Improvement", "What will you improve next week?", 0),
        Prompt("Improvement", "What will you improve next week?", 0)
    )

    fun getPrompts(context: Context): Flow<List<Prompt>> =
        context.promptDataStore.data.map { preferences ->
            preferences[PROMPTS_KEY]?.let {
                try {
                    Json.decodeFromString<List<Prompt>>(it)
                } catch (e: Exception) {
                    DEFAULT_PROMPTS
                }
            } ?: DEFAULT_PROMPTS
        }

    suspend fun addPrompt(context: Context, prompt: Prompt = Prompt("Default", "", 0)) {
        context.promptDataStore.edit { preferences ->
            val current = preferences[PROMPTS_KEY]?.let {
                try { Json.decodeFromString<MutableList<Prompt>>(it) } catch (e: Exception) { DEFAULT_PROMPTS.toMutableList() }
            } ?: DEFAULT_PROMPTS.toMutableList()
            current.add(prompt)
            preferences[PROMPTS_KEY] = Json.encodeToString(current)
        }
    }

    suspend fun updatePrompt(context: Context, index: Int, newPrompt: Prompt) {
        context.promptDataStore.edit { preferences ->
            val current = preferences[PROMPTS_KEY]?.let {
                try { Json.decodeFromString<MutableList<Prompt>>(it) } catch (e: Exception) { DEFAULT_PROMPTS.toMutableList() }
            } ?: DEFAULT_PROMPTS.toMutableList()
            if (index in current.indices) {
                current[index] = newPrompt
                preferences[PROMPTS_KEY] = Json.encodeToString(current)
            }
        }
    }

    suspend fun incrementFrequency(context: Context, index: Int) {
        context.promptDataStore.edit { preferences ->
            val current = preferences[PROMPTS_KEY]?.let {
                try { Json.decodeFromString<MutableList<Prompt>>(it) } catch (e: Exception) { DEFAULT_PROMPTS.toMutableList() }
            } ?: DEFAULT_PROMPTS.toMutableList()
            if (index in current.indices) {
                val prompt = current[index]
                current[index] = prompt.copy(frequency = prompt.frequency + 1)
                preferences[PROMPTS_KEY] = Json.encodeToString(current)
            }
        }
    }

    suspend fun initializeIfEmpty(context: Context) {
        context.promptDataStore.edit { preferences ->
            if (preferences[PROMPTS_KEY] == null) {
                preferences[PROMPTS_KEY] = Json.encodeToString(DEFAULT_PROMPTS)
            }
        }
    }
}
