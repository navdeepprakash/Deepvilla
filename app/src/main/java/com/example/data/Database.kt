package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        UserEntity::class,
        PlantEntity::class,
        PlantLogEntity::class,
        ForumPostEntity::class,
        MessageEntity::class,
        TaskReminderEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class GardenDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun plantDao(): PlantDao
    abstract fun plantLogDao(): PlantLogDao
    abstract fun forumPostDao(): ForumPostDao
    abstract fun messageDao(): MessageDao
    abstract fun taskReminderDao(): TaskReminderDao

    companion object {
        @Volatile
        private var INSTANCE: GardenDatabase? = null

        fun getDatabase(context: Context): GardenDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GardenDatabase::class.java,
                    "garden_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
