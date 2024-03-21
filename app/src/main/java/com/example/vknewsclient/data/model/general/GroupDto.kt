package com.example.vknewsclient.data.model.general

import com.google.gson.annotations.SerializedName
data class GroupDto(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("photo_200") val imageUrl: String
)
