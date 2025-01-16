package com.mamydinyah.schedulemg.crud

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.mamydinyah.schedulemg.R
import com.mamydinyah.schedulemg.data.Task
import com.mamydinyah.schedulemg.data.TaskRepository
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

class EditModal(private val task: Task, private val taskRepository: TaskRepository) : DialogFragment() {

    private lateinit var editTextTitle: EditText
    private lateinit var editTextDescription: EditText
    private lateinit var editTextDate: EditText
    private lateinit var editTextStartTime: EditText
    private lateinit var editTextEndTime: EditText
    private lateinit var buttonSubmit: Button
    private lateinit var buttonClose: ImageButton
    private lateinit var titleError: TextView
    private lateinit var descriptionError: TextView
    private lateinit var dateError: TextView
    private lateinit var startTimeError: TextView
    private lateinit var endTimeError: TextView

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(context).inflate(R.layout.fragment_edit_task, null)

        editTextTitle = view.findViewById(R.id.editTextTitle)
        editTextDescription = view.findViewById(R.id.editTextDescription)
        editTextDate = view.findViewById(R.id.editTextDate)
        editTextStartTime = view.findViewById(R.id.editTextStartTime)
        editTextEndTime = view.findViewById(R.id.editTextEndTime)
        buttonSubmit = view.findViewById(R.id.buttonSubmit)
        buttonClose = view.findViewById(R.id.buttonClose)
        titleError = view.findViewById(R.id.titleError)
        descriptionError = view.findViewById(R.id.descriptionError)
        dateError = view.findViewById(R.id.dateError)
        startTimeError = view.findViewById(R.id.startTimeError)
        endTimeError = view.findViewById(R.id.endTimeError)

        editTextTitle.setText(task.title)
        editTextDescription.setText(task.description)
        editTextDate.setText(task.date)
        editTextStartTime.setText(task.startTime)
        editTextEndTime.setText(task.endTime)

        editTextDate.setOnClickListener {
            showDatePickerDialog()
        }

        editTextStartTime.setOnClickListener {
            showTimePickerDialog(editTextStartTime)
        }

        editTextEndTime.setOnClickListener {
            showTimePickerDialog(editTextEndTime)
        }

        buttonSubmit.setOnClickListener {
            if (validateInputs()) {
                task.title = editTextTitle.text.toString()
                task.description = editTextDescription.text.toString()
                task.date = editTextDate.text.toString()
                task.startTime = editTextStartTime.text.toString()
                task.endTime = editTextEndTime.text.toString()

                updateTask(task)
                showToast("Task updated successfully")
                dismiss()
            }
        }

        buttonClose.setOnClickListener {
            dismiss()
        }

        val builder = AlertDialog.Builder(requireContext())
        builder.setView(view)
            .setCancelable(false)

        return builder.create()
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
            val selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
            editTextDate.setText(selectedDate)
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        datePickerDialog.show()
    }

    private fun showTimePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()
        val timePickerDialog = TimePickerDialog(requireContext(), { _, hourOfDay, minute ->
            val selectedTime = String.format("%02d:%02d", hourOfDay, minute)
            editText.setText(selectedTime)
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true)
        timePickerDialog.show()
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        titleError.visibility = View.GONE
        descriptionError.visibility = View.GONE
        dateError.visibility = View.GONE
        startTimeError.visibility = View.GONE
        endTimeError.visibility = View.GONE

        if (TextUtils.isEmpty(editTextTitle.text)) {
            titleError.visibility = View.VISIBLE
            isValid = false
        }

        if (TextUtils.isEmpty(editTextDescription.text)) {
            descriptionError.visibility = View.VISIBLE
            isValid = false
        }

        if (TextUtils.isEmpty(editTextDate.text)) {
            dateError.visibility = View.VISIBLE
            isValid = false
        }

        if (TextUtils.isEmpty(editTextStartTime.text)) {
            startTimeError.visibility = View.VISIBLE
            isValid = false
        }

        if (TextUtils.isEmpty(editTextEndTime.text)) {
            endTimeError.visibility = View.VISIBLE
            isValid = false
        }

        return isValid
    }

    private fun updateTask(task: Task) {
        Executors.newSingleThreadExecutor().execute {
            val status = getStatus(task.date, task.startTime, task.endTime)
            task.status = status

            taskRepository.updateTask(task)
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

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}