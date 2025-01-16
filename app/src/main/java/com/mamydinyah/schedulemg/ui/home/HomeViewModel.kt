package com.mamydinyah.schedulemg.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mamydinyah.schedulemg.data.Connection
import com.mamydinyah.schedulemg.data.TaskRepository

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TaskRepository
    val totalTaskCount: LiveData<Int>
    val todayTaskCount: LiveData<Int>

    init {
        val taskDao = Connection.getDatabase(application).taskDao()
        repository = TaskRepository(taskDao, application)
        totalTaskCount = repository.totalTaskCount
        todayTaskCount = repository.getTodayTaskCount()
    }

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text
}

