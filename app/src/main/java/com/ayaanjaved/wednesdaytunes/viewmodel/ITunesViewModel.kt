package com.ayaanjaved.wednesdaytunes.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.*
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.ayaanjaved.wednesdaytunes.WednesdayApplication
import com.ayaanjaved.wednesdaytunes.database.Database
import com.ayaanjaved.wednesdaytunes.database.EntityDao
import com.ayaanjaved.wednesdaytunes.models.Resource
import com.ayaanjaved.wednesdaytunes.models.Result
import com.ayaanjaved.wednesdaytunes.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class ITunesViewModel(val repository: Repository, application: Application) : AndroidViewModel(
    application
) {
    val iTunesResponse: MutableLiveData<Resource<Result>> = MutableLiveData()
    lateinit var entityDao: EntityDao

    init {
        val db = Room.databaseBuilder(
            getApplication(),
            Database::class.java, "database"
        ).build()

        entityDao = db.getDao()

        getArtists("arijit+singh")

    }

    fun getArtists(searchTerm: String) = viewModelScope.launch {
        safeCall(searchTerm)
    }

    private suspend fun safeCall(searchTerm: String) {
        iTunesResponse.postValue(Resource.Loading())
        try{
            if(hasInternetConnection()) {
                val response = repository.getArtists(searchTerm)
                iTunesResponse.postValue(handleResponse(response))
            } else {
//                iTunesResponse.postValue(Resource.Error("no internet connnection"))
                val list = entityDao.getList(searchTerm)
                iTunesResponse.postValue( Resource.Success(Result(list.size, list)))
            }
        } catch (t: Throwable) {
//            when(t) {
//                is IOException -> iTunesResponse.postValue(Resource.Error("network failure"))
//                else -> iTunesResponse.postValue(Resource.Error("other error"))
//            }
            viewModelScope.launch(Dispatchers.IO) {
                val result = Result(entityDao.getAllList().size, entityDao.getList(searchTerm))
                Log.i("viewmodel", result.resultCount.toString())
                iTunesResponse.postValue(Resource.Success(result))
            }

        }

    }

    private suspend fun handleResponse(response: Response<Result>) : Resource<Result> {
        if(response.isSuccessful) {
            response.body()?.let{ resultResponse ->
                resultResponse.results.forEach { entityDao.insert(it)}
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<WednesdayApplication>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when {
            capabilities.hasTransport(TRANSPORT_WIFI) -> true
            capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }
}