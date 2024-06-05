package com.practicum.playlistmaker.data.dto

import com.google.gson.annotations.SerializedName
import com.practicum.playlistmaker.domain.models.Track

data class SearchResponse(
    @SerializedName("resultCount") val resultCount: Int,
    @SerializedName("results") val results: MutableList<Track>
)
