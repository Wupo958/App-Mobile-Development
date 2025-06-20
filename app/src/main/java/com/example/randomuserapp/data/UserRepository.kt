package com.example.randomuserapp.data

import androidx.lifecycle.LiveData
import com.example.randomuserapp.user.User
import com.example.randomuserapp.user.formatDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Schnittstelle zwischen AppDatabase und UserAPI
class UserRepository(private val db: AppDatabase) {
    val users: LiveData<List<User>> = db.userDao().getAll()

    //Leert die Datenbank und fügt dann neue User hinzu
    suspend fun refreshUsers() = withContext(Dispatchers.IO) {
        db.userDao().deleteAll()
        addUsers()
    }

    //Neue User zur Datenbank hinzufügen
    suspend fun addUsers() = withContext(Dispatchers.IO){
        val response = ApiClient.api.getUsers()
        if (response.isSuccessful) {
            response.body()?.results?.forEach {
                val user = User(
                    firstName = it.name.first,
                    lastName = it.name.last,
                    dob = it.dob.date,
                    phone = it.phone,
                    photoUrl = it.picture.large
                )
                db.userDao().insert(user)
            }
        }
    }

    suspend fun getUserById(id: Int): User {
        return db.userDao().getUserById(id)
    }

    suspend fun clearUsers() = withContext(Dispatchers.IO) {
        db.userDao().deleteAll()
    }

    //gibt true zurück wenn die Datenbank mehr als 0 user hat
    suspend fun hasUsers(): Boolean {
        return db.userDao().countUsers() > 0
    }

    //neuen user einfügen
    suspend fun insert(user: User) = withContext(Dispatchers.IO) {
        db.userDao().insert(user)
    }

    //neuen user einfügen außer er existiert bereits
    suspend fun insertIfNotExists(user: User): User? = withContext(Dispatchers.IO) {
        val existing = db.userDao().getByIdentity(user.firstName, user.lastName, user.dob)
        if (existing == null) {
            db.userDao().insert(user)
            db.userDao().getByIdentity(user.firstName, user.lastName, user.dob)
        } else {
            existing
        }
    }

    suspend fun updateUser(user: User) {
        db.userDao().update(user)
    }
}

