package com.dnimara.cinemalink.ui.feedscreen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dnimara.cinemalink.databinding.ListItemMovieTagSearchBinding

class TagMovieAdapter(val clickListener: TagMovieListener):
    ListAdapter<MovieTagDto, TagMovieAdapter.ViewHolder>(DiffCallback) {

    class ViewHolder private constructor(val binding: ListItemMovieTagSearchBinding):
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MovieTagDto) {
            if (item.posterUrl == null) {
                item.posterUrl = ""
            }
            binding.movieTag = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemMovieTagSearchBinding.inflate(layoutInflater,
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
        holder.itemView.setOnClickListener {
            clickListener.onClick(item)
        }
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    class TagMovieListener(val clickListener: (movie: MovieTagDto) -> Unit) {
        fun onClick(movie: MovieTagDto) = clickListener(movie)
    }

    companion object DiffCallback: DiffUtil.ItemCallback<MovieTagDto>() {
        override fun areItemsTheSame(oldItem: MovieTagDto, newItem: MovieTagDto): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MovieTagDto, newItem: MovieTagDto): Boolean {
            return oldItem == newItem
        }
    }

}