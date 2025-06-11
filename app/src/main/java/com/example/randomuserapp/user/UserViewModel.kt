package com.example.randomuserapp.user

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.randomuserapp.data.UserRepository
import kotlinx.coroutines.launch
import androidx.lifecycle.LiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.ExperimentalCoroutinesApi

class UserViewModel(private val repository: UserRepository) : ViewModel() {
    private val _sortOption = MutableStateFlow(SortOption.FIRST_NAME)
    val sortOption: StateFlow<SortOption> = _sortOption

    fun setSortOption(option: SortOption) {
        _sortOption.value = option
    }

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

    @OptIn(ExperimentalCoroutinesApi::class)
    val users: LiveData<List<User>> = _sortOption
        .flatMapLatest { option ->
            repository.users
                .asFlow()
                .map { list ->
                    when (option) {
                        SortOption.FIRST_NAME -> list.sortedBy { it.firstName }
                        SortOption.LAST_NAME -> list.sortedBy { it.lastName }
                        SortOption.DOB -> list.sortedBy { it.dob }
                    }
                }
        }
        .asLiveData()

    fun loadSortOption(context: Context) {
        viewModelScope.launch {
            SortPreferences.getSortOption(context).collect {
                _sortOption.value = it
            }
        }
    }

    fun setSortOption(option: SortOption, context: Context) {
        _sortOption.value = option
        viewModelScope.launch {
            SortPreferences.setSortOption(context, option)
        }
    }
}

class UserViewModelFactory(private val repository: UserRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return UserViewModel(repository) as T
    }
}