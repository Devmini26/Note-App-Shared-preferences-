package com.example.notesapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.notesapp.databinding.ActivityAddNoteBinding
import java.util.*

class AddNoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddNoteBinding
    private lateinit var preferencesHelper: NotePreferencesHelper
    private var isStopwatchRunning = false
    private var stopwatchTime = 0L // To track the seconds passed
    private val handler = Handler(Looper.getMainLooper())
    private val stopwatchRunnable = object : Runnable {
        override fun run() {
            stopwatchTime++
            val hours = stopwatchTime / 3600
            val minutes = (stopwatchTime % 3600) / 60
            val seconds = stopwatchTime % 60
            val time = String.format("%02d:%02d:%02d", hours, minutes, seconds)
            binding.StopWatch.text = time // Update the button text with the stopwatch time
            handler.postDelayed(this, 1000) // Update the time every second
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferencesHelper = NotePreferencesHelper(this)

        // Save button listener
        binding.saveButton.setOnClickListener {
            val title = binding.titleEditText.text.toString()
            val content = binding.contentEditText.text.toString()
            val reminderDate = binding.btnDate.text.toString()
            val reminderTime = binding.btnTime.text.toString()

            val note = Note(
                System.currentTimeMillis().toInt(),
                title,
                content,
                reminderDate.takeIf { it != "Set Date" }, // Save only if date is set
                reminderTime.takeIf { it != "Set Time" }  // Save only if time is set
            )

            preferencesHelper.saveNote(note)
            finish()
            Toast.makeText(this, "Note Saved", Toast.LENGTH_SHORT).show()
        }

        // Date button listener
        binding.btnDate.setOnClickListener {
            showDatePickerDialog()
        }

        // Time button listener
        binding.btnTime.setOnClickListener {
            showTimePickerDialog()
        }

        // Stopwatch button listener
        binding.StopWatch.setOnClickListener {
            toggleStopwatch()
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
                binding.btnDate.text = date
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
                binding.btnTime.text = time
            },
            hour, minute, true
        )
        timePickerDialog.show()
    }

    // Function to toggle the stopwatch
    private fun toggleStopwatch() {
        if (isStopwatchRunning) {
            stopStopwatch()
        } else {
            startStopwatch()
        }
    }

    // Function to start the stopwatch
    private fun startStopwatch() {
        isStopwatchRunning = true
        handler.post(stopwatchRunnable)
    }

    // Function to stop the stopwatch
    private fun stopStopwatch() {
        isStopwatchRunning = false
        handler.removeCallbacks(stopwatchRunnable)
    }
}
