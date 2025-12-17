package com.example.marketduoc.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ArticuloViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ArticuloViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ArticuloViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}