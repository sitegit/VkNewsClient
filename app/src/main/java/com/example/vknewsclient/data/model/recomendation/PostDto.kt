package com.example.vknewsclient.data.model.recomendation

import com.example.vknewsclient.data.model.general.AttachmentDto
import com.example.vknewsclient.data.model.general.CommentsDto
import com.example.vknewsclient.data.model.general.LikesDto
import com.example.vknewsclient.data.model.general.RepostsDto
import com.example.vknewsclient.data.model.general.ViewsDto
import com.google.gson.annotations.SerializedName

data class PostDto(
    @SerializedName("id") val id: Long,
    @SerializedName("source_id") val communityId: Long,
    @SerializedName("text") val text: String,
    @SerializedName("date") val date: Long,
    @SerializedName("likes") val likes: LikesDto,
    @SerializedName("comments") val comments: CommentsDto,
    @SerializedName("views") val views: ViewsDto,
    @SerializedName("reposts") val reposts: RepostsDto,
    @SerializedName("attachments") val attachments: List<AttachmentDto>?
)
