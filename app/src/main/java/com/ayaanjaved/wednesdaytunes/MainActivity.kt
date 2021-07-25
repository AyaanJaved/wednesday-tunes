package com.ayaanjaved.wednesdaytunes

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayaanjaved.wednesdaytunes.adapter.ITunesAdapter
import com.ayaanjaved.wednesdaytunes.database.Database
import com.ayaanjaved.wednesdaytunes.models.ITunesItem
import com.ayaanjaved.wednesdaytunes.models.Resource
import com.ayaanjaved.wednesdaytunes.repository.Repository
import com.ayaanjaved.wednesdaytunes.viewmodel.ITunesViewModel
import com.ayaanjaved.wednesdaytunes.viewmodel.ITunesViewModelProviderFactory
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.itunes_item.view.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), androidx.appcompat.widget.SearchView.OnQueryTextListener{
    private val TAG: String = "mainActivity"
    lateinit var viewModel: ITunesViewModel
    lateinit var iTunesAdapter: ITunesAdapter
    lateinit var mediaPlayer: MediaPlayer
    private var lastButton: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val repository = Repository(Database(this))
        val viewModelProviderFactory = ITunesViewModelProviderFactory(repository, application)
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(ITunesViewModel::class.java)

        iTunesAdapter = ITunesAdapter()
        itunes_recycler_view.apply {
            adapter = iTunesAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }

        mediaPlayer = MediaPlayer()
        mediaPlayer.apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )}

        iTunesAdapter.setOnItemClickListener { iTunesItem: ITunesItem, view: View ->
            lastButton?.imageButton?.setImageResource(R.drawable.circled_play_50)
            lastButton = view
            view.imageButton.setImageResource(R.drawable.pause_button_50)
            mediaPlayer.apply {
                reset()
                setDataSource(iTunesItem.previewUrl)
                prepare() // might take long! (for buffering, etc)
                start()
            }
        }

        viewModel.iTunesResponse.observe(this, Observer { response ->
            when(response) {
                is Resource.Success -> {
                    Log.i(TAG, response.data?.resultCount.toString())
                    progress_bar.visibility = View.GONE
                    response.data?.let { iTunesResponse ->
                        iTunesAdapter.differ.submitList(iTunesResponse.results)
                    }
                }
                is Resource.Error -> {
                    response.message?.let{ message ->
                        Log.e(TAG, "onCreate: error", )
                        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)

        val search = menu?.findItem(R.id.search_view_button)
        val searchView = search?.actionView as? androidx.appcompat.widget.SearchView
        searchView?.isSubmitButtonEnabled = true
        searchView?.setOnQueryTextListener(this)
        return super.onCreateOptionsMenu(menu)
    }
//
    override fun onQueryTextSubmit(query: String?): Boolean {
        Log.i(TAG, "onQueryTextSubmit: ")
        var job: Job ?= null
        if(query != null && query.isNotEmpty()) {
            Log.i(TAG, "onQueryTextSubmit: inside if block")
            job?.cancel()
            job = MainScope().launch {
                    viewModel.getArtists(query)
                }
            }
        return true;
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        Log.i(TAG, "onQueryTextChange: ")
//        var job: Job ?= null
//        if(newText != null && newText.isNotEmpty()) {
//            job?.cancel()
//            job = MainScope().launch {
//                delay(500L)
//                viewModel.getArtists(newText)
//            }
//        }
        return false;
    }
}