package com.example.productivity.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PromptDao {
    @Query("SELECT * FROM prompts")
    fun getAllPrompts(): Flow<List<PromptEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrompt(prompt: PromptEntity)

    @Update
    suspend fun updatePrompt(prompt: PromptEntity)

    @Query("UPDATE prompts SET frequency = frequency + 1 WHERE id = :id")
    suspend fun incrementFrequency(id: Int)

    @Query("SELECT * FROM prompts")
    suspend fun getAllPromptsOnce(): List<PromptEntity>

    @Query("DELETE FROM prompts")
    suspend fun clearAll()

    @Query("SELECT * FROM prompts WHERE (:categoryId IS NULL OR categoryId = :categoryId)")
    fun getPromptsByCategory(categoryId: Int?): Flow<List<PromptEntity>>
}
