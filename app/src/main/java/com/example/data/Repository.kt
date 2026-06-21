package com.example.data

import kotlinx.coroutines.flow.Flow

class GardenRepository(private val db: GardenDatabase) {
    val userDao = db.userDao()
    val plantDao = db.plantDao()
    val plantLogDao = db.plantLogDao()
    val forumPostDao = db.forumPostDao()
    val messageDao = db.messageDao()
    val taskReminderDao = db.taskReminderDao()

    // Users
    suspend fun getUserByEmail(email: String): UserEntity? = userDao.getUserByEmail(email)
    suspend fun getUserById(id: Int): UserEntity? = userDao.getUserById(id)
    suspend fun getAllUsers(): List<UserEntity> = userDao.getAllUsers()
    suspend fun insertUser(user: UserEntity): Long = userDao.insertUser(user)
    suspend fun updateUser(user: UserEntity) = userDao.updateUser(user)

    // Plants
    val allPlants: Flow<List<PlantEntity>> = plantDao.getAllPlants()
    fun getPlantById(id: Int): Flow<PlantEntity?> = plantDao.getPlantById(id)
    suspend fun insertPlant(plant: PlantEntity): Long = plantDao.insertPlant(plant)
    suspend fun updatePlant(plant: PlantEntity) = plantDao.updatePlant(plant)
    suspend fun deletePlant(plant: PlantEntity) {
        plantDao.deletePlant(plant)
        taskReminderDao.deleteRemindersForPlant(plant.id)
    }

    // Plant Logs
    fun getLogsForPlant(plantId: Int): Flow<List<PlantLogEntity>> = plantLogDao.getLogsForPlant(plantId)
    suspend fun insertLog(log: PlantLogEntity): Long = plantLogDao.insertLog(log)
    suspend fun deleteLog(log: PlantLogEntity) = plantLogDao.deleteLog(log)

    // Forum Posts
    val allPosts: Flow<List<ForumPostEntity>> = forumPostDao.getAllPosts()
    suspend fun insertPost(post: ForumPostEntity): Long = forumPostDao.insertPost(post)
    suspend fun updatePost(post: ForumPostEntity) = forumPostDao.updatePost(post)

    // Messages
    fun getAllMessagesForUser(userId: Int): Flow<List<MessageEntity>> = messageDao.getAllMessagesForUser(userId)
    suspend fun insertMessage(message: MessageEntity): Long = messageDao.insertMessage(message)

    // Reminders
    val allReminders: Flow<List<TaskReminderEntity>> = taskReminderDao.getAllReminders()
    val activeReminders: Flow<List<TaskReminderEntity>> = taskReminderDao.getActiveReminders()
    suspend fun insertReminder(reminder: TaskReminderEntity): Long = taskReminderDao.insertReminder(reminder)
    suspend fun updateReminder(reminder: TaskReminderEntity) = taskReminderDao.updateReminder(reminder)
    suspend fun completeReminder(id: Int) = taskReminderDao.completeReminder(id)
}
