package com.example.notesapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.notesapp.databinding.ActivityUpdateNoteBinding
import java.util.*

class UpdateNoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateNoteBinding
    private lateinit var preferencesHelper: NotePreferencesHelper
    private var noteId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferencesHelper = NotePreferencesHelper(this)

        // Get note ID from the intent
        noteId = intent.getIntExtra("note_id", -1)
        if (noteId == -1) {
            finish() // Exit if the note ID is invalid
            return
        }

        // Get the note from SharedPreferences by its ID
        val note = preferencesHelper.getNoteByID(noteId)
        if (note == null) {
            Toast.makeText(this, "Note not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Populate the fields with existing note data
        binding.updateTitleEditText.setText(note.title)
        binding.updateContentEditText.setText(note.content)

        // Set the saved reminder date and time if available
        if (!note.reminderDate.isNullOrEmpty()) {
            binding.updateBtnDate.text = note.reminderDate
        }
        if (!note.reminderTime.isNullOrEmpty()) {
            binding.updateBtnTime.text = note.reminderTime
        }

        // Handle save button click
        binding.updateSaveButton.setOnClickListener {
            val newTitle = binding.updateTitleEditText.text.toString()
            val newContent = binding.updateContentEditText.text.toString()
            val updatedReminderDate = binding.updateBtnDate.text.toString()
            val updatedReminderTime = binding.updateBtnTime.text.toString()

            val updatedNote = Note(
                noteId,
                newTitle,
                newContent,
                updatedReminderDate.takeIf { it != "Set Date" },
                updatedReminderTime.takeIf { it != "Set Time" }
            )

            // Update the note in SharedPreferences
            preferencesHelper.updateNote(updatedNote)
            Toast.makeText(this, "Changes Saved Successfully", Toast.LENGTH_SHORT).show()
            finish() // Close the activity after saving
        }

        // Date button listener
        binding.updateBtnDate.setOnClickListener {
            showDatePickerDialog()
        }

        // Time button listener
        binding.updateBtnTime.setOnClickListener {
            showTimePickerDialog()
        }
    }

    // Function to show DatePickerDialog
    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val date = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                binding.updateBtnDate.text = date
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    // Function to show TimePickerDialog
    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            { _, selectedHour, selectedMinute ->
                val time = String.format("%02d:%02d", selectedHour, selectedMinute)
                binding.updateBtnDate.text = time
            },
            hour, minute, true
        )
        timePickerDialog.show()
    }
}
