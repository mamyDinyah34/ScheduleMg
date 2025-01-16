/*
package com.mamydinyah.schedulemg.crud

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.mamydinyah.schedulemg.data.TaskRepository
import com.mamydinyah.schedulemg.data.Connection

class UpdateTaskStatusWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {
        return try {
            val database = Connection.getDatabase(applicationContext)
            val taskDao = database.taskDao()
            val repository = TaskRepository(taskDao)
            repository.updateTaskStatus()
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }
}*/
