package com.dnimara.cinemalink.ui.moviescreen

import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dnimara.cinemalink.databinding.ListItemWatchlistTitleBinding
import com.dnimara.cinemalink.ui.watchlistscreens.WatchlistItemDto

class WatchlistChoiceAdapter(val viewModel: MovieViewModel):
    ListAdapter<WatchlistItemDto, WatchlistChoiceAdapter.ViewHolder>(DiffCallback) {

    private var selectedPos = RecyclerView.NO_POSITION

    class ViewHolder private constructor(val binding: ListItemWatchlistTitleBinding):
            RecyclerView.ViewHolder(binding.root) {

        fun bind(item: WatchlistItemDto) {
            binding.watchlist = item
            if (item.isSelected) {
                binding.tvWatchlistItem.setTypeface(null, Typeface.BOLD)
                binding.tvWatchlistItem.setBackgroundColor(Color.parseColor("#ffc053"))
            } else {
                binding.tvWatchlistItem.setTypeface(null, Typeface.NORMAL)
                binding.tvWatchlistItem.background = null
            }
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemWatchlistTitleBinding.inflate(layoutInflater,
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
            if (selectedPos != holder.absoluteAdapterPosition) {
                selectedPos = holder.absoluteAdapterPosition
            } else {
                selectedPos = RecyclerView.NO_POSITION
            }
            notifyDataSetChanged()
        }
        item.isSelected = selectedPos == position
        if (item.isSelected && selectedPos != RecyclerView.NO_POSITION) {
            viewModel.selectWatchlist(item.id)
        } else if (selectedPos == RecyclerView.NO_POSITION) {
            viewModel.unselectWatchlist()
        }
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    class WatchlistChoiceListener(val clickListener: (position: Int) -> Unit) {
        fun onClick(position: Int) = clickListener(position)
    }

    companion object DiffCallback: DiffUtil.ItemCallback<WatchlistItemDto>() {
        override fun areItemsTheSame(oldItem: WatchlistItemDto, newItem: WatchlistItemDto): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: WatchlistItemDto, newItem: WatchlistItemDto): Boolean {
            return oldItem == newItem
        }
    }

}