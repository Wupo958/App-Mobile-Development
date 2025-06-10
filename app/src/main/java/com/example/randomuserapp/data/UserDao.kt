package com.example.randomuserapp.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.randomuserapp.user.User

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)

    @Query("SELECT * FROM user_table ORDER BY lastName ASC")
    fun getAll(): LiveData<List<User>>

    @Query("DELETE FROM user_table")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM user_table")
    suspend fun countUsers(): Int

    @Query("SELECT * FROM user_table WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: Int): User
}


