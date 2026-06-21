package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: Int): UserEntity?

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<UserEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)
}

@Dao
interface PlantDao {
    @Query("SELECT * FROM plants WHERE isArchived = 0 ORDER BY plantedDate DESC")
    fun getAllPlants(): Flow<List<PlantEntity>>

    @Query("SELECT * FROM plants WHERE id = :id LIMIT 1")
    fun getPlantById(id: Int): Flow<PlantEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlant(plant: PlantEntity): Long

    @Update
    suspend fun updatePlant(plant: PlantEntity)

    @Delete
    suspend fun deletePlant(plant: PlantEntity)
}

@Dao
interface PlantLogDao {
    @Query("SELECT * FROM plant_logs WHERE plantId = :plantId ORDER BY timestamp DESC")
    fun getLogsForPlant(plantId: Int): Flow<List<PlantLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: PlantLogEntity): Long

    @Delete
    suspend fun deleteLog(log: PlantLogEntity)
}

@Dao
interface ForumPostDao {
    @Query("SELECT * FROM forum_posts ORDER BY timestamp DESC")
    fun getAllPosts(): Flow<List<ForumPostEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: ForumPostEntity): Long

    @Update
    suspend fun updatePost(post: ForumPostEntity)
}

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages WHERE senderId = :userId OR receiverId = :userId ORDER BY timestamp ASC")
    fun getAllMessagesForUser(userId: Int): Flow<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity): Long
}

@Dao
interface TaskReminderDao {
    @Query("SELECT * FROM task_reminders ORDER BY dueDate ASC")
    fun getAllReminders(): Flow<List<TaskReminderEntity>>

    @Query("SELECT * FROM task_reminders WHERE isCompleted = 0 ORDER BY dueDate ASC")
    fun getActiveReminders(): Flow<List<TaskReminderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: TaskReminderEntity): Long

    @Update
    suspend fun updateReminder(reminder: TaskReminderEntity)

    @Query("UPDATE task_reminders SET isCompleted = 1 WHERE id = :id")
    suspend fun completeReminder(id: Int)

    @Query("DELETE FROM task_reminders WHERE plantId = :plantId")
    suspend fun deleteRemindersForPlant(plantId: Int)
}
