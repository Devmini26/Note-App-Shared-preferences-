package com.example.notesapp

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class NotePreferencesHelper(context: Context) {

    companion object {
        private const val PREFS_NAME = "NotesPrefs"
        private const val NOTES_KEY = "notesList"
    }

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // Function to save a note
    fun saveNote(note: Note) {
        val notesList = getAllNotes().toMutableList()
        notesList.add(note)
        saveNotesToPreferences(notesList)
    }

    // Function to update a note
    fun updateNote(updatedNote: Note) {
        val notesList = getAllNotes().toMutableList()
        val index = notesList.indexOfFirst { it.id == updatedNote.id }
        if (index != -1) {
            notesList[index] = updatedNote
            saveNotesToPreferences(notesList)
        }
    }

    // Function to get all notes
    fun getAllNotes(): List<Note> {
        val json = sharedPreferences.getString(NOTES_KEY, null)
        return if (json != null) {
            val type = object : TypeToken<List<Note>>() {}.type
            Gson().fromJson(json, type)
        } else {
            emptyList()
        }
    }

    // Function to get a note by ID
    fun getNoteByID(id: Int): Note? {
        return getAllNotes().find { it.id == id }
    }

    // Function to delete a note
    fun deleteNote(noteId: Int) {
        val notesList = getAllNotes().toMutableList()
        notesList.removeAll { it.id == noteId }
        saveNotesToPreferences(notesList)
    }

    // Helper function to save the list of notes to SharedPreferences
    private fun saveNotesToPreferences(notesList: List<Note>) {
        val json = Gson().toJson(notesList)
        sharedPreferences.edit().putString(NOTES_KEY, json).apply()
    }
}
