package com.example.productivity.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.RoomDatabase.Callback

@Database(entities = [PromptEntity::class, CategoryEntity::class], version = 2)
abstract class PromptDatabase : RoomDatabase() {
    abstract fun promptDao(): PromptDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        @Volatile
        private var INSTANCE: PromptDatabase? = null

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
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
