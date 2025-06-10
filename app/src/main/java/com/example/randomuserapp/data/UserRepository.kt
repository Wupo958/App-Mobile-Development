package com.example.randomuserapp.data

import androidx.lifecycle.LiveData
import androidx.room.Query
import com.example.randomuserapp.user.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(private val db: AppDatabase) {
    val users: LiveData<List<User>> = db.userDao().getAll()

    suspend fun refreshUsers() = withContext(Dispatchers.IO) {
        db.userDao().deleteAll()
        addUsers()
    }

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

    suspend fun hasUsers(): Boolean {
        return db.userDao().countUsers() > 0
    }

    suspend fun insert(user: User) = withContext(Dispatchers.IO) {
        db.userDao().insert(user)
    }

    suspend fun insertIfNotExists(user: User): User? = withContext(Dispatchers.IO) {
        val existing = db.userDao().getByIdentity(user.firstName, user.lastName, user.dob)
        if (existing == null) {
            db.userDao().insert(user)
            // Finde ihn direkt danach (wegen autoGenerate id)
            db.userDao().getByIdentity(user.firstName, user.lastName, user.dob)
        } else {
            existing
        }
    }
}

