package com.dnimara.cinemalink.ui.searchmoviescreen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dnimara.cinemalink.databinding.ListItemSearchBinding
import com.dnimara.cinemalink.ui.moviescreen.MovieSummaryDto

class SearchMovieAdapter(private val clickListener: SearchMovieListener):
    ListAdapter<MovieSummaryDto, SearchMovieAdapter.ViewHolder>(DiffCallback) {

    class ViewHolder private constructor(val binding: ListItemSearchBinding):
            RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MovieSummaryDto) {
            if (item.posterUrl == null) {
                item.posterUrl = ""
            }
            if (item.outline == null) {
                binding.tvSearchOutline.visibility = View.GONE
            } else {
                binding.tvSearchOutline.visibility = View.VISIBLE
            }
            binding.movie = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemSearchBinding.inflate(layoutInflater,
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

    class SearchMovieListener(val clickListener: (movie: MovieSummaryDto) -> Unit) {
        fun onClick(movie: MovieSummaryDto) = clickListener(movie)
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