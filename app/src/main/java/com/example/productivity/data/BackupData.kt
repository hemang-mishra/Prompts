package com.example.productivity.data

import kotlinx.serialization.Serializable

@Serializable
data class BackupData(
    val prompts: List<PromptEntity>,
    val categories: List<CategoryEntity>
)

