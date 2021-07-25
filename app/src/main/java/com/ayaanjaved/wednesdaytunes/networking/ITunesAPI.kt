package com.ayaanjaved.wednesdaytunes.networking

import com.ayaanjaved.wednesdaytunes.models.Result
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesAPI {
    @GET("search")
    suspend fun getArtists(
        @Query("term")
        term: String
    ): Response<Result>
}