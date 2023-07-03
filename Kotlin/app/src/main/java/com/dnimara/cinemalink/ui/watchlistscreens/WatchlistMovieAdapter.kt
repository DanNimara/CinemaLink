package com.dnimara.cinemalink.ui.watchlistscreens

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dnimara.cinemalink.databinding.ListItemWatchlistMovieBinding
import com.dnimara.cinemalink.ui.moviescreen.MovieSummaryDto

class WatchlistMovieAdapter(val viewModel: ShowWatchlistViewModel,
                            val clickListener: WatchlistMovieListener):
    ListAdapter<MovieSummaryDto, WatchlistMovieAdapter.ViewHolder>(DiffCallback) {

    class ViewHolder private constructor(val binding: ListItemWatchlistMovieBinding):
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MovieSummaryDto, clickListener: WatchlistMovieListener, viewModel: ShowWatchlistViewModel) {
            if (item.posterUrl == null) {
                item.posterUrl = ""
            }
            if (item.note == null) {
                binding.tvWatchlistNote.visibility = View.GONE
            } else {
                binding.tvWatchlistNote.visibility = View.VISIBLE
            }
            if (item.duration != null) {
                binding.tvWatchlistRuntime.text = (item.duration!! / 60).toString() + " minutes"
                binding.tvWatchlistRuntime.visibility = View.VISIBLE
            } else {
                binding.tvWatchlistRuntime.visibility = View.GONE
            }
            binding.ivWatchlistPoster.setOnClickListener {
                clickListener.onClick(item.id)
            }
            binding.tvWatchlistTitle.setOnClickListener {
                clickListener.onClick(item.id)
            }
            binding.tvWatchlistRuntime.setOnClickListener {
                clickListener.onClick(item.id)
            }
            binding.tvWatchlistNote.setOnClickListener {
                clickListener.onClick(item.id)
            }
            binding.ibDeleteWatchlistMovie.setOnClickListener {
                viewModel.deleteMovie(item.id)
            }
            binding.watchlistmovie = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemWatchlistMovieBinding.inflate(layoutInflater,
                    parent, false)
                return ViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, clickListener, viewModel)
    }

    class WatchlistMovieListener(val clickListener: (movieId: String) -> Unit) {
        fun onClick(movieId: String) = clickListener(movieId)
    }

    companion object DiffCallback: DiffUtil.ItemCallback<MovieSummaryDto>() {
        override fun areItemsTheSame(oldItem: MovieSummaryDto, newItem: MovieSummaryDto): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MovieSummaryDto, newItem: MovieSummaryDto): Boolean {
            return oldItem == newItem
        }
    }

}