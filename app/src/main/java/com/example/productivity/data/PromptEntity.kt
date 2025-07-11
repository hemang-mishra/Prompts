package com.example.productivity.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "prompts")
data class PromptEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val text: String,
    val frequency: Int = 0,
    val categoryId: Int = 1, // Default category id will be 1
    val lastReviewTimestamp: Long? = null, // Timestamp of last review (nullable)
    val reviewFrequency: Int = 7 // Default review frequency in days
)
