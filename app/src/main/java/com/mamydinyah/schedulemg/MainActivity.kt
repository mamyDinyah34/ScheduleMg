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

        binding.appBarMain.fab.setOnClickListener {
            showFormDialog()
        }

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.nav_home, R.id.nav_todo, R.id.nav_inprogress, R.id.nav_finished),
            drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
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
        val editTextTime: EditText = view.findViewById(R.id.editTextTime)

        val calendar = Calendar.getInstance()
        val dateFormatter = SimpleDateFormat("dd - MM - yyyy", Locale.getDefault())
        val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())

        editTextDate.setText(dateFormatter.format(calendar.time))
        editTextTime.setText(timeFormatter.format(calendar.time))

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

        buttonSubmit.setOnClickListener {
            val title = editTextTitle.text.toString().trim()
            val description = editTextDescription.text.toString().trim()
            val date = editTextDate.text.toString().trim()
            val time = editTextTime.text.toString().trim()

            if (title.isEmpty() || description.isEmpty() || date.isEmpty() || time.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else {
                val task = Task(
                    id = 0,
                    title = title,
                    description = description,
                    date = date,
                    time = time,
                    isFinished = false
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

}
