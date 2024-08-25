package com.practicum.playlistmaker.search.data.network

import com.practicum.playlistmaker.search.data.dto.SearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesService {
    @GET("/search?entity=song")
    suspend fun search(@Query("term") text: String): Response<SearchResponse>
}