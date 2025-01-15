package com.mamydinyah.schedulemg.notification

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class TaskReminderWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        val title = inputData.getString("title") ?: "Rappel"
        val content = inputData.getString("content") ?: "Vous avez une tâche à effectuer."
        val taskId = inputData.getInt("taskId", 0)

        NotificationHelper.sendNotification(title, content, taskId, applicationContext)

        return Result.success()
    }
}
