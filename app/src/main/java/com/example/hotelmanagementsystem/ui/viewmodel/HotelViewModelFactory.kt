package com.example.hotelmanagementsystem.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.hotelmanagementsystem.networkmanager.NetworkManager
import com.example.hotelmanagementsystem.repository.Repository

class HotelViewModelFactory(
    private val repository: Repository,
    private val networkManager: NetworkManager,
    val app:Application
    ):ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HotelViewModel(repository,networkManager, app) as T
    }
}