package com.example.vknewsclient.data.model.fave

import com.google.gson.annotations.SerializedName

data class FaveResponseDto(
    @SerializedName("response") val faveContentDto: FaveContentDto
)