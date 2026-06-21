package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.example.data.GardenDatabase
import com.example.data.GardenRepository
import com.example.ui.GardenApp
import com.example.ui.GardenViewModel
import com.example.ui.GardenViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Hook up Room Database & Repository
        val database = GardenDatabase.getDatabase(this)
        val repository = GardenRepository(database)

        // Instantiate GardenViewModel with Factory support
        val factory = GardenViewModelFactory(application, repository)
        val viewModel = ViewModelProvider(this, factory)[GardenViewModel::class.java]

        setContent {
            GardenApp(viewModel = viewModel)
        }
    }
}
