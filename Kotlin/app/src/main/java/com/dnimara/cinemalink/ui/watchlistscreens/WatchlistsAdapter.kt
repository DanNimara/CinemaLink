package com.dnimara.cinemalink.ui.watchlistscreens

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dnimara.cinemalink.R
import com.dnimara.cinemalink.application.CinemaLinkApplication
import com.dnimara.cinemalink.databinding.ListItemWatchlistBinding
import java.text.SimpleDateFormat
import java.util.*

class WatchlistsAdapter(val clickListener: WatchlistsListener):
    ListAdapter<WatchlistDto, WatchlistsAdapter.ViewHolder>(DiffCallback) {

    class ViewHolder private constructor(val binding: ListItemWatchlistBinding):
            RecyclerView.ViewHolder(binding.root) {

        val sdf = SimpleDateFormat("MMM dd yyyy", Locale.ENGLISH)

        fun bind(item: WatchlistDto) {
            binding.watchlist = item
            binding.tvWatchlistCreatedItem.text =
                CinemaLinkApplication.mInstance.applicationContext.resources.getString(
                    R.string.created_watchlist, sdf.format(item.createdTime))
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemWatchlistBinding.inflate(
                    layoutInflater,
                    parent, false
                )
                return ViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.itemView.setOnClickListener {
            clickListener.onClick(item.id)
        }
        holder.bind(item)
    }

    class WatchlistsListener(val clickListener: (watchlistId: Long) -> Unit) {
        fun onClick(watchlistId: Long) = clickListener(watchlistId)
    }

    companion object DiffCallback: DiffUtil.ItemCallback<WatchlistDto>() {
        override fun areItemsTheSame(oldItem: WatchlistDto, newItem: WatchlistDto): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: WatchlistDto, newItem: WatchlistDto): Boolean {
            return oldItem == newItem
        }
    }

}