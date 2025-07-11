package com.example.productivity.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.RoomDatabase.Callback
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [PromptEntity::class, CategoryEntity::class], version = 3)
abstract class PromptDatabase : RoomDatabase() {
    abstract fun promptDao(): PromptDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        @Volatile
        private var INSTANCE: PromptDatabase? = null

        // Migration from version 2 to 3 (adding reminder fields)
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE prompts ADD COLUMN lastReviewTimestamp INTEGER DEFAULT NULL")
                database.execSQL("ALTER TABLE prompts ADD COLUMN reviewFrequency INTEGER NOT NULL DEFAULT 7")
            }
        }

        fun getDatabase(context: Context): PromptDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PromptDatabase::class.java,
                    "prompt_database"
                )
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        db.execSQL("INSERT INTO categories (id, name) VALUES (1, 'Default')")
                    }
                })
                .addMigrations(MIGRATION_2_3)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
