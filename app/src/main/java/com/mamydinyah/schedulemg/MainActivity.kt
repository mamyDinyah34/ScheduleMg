package com.mamydinyah.schedulemg

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.mamydinyah.schedulemg.crud.TaskManager
import com.mamydinyah.schedulemg.data.Connection
import com.mamydinyah.schedulemg.data.TaskRepository
import com.mamydinyah.schedulemg.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {


    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var taskRepository: TaskRepository
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var taskManager: TaskManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate called")
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val database = Connection.getDatabase(this)
        val taskDao = database.taskDao()
        taskRepository = TaskRepository(taskDao)

        taskManager = TaskManager(this, taskRepository)

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
            taskManager.showTaskCreationDialog()
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
}
