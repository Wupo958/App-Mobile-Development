package com.example.randomuserapp.data

import retrofit2.Response
import retrofit2.http.GET

data class RandomUserResponse(val results: List<UserDto>)

data class UserDto(
    val name: Name,
    val dob: Dob,
    val phone: String,
    val picture: Picture
)

data class Name(val first: String, val last: String)
data class Dob(val date: String)
data class Picture(val large: String)

interface UserApi {
    @GET("api/?results=10")
    suspend fun getUsers(): Response<RandomUserResponse>
}

