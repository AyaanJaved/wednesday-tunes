package com.ayaanjaved.wednesdaytunes.models

data class Result(
    val resultCount: Int,
    val results: List<ITunesItem>
)