package com.example.productivity.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.util.*

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

    // Reminder-related queries
    @Query("UPDATE prompts SET lastReviewTimestamp = :timestamp WHERE id = :id")
    suspend fun updateLastReviewTimestamp(id: Int, timestamp: Long)

    @Query("UPDATE prompts SET reviewFrequency = :days WHERE id = :id")
    suspend fun updateReviewFrequency(id: Int, days: Int)

    @Query("UPDATE prompts SET lastReviewTimestamp = :timestamp, frequency = frequency + 1 WHERE id = :id")
    suspend fun reviewPrompt(id: Int, timestamp: Long)

    @Query("SELECT * FROM prompts ORDER BY " +
            "CASE WHEN lastReviewTimestamp IS NULL THEN 0 ELSE lastReviewTimestamp + (reviewFrequency * 86400000) END DESC")
    fun getPromptsForReview(): Flow<List<PromptEntity>>
}
