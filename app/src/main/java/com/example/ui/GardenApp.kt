package com.example.ui

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.*
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

enum class GardenTab {
    MyGarden,
    Guides,
    Forum,
    Messenger,
    Profile
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GardenApp(viewModel: GardenViewModel) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()
    val context = LocalContext.current

    MyApplicationTheme(darkTheme = isDarkMode) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            if (currentUser == null) {
                AuthScreen(
                    viewModel = viewModel,
                    isDarkMode = isDarkMode,
                    onToggleDarkMode = { viewModel.toggleDarkMode() }
                )
            } else {
                MainGardenShell(
                    viewModel = viewModel,
                    currentUser = currentUser!!,
                    isDarkMode = isDarkMode
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainGardenShell(
    viewModel: GardenViewModel,
    currentUser: UserEntity,
    isDarkMode: Boolean
) {
    var currentTab by remember { mutableStateOf(GardenTab.MyGarden) }
    var selectedPlantForLogs by remember { mutableStateOf<PlantEntity?>(null) }
    var showAddPlantDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Eco,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Urban Garden",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.toggleDarkMode() },
                        modifier = Modifier.semantics {
                            contentDescription = if (isDarkMode) "Disable dark mode" else "Enable dark mode"
                        }
                    ) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    if (currentTab == GardenTab.MyGarden) {
                        IconButton(onClick = { showAddPlantDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add plant"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                modifier = Modifier.shadow(4.dp)
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = currentTab == GardenTab.MyGarden,
                    onClick = {
                        selectedPlantForLogs = null
                        currentTab = GardenTab.MyGarden
                    },
                    icon = { Icon(Icons.Default.Grass, contentDescription = null) },
                    label = { Text("My Garden") },
                    modifier = Modifier.testTag("nav_my_garden")
                )
                NavigationBarItem(
                    selected = currentTab == GardenTab.Guides,
                    onClick = { currentTab = GardenTab.Guides },
                    icon = { Icon(Icons.Default.MenuBook, contentDescription = null) },
                    label = { Text("Guides") },
                    modifier = Modifier.testTag("nav_guides")
                )
                NavigationBarItem(
                    selected = currentTab == GardenTab.Forum,
                    onClick = { currentTab = GardenTab.Forum },
                    icon = { Icon(Icons.Default.Groups, contentDescription = null) },
                    label = { Text("Forum") },
                    modifier = Modifier.testTag("nav_forum")
                )
                NavigationBarItem(
                    selected = currentTab == GardenTab.Messenger,
                    onClick = { currentTab = GardenTab.Messenger },
                    icon = { Icon(Icons.Default.Chat, contentDescription = null) },
                    label = { Text("Chat") },
                    modifier = Modifier.testTag("nav_chat")
                )
                NavigationBarItem(
                    selected = currentTab == GardenTab.Profile,
                    onClick = { currentTab = GardenTab.Profile },
                    icon = { Icon(Icons.Default.AccountCircle, contentDescription = null) },
                    label = { Text("Profile") },
                    modifier = Modifier.testTag("nav_profile")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                }
            ) { tab ->
                when (tab) {
                    GardenTab.MyGarden -> {
                        if (selectedPlantForLogs != null) {
                            PlantLogsScreen(
                                viewModel = viewModel,
                                plant = selectedPlantForLogs!!,
                                onBack = { selectedPlantForLogs = null }
                            )
                        } else {
                            MyGardenScreen(
                                viewModel = viewModel,
                                onSelectPlantDetails = { selectedPlantForLogs = it },
                                onAddNewPlantClick = { showAddPlantDialog = true }
                            )
                        }
                    }
                    GardenTab.Guides -> {
                        GuidesScreen(viewModel = viewModel)
                    }
                    GardenTab.Forum -> {
                        ForumScreen(
                            viewModel = viewModel,
                            currentUser = currentUser,
                            onStartChat = { partnerId ->
                                viewModel.setConversationPartner(partnerId)
                                currentTab = GardenTab.Messenger
                            }
                        )
                    }
                    GardenTab.Messenger -> {
                        MessengerScreen(
                            viewModel = viewModel,
                            currentUser = currentUser
                        )
                    }
                    GardenTab.Profile -> {
                        ProfileScreen(viewModel = viewModel, user = currentUser)
                    }
                }
            }

            if (showAddPlantDialog) {
                AddPlantDialog(
                    onDismiss = { showAddPlantDialog = false },
                    onConfirm = { name, scientific, cat, water, fertil, notes ->
                        viewModel.addPlant(name, scientific, cat, water, fertil, notes)
                        showAddPlantDialog = false
                    }
                )
            }
        }
    }
}

// --------------------------------------------------------------------
// AUTHENTICATION & PRIVATE SIGN IN
// --------------------------------------------------------------------
@Composable
fun AuthScreen(
    viewModel: GardenViewModel,
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit
) {
    var isRegistering by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }

