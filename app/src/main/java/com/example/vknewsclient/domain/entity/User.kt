package com.example.vknewsclient.domain.entity

data class User(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val avatar: String,
    val city: String,
    val phone: String,
    val friends: Int,
    val followers: Int,
    val groups: Int,
    val photos: Int,
    val videos: Int,
    val gifts: Int
)
