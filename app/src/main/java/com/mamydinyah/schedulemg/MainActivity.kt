package com.mamydinyah.schedulemg

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.mamydinyah.schedulemg.data.Connection
import com.mamydinyah.schedulemg.data.Task
import com.mamydinyah.schedulemg.data.TaskRepository
import com.mamydinyah.schedulemg.databinding.ActivityMainBinding
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {


    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var taskRepository: TaskRepository
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate called")
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val database = Connection.getDatabase(this)
        val taskDao = database.taskDao()
        taskRepository = TaskRepository(taskDao)

        // Initialisation du SharedPreferences
        sharedPreferences = getSharedPreferences("theme_prefs", MODE_PRIVATE)
        val isDarkMode = sharedPreferences.getBoolean("isDarkMode", false)
        applyTheme(isDarkMode)

        // Ajout du listener pour le ToggleButton dans la Toolbar
        val themeToggleButton = findViewById<ToggleButton>(R.id.themeActionButton)
        themeToggleButton.isChecked = isDarkMode
        themeToggleButton.setOnCheckedChangeListener { _, isChecked ->
            applyTheme(isChecked)
            saveThemePreference(isChecked)
        }

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.nav_home, R.id.nav_all, R.id.nav_todo, R.id.nav_inprogress, R.id.nav_finished, R.id.nav_settings, R.id.nav_about),
            binding.drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.nav_home, R.id.nav_all -> binding.appBarMain.fab.show()
                else -> binding.appBarMain.fab.hide()
            }
        }

        binding.appBarMain.fab.setOnClickListener {
            showFormDialog()
        }

        taskRepository.updateTaskStatus()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun applyTheme(isDarkMode: Boolean) {
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    private fun saveThemePreference(isDarkMode: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("isDarkMode", isDarkMode)
        editor.apply()
    }

   /* private fun showFormDialog() {
        val inflater: LayoutInflater = layoutInflater
        val view: View = inflater.inflate(R.layout.form_layout, null)

        val dialog = AlertDialog.Builder(this)
            .setView(view)
            .create()

        val buttonClose: ImageButton = view.findViewById(R.id.buttonClose)
        val editTextDate: EditText = view.findViewById(R.id.editTextDate)
        val editTextTime: EditText = view.findViewById(R.id.editTextTime)

        val calendar = Calendar.getInstance()

        val dateFormatter = SimpleDateFormat("dd - MM - yyyy", Locale.getDefault())
        val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())

        editTextDate.setText(dateFormatter.format(calendar.time))
        editTextTime.setText(timeFormatter.format(calendar.time))

        // Date picker
        editTextDate.setOnClickListener {
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                calendar.set(Calendar.YEAR, selectedYear)
                calendar.set(Calendar.MONTH, selectedMonth)
                calendar.set(Calendar.DAY_OF_MONTH, selectedDay)
                editTextDate.setText(dateFormatter.format(calendar.time))
            }, year, month, day).show()
        }

        // Time picker
        editTextTime.setOnClickListener {
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            TimePickerDialog(this, { _, selectedHour, selectedMinute ->
                calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
                calendar.set(Calendar.MINUTE, selectedMinute)
                editTextTime.setText(timeFormatter.format(calendar.time))
            }, hour, minute, true).show()
        }

        buttonClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }*/

    private fun showFormDialog() {
        val inflater: LayoutInflater = layoutInflater
        val view: View = inflater.inflate(R.layout.form_layout, null)

        val dialog = AlertDialog.Builder(this)
            .setView(view)
            .create()

        val buttonClose: ImageButton = view.findViewById(R.id.buttonClose)
        val buttonSubmit: Button = view.findViewById(R.id.buttonSubmit)
        val editTextTitle: EditText = view.findViewById(R.id.editTextTitle)
        val editTextDescription: EditText = view.findViewById(R.id.editTextDescription)
        val editTextDate: EditText = view.findViewById(R.id.editTextDate)
        val editTextStartTime: EditText = view.findViewById(R.id.editTextStartTime)
        val editTextEndTime: EditText = view.findViewById(R.id.editTextEndTime)
        val titleError: TextView = view.findViewById(R.id.titleError)
        val descriptionError: TextView = view.findViewById(R.id.descriptionError)
        val dateError: TextView = view.findViewById(R.id.dateError)
        val startTimeError: TextView = view.findViewById(R.id.startTimeError)
        val endTimeError: TextView = view.findViewById(R.id.endTimeError)

        val calendar = Calendar.getInstance()
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())

        editTextDate.setText(dateFormatter.format(calendar.time))
        editTextStartTime.setText(timeFormatter.format(calendar.time))
        editTextEndTime.setText(timeFormatter.format(calendar.time))

        editTextDate.setOnClickListener {
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                calendar.set(Calendar.YEAR, selectedYear)
                calendar.set(Calendar.MONTH, selectedMonth)
                calendar.set(Calendar.DAY_OF_MONTH, selectedDay)
                editTextDate.setText(dateFormatter.format(calendar.time))
                dateError.visibility = View.GONE
            }, year, month, day).show()
        }

        editTextStartTime.setOnClickListener {
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            TimePickerDialog(this, { _, selectedHour, selectedMinute ->
                calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
                calendar.set(Calendar.MINUTE, selectedMinute)
                editTextStartTime.setText(timeFormatter.format(calendar.time))
                startTimeError.visibility = View.GONE
            }, hour, minute, true).show()
        }

        editTextEndTime.setOnClickListener {
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)
            val selectedStartTime = timeFormatter.parse(editTextStartTime.text.toString())

            TimePickerDialog(this, { _, selectedHour, selectedMinute ->
                calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
                calendar.set(Calendar.MINUTE, selectedMinute)
                val selectedEndTime = calendar.time

                if (selectedEndTime.after(selectedStartTime)) {
                    editTextEndTime.setText(timeFormatter.format(selectedEndTime))
                    endTimeError.visibility = View.GONE
                } else {
                    endTimeError.visibility = View.VISIBLE
                }
            }, hour, minute, true).show()
        }

        buttonClose.setOnClickListener {
            dialog.dismiss()
        }

        buttonSubmit.setOnClickListener {
            val title = editTextTitle.text.toString().trim()
            val description = editTextDescription.text.toString().trim()
            val date = editTextDate.text.toString().trim()
            val startTime = editTextStartTime.text.toString().trim()
            val endTime = editTextEndTime.text.toString().trim()

            var isValid = true

            if (title.isEmpty()) {
                titleError.visibility = View.VISIBLE
                titleError.text = "Title is required"
                isValid = false
            } else {
                titleError.visibility = View.GONE
            }

            if (description.isEmpty()) {
                descriptionError.visibility = View.VISIBLE
                descriptionError.text = "Description is required"
                isValid = false
            } else {
                descriptionError.visibility = View.GONE
            }

            if (date.isEmpty()) {
                dateError.visibility = View.VISIBLE
                dateError.text = "Date is required"
                isValid = false
            } else {
                dateError.visibility = View.GONE
            }

            if (startTime.isEmpty()) {
                startTimeError.visibility = View.VISIBLE
                startTimeError.text = "Start time is required"
                isValid = false
            } else {
                startTimeError.visibility = View.GONE
            }

            if (endTime.isEmpty() || endTime <= startTime) {
                endTimeError.visibility = View.VISIBLE
                endTimeError.text = "End time must be after start time"
                isValid = false
            } else {
                endTimeError.visibility = View.GONE
            }

            if (isValid) {
                val task = Task(
                    id = 0,
                    title = title,
                    description = description,
                    date = date,
                    startTime = startTime,
                    endTime = endTime,
                    status = getStatus(date, startTime, endTime)
                )
                insertTask(task)
                dialog.dismiss()
                Toast.makeText(this, "Task saved!", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun insertTask(task: Task) {
        Executors.newSingleThreadExecutor().execute {
            taskRepository.insertTask(task)
        }
    }

    private fun getStatus(date: String, startTime: String, endTime: String): String {
        val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val currentDateTime = Calendar.getInstance()

        return try {
            val taskStartDateTime = Calendar.getInstance()
            val taskEndDateTime = Calendar.getInstance()
            taskStartDateTime.time = dateTimeFormat.parse("$date $startTime")
            taskEndDateTime.time = dateTimeFormat.parse("$date $endTime")

            when {
                taskEndDateTime.before(currentDateTime) -> "finished"
                taskStartDateTime.after(currentDateTime) -> "to do"
                taskStartDateTime.before(currentDateTime) && taskEndDateTime.after(currentDateTime) -> "in progress"
                else -> "unknown"
            }
        } catch (e: ParseException) {
            e.printStackTrace()
            "unknown"
        }
    }
}
