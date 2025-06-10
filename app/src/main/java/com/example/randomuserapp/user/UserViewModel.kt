package com.example.randomuserapp.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.randomuserapp.data.UserRepository
import kotlinx.coroutines.launch
import androidx.lifecycle.LiveData

class UserViewModel(private val repository: UserRepository) : ViewModel() {
    val users: LiveData<List<User>> = repository.users

    fun refreshUsers() {
        viewModelScope.launch {
            repository.refreshUsers()
        }
    }

    fun addUsers(){
        viewModelScope.launch {
            repository.addUsers()
        }
    }

    fun clearUsers() {
        viewModelScope.launch {
            repository.clearUsers()
        }
    }
}

class UserViewModelFactory(private val repository: UserRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return UserViewModel(repository) as T
    }
}