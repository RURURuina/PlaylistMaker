package com.practicum.playlistmaker.search.data.dto

import com.google.gson.annotations.SerializedName
import com.practicum.playlistmaker.search.domain.models.Track

data class SearchResponse(
    @SerializedName("resultCount") val resultCount: Int,
    @SerializedName("results") val results: MutableList<Track?>
)
