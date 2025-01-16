package com.mamydinyah.schedulemg.ui.inprogress

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class InProgressVIewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InprogressViewModel::class.java)) {
            return InprogressViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}