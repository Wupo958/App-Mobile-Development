package com.example.randomuserapp.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//Erstellt Retrofit Instanz zum kommunizieren mit API
object ApiClient {
    val api: UserApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://randomuser.me/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UserApi::class.java)
    }
}

