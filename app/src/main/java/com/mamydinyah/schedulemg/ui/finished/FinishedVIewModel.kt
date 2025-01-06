package com.mamydinyah.schedulemg.ui.finished

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FinishedVIewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is finished Fragment"
    }
    val text: LiveData<String> = _text
}