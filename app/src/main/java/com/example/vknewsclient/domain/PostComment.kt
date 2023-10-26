package com.example.vknewsclient.domain

import com.example.vknewsclient.R

data class PostComment(
    val id: Int,
    val authorName: String = "Alex",
    val avatarId: Int = R.drawable.post_comunity_thumbnail,
    val commentText: String = "Long comment text",
    val publicationDate: String = "14:00"
)
