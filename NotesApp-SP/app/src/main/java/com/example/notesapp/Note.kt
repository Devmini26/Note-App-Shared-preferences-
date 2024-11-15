package com.example.notesapp

data class Note(
    val id: Int,
    val title: String,
    val content: String,
    val reminderDate: String? = null, // Optional reminder date
    val reminderTime: String? = null  // Optional reminder time
)
