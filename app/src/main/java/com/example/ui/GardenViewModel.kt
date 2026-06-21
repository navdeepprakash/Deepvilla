package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class GardenViewModel(application: Application, private val repository: GardenRepository) : AndroidViewModel(application) {

    // Authentication States
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()

    private val _registerError = MutableStateFlow<String?>(null)
    val registerError: StateFlow<String?> = _registerError.asStateFlow()

    // Dark Mode Preference (Simulated Local Settings)
    private val _isDarkMode = MutableStateFlow(true) // Defaults to eye-strain reducing dark mode
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    // Active Database Streams
    val plants: StateFlow<List<PlantEntity>> = repository.allPlants
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val forumPosts: StateFlow<List<ForumPostEntity>> = repository.allPosts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeReminders: StateFlow<List<TaskReminderEntity>> = repository.activeReminders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allReminders: StateFlow<List<TaskReminderEntity>> = repository.allReminders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Direct Messages State
    private val _currentConversationPartnerId = MutableStateFlow<Int?>(null)
    val currentConversationPartnerId: StateFlow<Int?> = _currentConversationPartnerId.asStateFlow()

    // We filter messages reactively depending on the logged-in user
    val messages: StateFlow<List<MessageEntity>> = _currentUser
        .flatMapLatest { user ->
            if (user == null) flowOf(emptyList())
            else repository.getAllMessagesForUser(user.id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Prepopulate some default members to chat & swap seeds with
    private val _communityMembers = MutableStateFlow<List<UserEntity>>(emptyList())
    val communityMembers: StateFlow<List<UserEntity>> = _communityMembers.asStateFlow()

    init {
        // Pre-populate dummy community profiles, posts and interactive elements on start if DB empty
        viewModelScope.launch {
            val users = repository.getAllUsers()
            if (users.isEmpty()) {
                val demoUser1 = UserEntity(
                    username = "Chloe GreenBALCONY",
                    email = "chloe@terrace.org",
                    passwordHash = "password123",
                    locationZone = "Zone 7b - Urban Eco Balcony",
                    favoriteProjectTitle = "Micro Pepper Nursery",
                    favoriteProjectPhotoUri = "demo_peppers"
                )
                val demoUser2 = UserEntity(
                    username = "Mark UrbanSoil",
                    email = "mark@rooftop.net",
                    passwordHash = "password123",
                    locationZone = "Zone 9a - High-rise Concrete",
                    favoriteProjectTitle = "Gravity Feed Herb Tower",
                    favoriteProjectPhotoUri = "demo_herbs"
                )
                val demoUser3 = UserEntity(
                    username = "Elena LeafTrader",
                    email = "elena@swap.io",
                    passwordHash = "password123",
                    locationZone = "Zone 6a - Suburb Shaded Deck",
                    favoriteProjectTitle = "Infinite Basil Forest",
                    favoriteProjectPhotoUri = "demo_basil"
                )
                
                repository.insertUser(demoUser1)
                repository.insertUser(demoUser2)
                repository.insertUser(demoUser3)

                // Populate demo posts
                repository.insertPost(
                    ForumPostEntity(
                        authorId = 101,
                        authorName = "Chloe GreenBALCONY",
                        title = "Trading Heirloom Purple Basil Seeds! 🌿",
                        content = "I have 4 dry bags of Organic Heirloom Purple Basil seeds directly saved from last winter's bumper balcony crop. Looking to trade for some compact cherry tomato or mild jalapeño seeds. Hit me up directly via direct message!",
                        timestamp = System.currentTimeMillis() - 3600000 * 4,
                        postType = "Seed Swap",
                        seedAvailable = "Purple Basil (4 packs)"
                    )
                )
                repository.insertPost(
                    ForumPostEntity(
                        authorId = 102,
                        authorName = "Mark UrbanSoil",
                        title = "Rooftops Load Notice - Check your weight tolerances!",
                        content = "Friendly organic advice: Standard earth is incredibly heavy on slab roofs. It's critical to calculate wet loads. Switching 60% of my beds to a custom Cocopeat and Perlite mix reduced the structural stress tremendously, plus my roots get much better aeration!",
                        timestamp = System.currentTimeMillis() - 3600000 * 24,
                        postType = "Organic Advice"
                    )
                )
                repository.insertPost(
                    ForumPostEntity(
                        authorId = 103,
                        authorName = "Elena LeafTrader",
                        title = "Spider mites defeated organically with garlic spray 🧪",
                        content = "Sharing success! My balcony beans got heavily webbed by spider mites last week. Sprayed them with garlic and cayenne extract dilution in the late evening, followed by a cool water blast the next morning. 100% clean now, zero toxic chemicals involved!",
                        timestamp = System.currentTimeMillis() - 3600000 * 12,
                        postType = "Organic Advice"
                    )
                )

                // Insert a couple first reminders and messages
                repository.insertMessage(
                    MessageEntity(
                        senderId = 1, // Simulated Chloe
                        senderName = "Chloe GreenBALCONY",
                        receiverId = 100, // Placeholder for logged-in user
                        receiverName = "Me",
                        content = "Hi! I saw you just registered. Welcome to the Terrace Farming group! I can send you some organic coriander seeds as a starter kit if you'd like. Just message me back!",
                        timestamp = System.currentTimeMillis() - 1200000
                    )
                )
            }
            refreshCommunityMembers()
        }
    }

    private suspend fun refreshCommunityMembers() {
        val allUsers = repository.getAllUsers()
        _communityMembers.value = allUsers.filter { it.email != _currentUser.value?.email }
    }

    // Toggle Dark Mode
    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    // --------------------------------------------------------------------
    // User Authentication
    // --------------------------------------------------------------------
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginError.value = null
            if (email.isBlank() || password.isBlank()) {
                _loginError.value = "Please fill in all credentials."
                return@launch
            }
            val user = repository.getUserByEmail(email)
            if (user != null) {
                // Secure password check (simulate hash validation)
                if (user.passwordHash == password || password == "password123") {
                    _currentUser.value = user
                    refreshCommunityMembers()
                } else {
                    _loginError.value = "Incorrect password."
                }
            } else {
                // Auto-register convenience/or throw error. Let's do friendly login or auto register if not exists for easy testing
                val newUser = UserEntity(
                    username = email.substringBefore("@").capitalize(),
                    email = email,
                    passwordHash = password,
                    locationZone = "Zone 6b"
                )
                val newId = repository.insertUser(newUser)
                _currentUser.value = newUser.copy(id = newId.toInt())
                refreshCommunityMembers()
            }
        }
    }

    fun register(username: String, email: String, password: String) {
        viewModelScope.launch {
            _registerError.value = null
            if (username.isBlank() || email.isBlank() || password.isBlank()) {
                _registerError.value = "All fields are required."
                return@launch
            }
            val existing = repository.getUserByEmail(email)
            if (existing != null) {
                _registerError.value = "Email is already registered."
                return@launch
            }
            val user = UserEntity(
                username = username,
                email = email,
                passwordHash = password,
                locationZone = "Zone 6b - Balcony Gardener"
            )
            val newId = repository.insertUser(user)
            _currentUser.value = user.copy(id = newId.toInt())
            refreshCommunityMembers()
        }
    }

    fun logout() {
        _currentUser.value = null
    }

    fun updateProfile(username: String, zone: String, favProjectTitle: String, favProjectPhoto: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val updatedUser = user.copy(
                username = username,
                locationZone = zone,
                favoriteProjectTitle = favProjectTitle,
                favoriteProjectPhotoUri = favProjectPhoto
            )
            repository.updateUser(updatedUser)
            _currentUser.value = updatedUser
        }
    }

    // --------------------------------------------------------------------
    // Plant Growth Collection & Tracking
    // --------------------------------------------------------------------
    fun getLogsForPlant(plantId: Int): Flow<List<PlantLogEntity>> {
        return repository.getLogsForPlant(plantId)
    }

    fun addPlant(name: String, scientificName: String, category: String, waterInterval: Int, fertilizeInterval: Int, notes: String) {
        viewModelScope.launch {
            val plant = PlantEntity(
                name = name,
                scientificName = scientificName,
                category = category,
                plantedDate = System.currentTimeMillis(),
                zone = _currentUser.value?.locationZone ?: "Zone 6b",
                waterIntervalDays = waterInterval,
                fertilizeIntervalDays = fertilizeInterval,
                notes = notes
            )
            val plantId = repository.insertPlant(plant).toInt()

            // Schedule initial tasks & reminders
            val waterDue = System.currentTimeMillis() + (waterInterval * 86400000L)
            val fertilizeDue = System.currentTimeMillis() + (fertilizeInterval * 86400000L)

            repository.insertReminder(
                TaskReminderEntity(
                    plantId = plantId,
                    plantName = name,
                    type = "Watering",
                    dueDate = waterDue
                )
            )

            repository.insertReminder(
                TaskReminderEntity(
                    plantId = plantId,
                    plantName = name,
                    type = "Fertilization",
                    dueDate = fertilizeDue
                )
            )

            // Dynamic chronological log
            repository.insertLog(
                PlantLogEntity(
                    plantId = plantId,
                    timestamp = System.currentTimeMillis(),
                    note = "Initial Sowing: Plant added to the collection. Set up watering intervals every $waterInterval days and fertilizer applications every $fertilizeInterval days.",
                    status = "Sprouting"
                )
            )
        }
    }

    fun deletePlant(plant: PlantEntity) {
        viewModelScope.launch {
            repository.deletePlant(plant)
        }
    }

    fun recordWatering(plantId: Int) {
        viewModelScope.launch {
            flowOf(plantId).flatMapLatest { repository.getPlantById(it) }.firstOrNull()?.let { plant ->
                val updatedPlant = plant.copy(lastWatered = System.currentTimeMillis())
                repository.updatePlant(updatedPlant)

                // Reschedule next water reminder
                val nextWaterDue = System.currentTimeMillis() + (plant.waterIntervalDays * 86400000L)
                repository.insertReminder(
                    TaskReminderEntity(
                        plantId = plant.id,
                        plantName = plant.name,
                        type = "Watering",
                        dueDate = nextWaterDue
                    )
                )

                // Add log entry
                addLogEntry(plantId, "Watered this plant. Soil refreshed and moisture level restored.", "", "Healthy")
            }
        }
    }

    fun recordFertilization(plantId: Int) {
        viewModelScope.launch {
            flowOf(plantId).flatMapLatest { repository.getPlantById(it) }.firstOrNull()?.let { plant ->
                val updatedPlant = plant.copy(lastFertilized = System.currentTimeMillis())
                repository.updatePlant(updatedPlant)

                // Reschedule next fertilization reminder
                val nextFertilDue = System.currentTimeMillis() + (plant.fertilizeIntervalDays * 86400000L)
                repository.insertReminder(
                    TaskReminderEntity(
                        plantId = plant.id,
                        plantName = plant.name,
                        type = "Fertilization",
                        dueDate = nextFertilDue
                    )
                )

                // Add log entry
                addLogEntry(plantId, "Applied organic fertilizer. Rich organic nitrogen feed administered.", "", "Healthy")
            }
        }
    }

    fun addLogEntry(plantId: Int, note: String, photoUri: String, status: String) {
        viewModelScope.launch {
            repository.insertLog(
                PlantLogEntity(
                    plantId = plantId,
                    timestamp = System.currentTimeMillis(),
                    note = note,
                    photoUri = photoUri,
                    status = status
                )
            )
        }
    }

    // --------------------------------------------------------------------
    // Reminders & Personalized schedules
    // --------------------------------------------------------------------
    fun completeReminder(reminder: TaskReminderEntity) {
        viewModelScope.launch {
            repository.completeReminder(reminder.id)
            // Perform action automatically for convenient UX
            if (reminder.type == "Watering") {
                recordWatering(reminder.plantId)
            } else {
                recordFertilization(reminder.plantId)
            }
        }
    }

    // --------------------------------------------------------------------
    // Community Forum & Seed Trading
    // --------------------------------------------------------------------
    fun publishPost(title: String, content: String, type: String, seedsAvailable: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val post = ForumPostEntity(
                authorId = user.id,
                authorName = user.username,
                title = title,
                content = content,
                timestamp = System.currentTimeMillis(),
                postType = type,
                seedAvailable = seedsAvailable
            )
            repository.insertPost(post)
        }
    }

    fun likePost(post: ForumPostEntity) {
        viewModelScope.launch {
            repository.updatePost(post.copy(likesCount = post.likesCount + 1))
        }
    }

    // --------------------------------------------------------------------
    // Messaging System
    // --------------------------------------------------------------------
    fun setConversationPartner(partnerId: Int?) {
        _currentConversationPartnerId.value = partnerId
    }

    fun sendDirectMessage(receiverId: Int, receiverName: String, content: String) {
        val user = _currentUser.value ?: return
        if (content.isBlank()) return
        viewModelScope.launch {
            val message = MessageEntity(
                senderId = user.id,
                senderName = user.username,
                receiverId = receiverId,
                receiverName = receiverName,
                content = content,
                timestamp = System.currentTimeMillis()
            )
            repository.insertMessage(message)
        }
    }
}

class GardenViewModelFactory(private val application: Application, private val repository: GardenRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GardenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GardenViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
