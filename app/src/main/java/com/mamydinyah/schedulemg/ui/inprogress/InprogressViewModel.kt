package com.mamydinyah.schedulemg.ui.inprogress

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mamydinyah.schedulemg.data.Connection
import com.mamydinyah.schedulemg.data.Task
import com.mamydinyah.schedulemg.data.TaskRepository

class InprogressViewModel(application: Application) : ViewModel() {

    val tasksByStatusInProgress: LiveData<List<Task>>
    private val repository: TaskRepository

    init {
        val taskDao = Connection.getDatabase(application).taskDao()
        repository = TaskRepository(taskDao)
        tasksByStatusInProgress = repository.tasksByStatusInProgress()
    }

    fun deleteTaskById(id: Int) {
        repository.deleteTaskById(id)
    }
}