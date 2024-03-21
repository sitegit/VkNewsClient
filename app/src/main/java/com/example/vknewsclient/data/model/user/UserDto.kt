package com.example.vknewsclient.data.model.user

import com.google.gson.annotations.SerializedName

data class UserDto(
    @SerializedName("id") val id: Long,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("photo_200") val photo: String,
    @SerializedName("city") val city: CityDto,
    @SerializedName("mobile_phone") val phone: String,
    @SerializedName("counters") val counters: CountersDto
)