    val loginError by viewModel.loginError.collectAsStateWithLifecycle()
    val registerError by viewModel.registerError.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = if (isDarkMode) {
                        listOf(ForestDarkBg, Color(0xFF0C130D))
                    } else {
                        listOf(EmeraldLight, SoilClayWhiteBg)
                    }
                )
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(24.dp))
                .background(
                    if (isDarkMode) ForestCardDark else Color.White,
                    RoundedCornerShape(24.dp)
                )
                .padding(28.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Elegant Eco Logo
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.FilterVintage,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(44.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Urban Organic Terrace",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                ),
                textAlign = TextAlign.Center
            )

            Text(
                text = "Grow organic vegetables on your balcony",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            if (isRegistering) {
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("username_input"),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("email_input"),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Master Password") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("password_input"),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                shape = RoundedCornerShape(12.dp)
            )

            val errorMsg = if (isRegistering) registerError else loginError
            if (errorMsg != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = errorMsg!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (isRegistering) {
                        viewModel.register(username, email, password)
                    } else {
                        viewModel.login(email, password)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("submit_auth"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (isRegistering) "Create Secure Account" else "Sign In Securely",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = { isRegistering = !isRegistering },
                modifier = Modifier.testTag("toggle_auth_mode")
            ) {
                Text(
                    text = if (isRegistering) "Already have an account? Sign In" else "New to Terrace Gardening? Create Account",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Dark Mode Convenience Button
            IconButton(onClick = onToggleDarkMode) {
                Icon(
                    imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = "Toggle Theme Preview"
                )
            }
        }
    }
}

// --------------------------------------------------------------------
// SCREEN: MY GARDEN (PLANT COLLECTION + WATERING LOGS)
// --------------------------------------------------------------------
@Composable
fun MyGardenScreen(
    viewModel: GardenViewModel,
    onSelectPlantDetails: (PlantEntity) -> Unit,
    onAddNewPlantClick: () -> Unit
) {
    val plants by viewModel.plants.collectAsStateWithLifecycle()
    val activeReminders by viewModel.activeReminders.collectAsStateWithLifecycle()

    var selectedTabItem by remember { mutableStateOf(0) } // 0 = Plants, 1 = Reminders Scheduler

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Upper Tab Selector: Collection vs Schedules Reminders
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .background(
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    RoundedCornerShape(12.dp)
                )
                .padding(4.dp)
        ) {
            Button(
                onClick = { selectedTabItem = 0 },
                modifier = Modifier
                    .weight(1f)
                    .testTag("tab_my_plants"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedTabItem == 0) MaterialTheme.colorScheme.primary else Color.Transparent,
                    contentColor = if (selectedTabItem == 0) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                ),
                elevation = null,
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocalFlorist, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("My Plants (${plants.size})", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }

            Button(
                onClick = { selectedTabItem = 1 },
                modifier = Modifier
                    .weight(1f)
                    .testTag("tab_reminders"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedTabItem == 1) MaterialTheme.colorScheme.primary else Color.Transparent,
                    contentColor = if (selectedTabItem == 1) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                ),
                elevation = null,
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Schedules (${activeReminders.size})", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (selectedTabItem == 0) {
            if (plants.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Default.LocalFlorist,
                    title = "Your balcony is empty!",
                    description = "Start your organic terrace journey today by adding leafy greens, cherry tomatoes, or aromatic herbs.",
                    actionLabel = "Add Your First Plant",
                    onAction = onAddNewPlantClick
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(plants, key = { it.id }) { plant ->
                        PlantCardItem(
                            plant = plant,
                            onCardClick = { onSelectPlantDetails(plant) },
                            onWaterTap = { viewModel.recordWatering(plant.id) },
                            onFertilizeTap = { viewModel.recordFertilization(plant.id) }
                        )
                    }
                }
            }
        } else {
            // Reminders / Notification Schedules
            val currentReminders = activeReminders
            if (currentReminders.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Default.AlarmOn,
                    title = "All items hydrated!",
                    description = "No pending watering or fertilization tasks on your calendar. Your urban terrace is thriving.",
                    actionLabel = null,
                    onAction = {}
                )
            } else {
                Text(
                    text = "Personalized Water & Fertilizer Schedule Reminders",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(currentReminders) { reminder ->
                        TaskReminderItem(
                            reminder = reminder,
                            onCompleteClick = { viewModel.completeReminder(reminder) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PlantCardItem(
    plant: PlantEntity,
    onCardClick: () -> Unit,
    onWaterTap: () -> Unit,
    onFertilizeTap: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() }
            .testTag("plant_card_${plant.id}"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Circular icon representing plant category
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when (plant.category) {
                            "Vegetables" -> "🍅"
                            "Herbs" -> "🌿"
                            "Fruits" -> "🍓"
                            else -> "🌱"
                        },
                        fontSize = 24.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = plant.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Text(
                        text = plant.scientificName,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    AssistChip(
                        onClick = {},
                        label = { Text(plant.category) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                            labelColor = MaterialTheme.colorScheme.secondary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Timings / Water status
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Calculate next water timing
                val nextWaterDateMs = plant.lastWatered + (plant.waterIntervalDays * 86400000L)
                val isWaterOverdue = System.currentTimeMillis() > nextWaterDateMs

                Column(horizontalAlignment = Alignment.Start) {
                    Text("Next Watering", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    Text(
                        text = if (isWaterOverdue) "Overdue ⚠️" else SimpleDateFormat("MMM dd", Locale.getDefault()).format(Date(nextWaterDateMs)),
                        fontWeight = FontWeight.Bold,
                        color = if (isWaterOverdue) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                    )
                }

                Divider(modifier = Modifier.width(1.dp).height(32.dp), color = Color.LightGray)

                val nextFertilDateMs = plant.lastFertilized + (plant.fertilizeIntervalDays * 86400000L)
                val isFertilOverdue = System.currentTimeMillis() > nextFertilDateMs

                Column(horizontalAlignment = Alignment.Start) {
                    Text("Organic Fertilise", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    Text(
                        text = if (isFertilOverdue) "Overdue ⚠️" else SimpleDateFormat("MMM dd", Locale.getDefault()).format(Date(nextFertilDateMs)),
                        fontWeight = FontWeight.Bold,
                        color = if (isFertilOverdue) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onWaterTap,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.WaterDrop, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Water", fontSize = 13.sp)
                }

                Button(
                    onClick = onFertilizeTap,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Co2, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Fertilize", fontSize = 13.sp)
                }
            }
        }
    }
}

// --------------------------------------------------------------------
// CHRONOLOGICAL LOGS & MEASURED PHOTOS TRACKING
// --------------------------------------------------------------------
@Composable
fun PlantLogsScreen(
    viewModel: GardenViewModel,
    plant: PlantEntity,
    onBack: () -> Unit
) {
    val plantLogs by viewModel.getLogsForPlant(plant.id).collectAsStateWithLifecycle(initialValue = emptyList())
    val context = LocalContext.current

    var newLogText by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf("Healthy") }
    var photoUriSimulated by remember { mutableStateOf("") }

    val statusOptions = listOf("Healthy", "Sprouting", "Leafing", "Flowering", "Harvesting", "Struggling")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Go back")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "${plant.name} Growth Log",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Track progress notes and visual achievements",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Add Log Entry Form Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Add Daily Progress Entry",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = newLogText,
                        onValueChange = { newLogText = it },
                        placeholder = { Text("Write about height, leaves, blossoming, or harvest quantities...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Status Chips Selector
                    Text("Plant Status Cues", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        statusOptions.forEach { opt ->
                            FilterChip(
                                selected = selectedStatus == opt,
                                onClick = { selectedStatus = opt },
                                label = { Text(opt) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Camera Simulated Upload option requested
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (photoUriSimulated.isEmpty()) "Add Growth Picture" else "Picture Ready ✅",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Preset selections for crop photos simulated securely
                        Row {
                            TextButton(onClick = {
                                photoUriSimulated = "img_pot_macro"
                                Toast.makeText(context, "Balcony Macro loaded!", Toast.LENGTH_SHORT).show()
                            }) {
                                Text("Mock Balcony", fontSize = 11.sp)
                            }
                            TextButton(onClick = {
                                photoUriSimulated = "img_harvest"
                                Toast.makeText(context, "Harvest photo mock attached!", Toast.LENGTH_SHORT).show()
                            }) {
                                Text("Mock Harvest", fontSize = 11.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            if (newLogText.isNotBlank()) {
                                viewModel.addLogEntry(
                                    plantId = plant.id,
                                    note = newLogText,
                                    photoUri = photoUriSimulated,
                                    status = selectedStatus
                                )
                                newLogText = ""
                                photoUriSimulated = ""
                                Toast.makeText(context, "Growth Entry Saved!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Save To Chronological Log")
                    }
                }
            }
        }

        // Chronological History Title
        item {
            Text(
                text = "Milestones & Botanical History",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        if (plantLogs.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No logs added yet. Use the panel above to insert progress milestones, status changes, and pictures.", textAlign = TextAlign.Center, color = Color.Gray)
                }
            }
        } else {
            items(plantLogs, key = { it.id }) { log ->
                LogItemCard(log = log)
            }
        }

        // Action: delete plant option
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    viewModel.deletePlant(plant)
                    onBack()
                    Toast.makeText(context, "${plant.name} deleted", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.15f), contentColor = MaterialTheme.colorScheme.error),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Delete / Archive Plant")
            }
        }
    }
}

@Composable
fun LogItemCard(log: PlantLogEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = SimpleDateFormat("MMM dd, yyyy h:mm a", Locale.getDefault()).format(Date(log.timestamp)),
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                    color = Color.Gray
                )
                AssistChip(
                    onClick = {},
                    label = { Text(log.status) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = when (log.status) {
                            "Healthy" -> MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                            "Harvesting" -> SunRayYellow.copy(alpha = 0.15f)
                            "Struggling" -> MaterialTheme.colorScheme.error.copy(alpha = 0.12f)
                            else -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f)
                        },
                        labelColor = when (log.status) {
                            "Healthy" -> MaterialTheme.colorScheme.primary
                            "Harvesting" -> WarmTerracotta
                            "Struggling" -> MaterialTheme.colorScheme.error
                            else -> MaterialTheme.colorScheme.secondary
                        }
                    )
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = log.note,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (log.photoUri.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                // Render custom mock or generated graphic box beautifully
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(PureOrganicGreen.copy(alpha = 0.5f), MintAccent.copy(alpha = 0.3f))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (log.photoUri == "img_harvest") "🌾 Seasonal Harvest Record" else "🌱 Terrace Growth Photo Frame",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Active Photo Log Attachment", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.8f))
                    }
                }
            }
        }
    }
}

// --------------------------------------------------------------------
// REMINDERS ITEM COMPOSABLE
// --------------------------------------------------------------------
@Composable
fun TaskReminderItem(
    reminder: TaskReminderEntity,
    onCompleteClick: () -> Unit
) {
    val context = LocalContext.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (reminder.type == "Watering") {
                            Color(0xFFE3F2FD)
                        } else {
                            Color(0xFFFFF3E0)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (reminder.type == "Watering") Icons.Default.WaterDrop else Icons.Default.Co2,
                    contentDescription = null,
                    tint = if (reminder.type == "Watering") Color(0xFF1E88E5) else Color(0xFFF57C00),
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${reminder.type} required",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = reminder.plantName,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Due: " + SimpleDateFormat("MMM dd", Locale.getDefault()).format(Date(reminder.dueDate)),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            IconButton(
                onClick = {
                    onCompleteClick()
                    Toast.makeText(context, "${reminder.plantName} task checked off!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.testTag("resolve_reminder_${reminder.id}")
            ) {
                Icon(
                    imageVector = Icons.Outlined.CheckCircle,
                    contentDescription = "Complete task",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

// --------------------------------------------------------------------
// SCREEN: GUIDES TAB (SOIL PREP, PEST REMEDIES, VEG CALENDARS + SHOP)
// --------------------------------------------------------------------
@Composable
fun GuidesScreen(viewModel: GardenViewModel) {
    var activeSubGuide by remember { mutableStateOf(0) } // 0 = Soil Prep, 1 = Plant Crops, 2 = Pest Remedies, 3 = Store Essentials
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        ScrollableTabRow(
            selectedTabIndex = activeSubGuide,
            edgePadding = 0.dp,
            containerColor = Color.Transparent,
            divider = {}
        ) {
            Tab(selected = activeSubGuide == 0, onClick = { activeSubGuide = 0 }) {
                Text("Soil Prep", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
            }
            Tab(selected = activeSubGuide == 1, onClick = { activeSubGuide = 1 }) {
                Text("Crop Calendars", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
            }
            Tab(selected = activeSubGuide == 2, onClick = { activeSubGuide = 2 }) {
                Text("Pest Secrets", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
            }
            Tab(selected = activeSubGuide == 3, onClick = { activeSubGuide = 3 }) {
                Text("Affiliate Store", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        when (activeSubGuide) {
            0 -> {
                // Soil Prep Screen
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("Terace Lightweight Constraints", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Standard agricultural soil holds thick moisture but compacts and overloads balcony slabs. Lightweight medium composition is highly required of urban enthusiasts.", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    items(GardeningGuides.SOIL_PREPARATION_STAGES) { stage ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = stage.first,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = stage.second, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
            1 -> {
                // Crop Calendars with ADD direct shortcut
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    item {
                        Text(
                            text = "Interactive Sowing Guide. Tap SOW to trace directly in your Garden!",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }

                    items(GardeningGuides.SOWING_GUIDES) { guide ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(guide.icon, fontSize = 32.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(guide.cropName, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                        Text("Sowing: ${guide.sowingTime}", style = MaterialTheme.typography.bodySmall)
                                    }
                                    Button(
                                        onClick = {
                                            viewModel.addPlant(
                                                name = guide.cropName,
                                                scientificName = "Organic Balcony Variant",
                                                category = guide.category,
                                                waterInterval = if (guide.waterNeed == "High") 2 else 4,
                                                fertilizeInterval = 14,
                                                notes = "Sown directly from Seasonal Guides tab. " + guide.soilHint
                                            )
                                            Toast.makeText(context, "${guide.cropName} added to tracking!", Toast.LENGTH_SHORT).show()
                                        },
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("SOW", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                Divider()
                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("Best Zone Cues", fontSize = 11.sp, color = Color.Gray)
                                        Text(guide.bestZone, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Column {
                                        Text("Days To Harvest", fontSize = 11.sp, color = Color.Gray)
                                        Text("${guide.daysToHarvest} days", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    }
                                    Column {
                                        Text("Depth Guide", fontSize = 11.sp, color = Color.Gray)
                                        Text(guide.depthHint, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "💡 Advice: ${guide.soilHint}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
            2 -> {
                // Organic Pest Secrets
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(GardeningGuides.ORGANIC_PEST_GUIDES) { pest ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "🐛 ${pest.pestName}",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.error
                                )
                                Text(text = pest.description, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)

                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(6.dp))
                                        .padding(8.dp)
                                ) {
                                    Column {
                                        Text("Organic Remedy: ${pest.organicRemedy}", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold), color = MaterialTheme.colorScheme.primary)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(pest.remedyRecipe, style = MaterialTheme.typography.bodyMedium)
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "🛡️ Preventive: ${pest.preventiveMeasure}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
            3 -> {
                // Affiliate Store Supplies
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    item {
                        Text(
                            text = "Affiliate Urban Gardening Supplies. Tap buy to purchase required materials directly (Simulated Links).",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }

                    items(GardeningGuides.AFFILIATE_PRODUCTS) { prod ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = prod.name,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = prod.priceEstimate,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Category: ${prod.category} | ${prod.importance}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.secondary,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(8.dp))
                                Text(prod.description, style = MaterialTheme.typography.bodyMedium)

                                Spacer(modifier = Modifier.height(10.dp))
                                Button(
                                    onClick = {
                                        Toast.makeText(context, "Redirecting to supply affiliate: ${prod.productUrl}", Toast.LENGTH_LONG).show()
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Buy Essential Material Now")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// --------------------------------------------------------------------
// SCREEN: COMMUNITY FORUM & SEED SWAP
// --------------------------------------------------------------------
@Composable
fun ForumScreen(
    viewModel: GardenViewModel,
    currentUser: UserEntity,
    onStartChat: (Int) -> Unit
) {
    val posts by viewModel.forumPosts.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var activeCategoryFilter by remember { mutableStateOf("All") } // "All", "General", "Seed Swap", "Organic Advice"
    var showCreatePostDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Upper banner with action
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Social Share & Swap Hub",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = Color.White)
                )
                Text(
                    text = "Trade seeds with nearby balcony gardeners, post questions, and share progress updates.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { showCreatePostDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.PostAdd, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Publish Garden Progression Post")
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Categories selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("All", "Seed Swap", "Organic Advice", "General").forEach { cat ->
                FilterChip(
                    selected = activeCategoryFilter == cat,
                    onClick = { activeCategoryFilter = cat },
                    label = { Text(cat) }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        val filteredPosts = if (activeCategoryFilter == "All") posts else posts.filter { it.postType == activeCategoryFilter }

        if (filteredPosts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text("No progressive posts in this sub-forum. Be the first to start a conversation!", textAlign = TextAlign.Center, color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredPosts) { post ->
                    ForumPostCardItem(
                        post = post,
                        currentUser = currentUser,
                        onLikeClick = { viewModel.likePost(post) },
                        onChatClick = { authorId -> onStartChat(authorId) }
                    )
                }
            }
        }

        if (showCreatePostDialog) {
            CreateForumPostDialog(
                onDismiss = { showCreatePostDialog = false },
                onPublish = { title, content, type, seeds ->
                    viewModel.publishPost(title, content, type, seeds)
                    showCreatePostDialog = false
                    Toast.makeText(context, "Progress Published!", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}

@Composable
fun ForumPostCardItem(
    post: ForumPostEntity,
    currentUser: UserEntity,
    onLikeClick: () -> Unit,
    onChatClick: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(post.authorName.take(1).uppercase(), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(post.authorName, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                        Text(SimpleDateFormat("MMM dd, h:mm a", Locale.getDefault()).format(Date(post.timestamp)), style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                }

                // Type Badge
                AssistChip(
                    onClick = {},
                    label = { Text(post.postType) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (post.postType == "Seed Swap") SunRayYellow.copy(alpha = 0.2f) else MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(post.title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            Spacer(modifier = Modifier.height(6.dp))
            Text(post.content, style = MaterialTheme.typography.bodyMedium)

            if (post.seedAvailable.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SunRayYellow.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                        .border(1.dp, SunRayYellow.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.SwapCalls, contentDescription = null, tint = WarmTerracotta)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Seed Offer: ${post.seedAvailable}",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = WarmTerracotta)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Divider()
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row {
                    IconButton(onClick = onLikeClick) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ThumbUp, contentDescription = "Like", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("${post.likesCount}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Show "Direct Connect / Ask Trade" if it's not our own post
                if (post.authorId != currentUser.id) {
                    TextButton(
                        onClick = { onChatClick(post.authorId) },
                        modifier = Modifier.testTag("chat_author_${post.authorId}")
                    ) {
                        Icon(Icons.Default.Message, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (post.postType == "Seed Swap") "Inquire About Swap" else "Direct Message Advice",
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Text("Your progression post", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }
        }
    }
}

// --------------------------------------------------------------------
// SCREEN: DIRECT COLLABORATIVE MESSENGER
// --------------------------------------------------------------------
@Composable
fun MessengerScreen(
    viewModel: GardenViewModel,
    currentUser: UserEntity
) {
    val messages by viewModel.messages.collectAsStateWithLifecycle()
    val activePartnerId by viewModel.currentConversationPartnerId.collectAsStateWithLifecycle()
    val members by viewModel.communityMembers.collectAsStateWithLifecycle()

    var textInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Aggregate unique conversation threads from all messages plus other members
    val conversationThreads = remember(messages, members) {
        val uniquePartnerIds = (messages.map { if (it.senderId == currentUser.id) it.receiverId else it.senderId } +
                members.map { it.id }).distinct().filter { it != currentUser.id }
        
        uniquePartnerIds.map { partnerId ->
            val conversation = messages.filter { (it.senderId == partnerId && it.receiverId == currentUser.id) || (it.senderId == currentUser.id && it.receiverId == partnerId) }
            val latestMessage = conversation.lastOrNull()
            val partnerName = members.find { it.id == partnerId }?.username ?: "Community Member #$partnerId"
            
            PartnerThread(
                partnerId = partnerId,
                partnerName = partnerName,
                latestMessage = latestMessage?.content ?: "Start a dialogue to trade seeds!",
                latestTimestamp = latestMessage?.timestamp ?: System.currentTimeMillis()
            )
        }.sortedByDescending { it.latestTimestamp }
    }

    Row(modifier = Modifier.fillMaxSize()) {
        // If no active thread selected, show conversation thread list
        if (activePartnerId == null) {
            Column(modifier = Modifier.weight(1f).padding(16.dp)) {
                Text(
                    text = "Collaborating Guild Messages",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Connect securely with local organic gardeners to barter seeds or ask about gardening pests.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                if (conversationThreads.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No active chats yet. Go to Forums or find members below to start direct seed bartering!", textAlign = TextAlign.Center)
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(conversationThreads) { thread ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.setConversationPartner(thread.partnerId) }
                                    .testTag("thread_item_${thread.partnerId}"),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(thread.partnerName.take(1).uppercase(), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(thread.partnerName, fontWeight = FontWeight.Bold)
                                        Text(thread.latestMessage, maxLines = 1, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                    }
                                    Icon(Icons.Default.ArrowForwardIos, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color.LightGray)
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Inside active conversation thread
            val activePartner = members.find { it.id == activePartnerId }
            val partnerName = activePartner?.username ?: "Terrace Farmer #$activePartnerId"
            val conversation = messages.filter { (it.senderId == activePartnerId && it.receiverId == currentUser.id) || (it.senderId == currentUser.id && it.receiverId == activePartnerId) }

            // Scroll to bottom whenever a new message is loaded
            LaunchedEffect(conversation.size) {
                if (conversation.isNotEmpty()) {
                    listState.animateScrollToItem(conversation.lastIndex)
                }
            }

            Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                // Thread Back Header
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { viewModel.setConversationPartner(null) }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Exit chat")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(partnerName.take(1).uppercase(), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(partnerName, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        Text(activePartner?.locationZone ?: "Balcony Collective member", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                }

                Divider()

                // List messages
                LazyColumn(
                    state = listState,
                    modifier = Modifier.weight(1f).padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(conversation) { msg ->
                        val isMe = msg.senderId == currentUser.id
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                        ) {
                            Box(
                                modifier = Modifier
                                    .widthIn(max = 260.dp)
                                    .clip(RoundedCornerShape(
                                        topStart = 12.dp,
                                        topEnd = 12.dp,
                                        bottomStart = if (isMe) 12.dp else 0.dp,
                                        bottomEnd = if (isMe) 0.dp else 12.dp
                                    ))
                                    .background(
                                        if (isMe) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                                    )
                                    .padding(12.dp)
                            ) {
                                Column {
                                    Text(
                                        text = msg.content,
                                        color = if (isMe) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(msg.timestamp)),
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp),
                                        color = (if (isMe) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant).copy(alpha = 0.6f),
                                        modifier = Modifier.align(Alignment.End)
                                    )
                                }
                            }
                        }
                    }
                }

                // Send Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = textInput,
                        onValueChange = { textInput = it },
                        placeholder = { Text("Write message here...") },
                        modifier = Modifier.weight(1f).testTag("chat_input"),
                        shape = RoundedCornerShape(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    FloatingActionButton(
                        onClick = {
                            if (textInput.isNotBlank()) {
                                viewModel.sendDirectMessage(activePartnerId!!, partnerName, textInput)
                                textInput = ""
                            }
                        },
                        modifier = Modifier.size(48.dp).testTag("chat_send_btn"),
                        shape = CircleShape,
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White)
                    }
                }
            }
        }
    }
}

data class PartnerThread(
    val partnerId: Int,
    val partnerName: String,
    val latestMessage: String,
    val latestTimestamp: Long
)

// --------------------------------------------------------------------
// SCREEN: USER PROFILE & PROGRESS PICTURE ARCHIEVEMENTS
// --------------------------------------------------------------------
@Composable
fun ProfileScreen(viewModel: GardenViewModel, user: UserEntity) {
    val context = LocalContext.current

    // Mutable states for profile configuration
    var editUsername by remember { mutableStateOf(user.username) }
    var editZone by remember { mutableStateOf(user.locationZone) }
    var editProjTitle by remember { mutableStateOf(user.favoriteProjectTitle) }
    var editProjPhoto by remember { mutableStateOf(user.favoriteProjectPhotoUri) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Rounded avatar display
        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = editUsername.take(1).uppercase(),
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Text(
            text = user.email,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )

        Divider()

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Edit Gardener Account", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)

                OutlinedTextField(
                    value = editUsername,
                    onValueChange = { editUsername = it },
                    label = { Text("Display Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = editZone,
                    onValueChange = { editZone = it },
                    label = { Text("Farming Zone Locator") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Favorite Balcony Accomplishment", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)
                Text("Describe your most beloved terrace garden achievement to display to the organic community.", style = MaterialTheme.typography.bodySmall, color = Color.Gray)

                OutlinedTextField(
                    value = editProjTitle,
                    onValueChange = { editProjTitle = it },
                    label = { Text("Project Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Render mock photo upload status if attached
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (editProjPhoto.isEmpty()) "No project picture added" else "Achievement Photo attached! 📸",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = {
                        editProjPhoto = "img_favorite_balcony"
                        Toast.makeText(context, "Balcony project photo chosen!", Toast.LENGTH_SHORT).show()
                    }) {
                        Text("Mock Balcony Pic", fontSize = 11.sp)
                    }
                }

                if (editProjPhoto.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(LeafSecondary.copy(alpha = 0.5f), SunRayYellow.copy(alpha = 0.4f))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🏆 $editProjTitle Preview", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Button(
            onClick = {
                viewModel.updateProfile(editUsername, editZone, editProjTitle, editProjPhoto)
                Toast.makeText(context, "Account preferences saved!", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Update Profile Achievements")
        }

        // Logout Secure option
        Spacer(modifier = Modifier.height(12.dp))
        TextButton(onClick = { viewModel.logout() }) {
            Icon(Icons.Default.ExitToApp, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Logout & Lock Data", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
        }
    }
}

// --------------------------------------------------------------------
// COMPOSABLE COMPONENT: EMPTY STATE ACTION VIEW
// --------------------------------------------------------------------
@Composable
fun EmptyStateView(
    icon: ImageVector,
    title: String,
    description: String,
    actionLabel: String?,
    onAction: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray),
            textAlign = TextAlign.Center
        )

        if (actionLabel != null) {
            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = onAction) {
                Text(actionLabel)
            }
        }
    }
}

// --------------------------------------------------------------------
// DIALOG: ADD PLANT TO COLLECTION
// --------------------------------------------------------------------
@Composable
fun AddPlantDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, scientific: String, category: String, waterInterval: Int, fertilizeInterval: Int, notes: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var scientific by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Vegetables") }
    var waterInterval by remember { mutableStateOf("3") }
    var fertilizeInterval by remember { mutableStateOf("14") }
    var notes by remember { mutableStateOf("") }

    val categoriesList = listOf("Vegetables", "Herbs", "Fruits", "Flowers")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Crop To Balcony Tracker", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Plant Name (e.g. Cherry Tomato)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("add_plant_name_input")
                )

                OutlinedTextField(
                    value = scientific,
                    onValueChange = { scientific = it },
                    label = { Text("Scientific Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Category Selector
                Text("Select Category", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categoriesList.forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat) }
                        )
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = waterInterval,
                        onValueChange = { waterInterval = it },
                        label = { Text("Water Every (Days)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = fertilizeInterval,
                        onValueChange = { fertilizeInterval = it },
                        label = { Text("Fertilize (Days)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Starting Notes / Soil Mix descriptions") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val waterInt = waterInterval.toIntOrNull() ?: 3
                        val fertilInt = fertilizeInterval.toIntOrNull() ?: 14
                        onConfirm(name, scientific, category, waterInt, fertilInt, notes)
                    }
                },
                modifier = Modifier.testTag("confirm_add_plant_btn")
            ) {
                Text("Add Crop")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// --------------------------------------------------------------------
// DIALOG: CREATE COMMUNITY FORUM PROGRESS POST
// --------------------------------------------------------------------
@Composable
fun CreateForumPostDialog(
    onDismiss: () -> Unit,
    onPublish: (title: String, content: String, type: String, seedsAvailable: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("General") } // "General", "Seed Swap", "Organic Advice"
    var seedAvailable by remember { mutableStateOf("") }

    val postTypes = listOf("General", "Seed Swap", "Organic Advice")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Community Post", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Post Headline") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("post_title_input")
                )

                // Type selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    postTypes.forEach { pt ->
                        FilterChip(
                            selected = type == pt,
                            onClick = { type = pt },
                            label = { Text(pt) }
                        )
                    }
                }

                if (type == "Seed Swap") {
                    OutlinedTextField(
                        value = seedAvailable,
                        onValueChange = { seedAvailable = it },
                        label = { Text("What seeds are you offering for trade?") },
                        placeholder = { Text("e.g. 5x Purple Basil pods, Cherry tomato seeds") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("post_seeds_input")
                    )
                }

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Describe progress, harvest logs, or ask for terrace advice...") },
                    maxLines = 5,
                    modifier = Modifier.fillMaxWidth().height(120.dp).testTag("post_content_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && content.isNotBlank()) {
                        onPublish(title, content, type, if (type == "Seed Swap") seedAvailable else "")
                    }
                },
                modifier = Modifier.testTag("publish_post_btn")
            ) {
                Text("Publish Progress")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
