package com.example.randomuserapp.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.randomuserapp.user.User

//Zugriffs Objekt für Datenbank
@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)

    //Alle User holen
    @Query("SELECT * FROM user_table ORDER BY firstName ASC")
    fun getAll(): LiveData<List<User>>

    //Alles aus der Datenbank löschen
    @Query("DELETE FROM user_table")
    suspend fun deleteAll()

    //Menge an Usern Zählen
    @Query("SELECT COUNT(*) FROM user_table")
    suspend fun countUsers(): Int

    //Bestimmten User auswählen
    @Query("SELECT * FROM user_table WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: Int): User

    //Alles was fürs sortieren gebraucht wird holen
    @Query("SELECT * FROM user_table WHERE firstName = :firstName AND lastName = :lastName AND dob = :dob LIMIT 1")
    suspend fun getByIdentity(firstName: String, lastName: String, dob: String): User?

    //neu laden
    @Update
    suspend fun update(user: User)
}


