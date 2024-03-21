package com.example.vknewsclient.data.model.fave

import com.example.vknewsclient.data.model.general.GroupDto
import com.google.gson.annotations.SerializedName

data class FaveContentDto(
    @SerializedName("items") val posts: List<FavePostDto>,
    @SerializedName("groups") val groups: List<GroupDto>
)