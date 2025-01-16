package com.mamydinyah.schedulemg.ui.finished

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mamydinyah.schedulemg.ui.inprogress.InprogressViewModel

class FinishedVIewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FinishedVIewModel::class.java)) {
            return FinishedVIewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}