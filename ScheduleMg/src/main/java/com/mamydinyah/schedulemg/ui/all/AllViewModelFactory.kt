package com.mamydinyah.schedulemg.ui.all

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AllViewModelFactory(val application: Application) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AllViewModel::class.java)) {
            return AllViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}