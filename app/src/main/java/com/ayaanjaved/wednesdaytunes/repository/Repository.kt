package com.ayaanjaved.wednesdaytunes.repository

import com.ayaanjaved.wednesdaytunes.database.Database
import com.ayaanjaved.wednesdaytunes.models.ITunesItem
import com.ayaanjaved.wednesdaytunes.models.Result
import com.ayaanjaved.wednesdaytunes.networking.RetrofitInstance

class Repository (val db: Database){
    suspend fun getArtists(searchTerm: String) = RetrofitInstance.api.getArtists(searchTerm)

    suspend fun getOfflineArtists(searchTerm: String) : Result {
        val offlineList: List<ITunesItem> =  db.getDao().getList("%$searchTerm%")
        return Result(offlineList.size, offlineList)
    }

    suspend fun getAllArtists(): Result {
        val allList = db.getDao().getAllList()
        return Result(allList.size, allList)
    }

}