package com.example.data

import androidx.room.*

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val email: String,
    val passwordHash: String, // Secure simulation for local auth
    val locationZone: String = "Zone 6b",
    val favoriteProjectTitle: String = "My Tomato Highrise",
    val favoriteProjectPhotoUri: String = ""
)

@Entity(tableName = "plants")
data class PlantEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val scientificName: String,
    val category: String, // "Vegetables", "Herbs", "Fruits", "Flowers"
    val plantedDate: Long,
    val zone: String,
    val waterIntervalDays: Int = 3,
    val fertilizeIntervalDays: Int = 14,
    val lastWatered: Long = System.currentTimeMillis(),
    val lastFertilized: Long = System.currentTimeMillis(),
    val notes: String = "",
    val isArchived: Boolean = false
)

@Entity(
    tableName = "plant_logs",
    foreignKeys = [
        ForeignKey(
            entity = PlantEntity::class,
            parentColumns = ["id"],
            childColumns = ["plantId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("plantId")]
)
data class PlantLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val plantId: Int,
    val timestamp: Long,
    val note: String,
    val photoUri: String = "",
    val status: String = "Healthy" // "Sprouting", "Leafing", "Flowering", "Harvesting", "Healthy"
)

@Entity(tableName = "forum_posts")
data class ForumPostEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val authorId: Int,
    val authorName: String,
    val title: String,
    val content: String,
    val timestamp: Long,
    val postType: String = "General", // "General", "Seed Swap", "Organic Advice"
    val likesCount: Int = 0,
    val seedAvailable: String = "" // Optional: seed details if seed swap post
)

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val senderId: Int,
    val senderName: String,
    val receiverId: Int,
    val receiverName: String,
    val content: String,
    val timestamp: Long
)

@Entity(tableName = "task_reminders")
data class TaskReminderEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val plantId: Int,
    val plantName: String,
    val type: String, // "Watering" or "Fertilizing"
    val dueDate: Long,
    val isCompleted: Boolean = false
)
