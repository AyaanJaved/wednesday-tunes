package com.ayaanjaved.wednesdaytunes.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ayaanjaved.wednesdaytunes.R
import com.ayaanjaved.wednesdaytunes.models.ITunesItem
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.itunes_item.view.*

class ITunesAdapter : RecyclerView.Adapter<ITunesAdapter.ITunesViewHoler>() {
    inner class ITunesViewHoler(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    private val differCallback = object : DiffUtil.ItemCallback<ITunesItem>() {
        override fun areItemsTheSame(oldItem: ITunesItem, newItem: ITunesItem): Boolean {
            return oldItem.trackId == newItem.trackId
        }

        override fun areContentsTheSame(oldItem: ITunesItem, newItem: ITunesItem): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ITunesViewHoler {
        return ITunesViewHoler(
            LayoutInflater.from(parent.context).inflate(R.layout.itunes_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ITunesViewHoler, position: Int) {
        val iTunesItem = differ.currentList[position]
        holder.itemView.apply {
            trackName.text = iTunesItem.trackName
            artistName.text = iTunesItem.artistName
            Glide.with(this).load(iTunesItem.artworkUrl100).into(trackImage)
            setOnClickListener {
                onItemClickListener?.let{it(iTunesItem, this)}
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onItemClickListener: ((ITunesItem, View)-> Unit)? = null

    fun setOnItemClickListener(listener: (ITunesItem, View) -> Unit) {
        onItemClickListener = listener
    }

}