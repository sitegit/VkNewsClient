package com.example.vknewsclient.data.model.fave

import com.google.gson.annotations.SerializedName

data class FavePostDto(
    @SerializedName("post") val post: FavePostItemDto
)