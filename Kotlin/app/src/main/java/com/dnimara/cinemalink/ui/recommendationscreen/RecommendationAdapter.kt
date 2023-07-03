package com.dnimara.cinemalink.ui.recommendationscreen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dnimara.cinemalink.databinding.ListItemRecommendationBinding

class RecommendationAdapter(val clickListener: RecommendationListener):
    ListAdapter<RecommendationDto, RecommendationAdapter.ViewHolder>(DiffCallback) {

    class ViewHolder private constructor(val binding: ListItemRecommendationBinding):
            RecyclerView.ViewHolder(binding.root) {

        fun bind(item: RecommendationDto) {
            if (item.posterUrl == null) {
                item.posterUrl = ""
            }
            binding.recommendation = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemRecommendationBinding.inflate(layoutInflater,
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
            clickListener.onClick(item.movieId)
        }
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    class RecommendationListener(val clickListener: (movieId: String) -> Unit) {
        fun onClick(movieId: String) = clickListener(movieId)
    }

    companion object DiffCallback: DiffUtil.ItemCallback<RecommendationDto>() {
        override fun areItemsTheSame(oldItem: RecommendationDto, newItem: RecommendationDto): Boolean {
            return oldItem.movieId == newItem.movieId
        }

        override fun areContentsTheSame(oldItem: RecommendationDto, newItem: RecommendationDto): Boolean {
            return oldItem == newItem
        }
    }

}