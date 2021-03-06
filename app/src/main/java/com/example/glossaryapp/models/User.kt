package com.example.glossaryapp.models

import java.util.*


data class User(
    val _id: String? = null,
    val email: String? = null,
    val firstName: String? = null,
    val mobile: String? = null,
    val password: String? = null
)

data class LoginResponse(
    val token: String,
    val user: User
)