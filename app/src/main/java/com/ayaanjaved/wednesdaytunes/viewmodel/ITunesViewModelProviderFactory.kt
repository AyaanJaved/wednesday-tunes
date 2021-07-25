package com.ayaanjaved.wednesdaytunes.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ayaanjaved.wednesdaytunes.repository.Repository

class ITunesViewModelProviderFactory(val repository: Repository, val application: Application) : ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ITunesViewModel(repository, application) as T
    }
}