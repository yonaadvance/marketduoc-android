package com.example.marketduoc.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.marketduoc.data.local.dao.ArticuloDao


class ArticuloViewModelFactory(private val dao: ArticuloDao) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(ArticuloViewModel::class.java)) {

            @Suppress("UNCHECKED_CAST")
            return ArticuloViewModel(dao) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}