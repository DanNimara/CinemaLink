package com.dnimara.cinemalink.ui.searchuserscreen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dnimara.cinemalink.databinding.ListItemUserSearchBinding
class SearchUserAdapter(val clickListener: SearchUserListener):
    ListAdapter<UserDto, SearchUserAdapter.ViewHolder>(DiffCallback) {

    class ViewHolder private constructor(val binding: ListItemUserSearchBinding):
            RecyclerView.ViewHolder(binding.root) {

        fun bind(item: UserDto) {
            binding.user = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemUserSearchBinding.inflate(layoutInflater,
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
            clickListener.onClick(item.userId)
        }
        holder.bind(item)
    }

    class SearchUserListener(val clickListener: (userId: Long) -> Unit) {
        fun onClick(userId: Long) = clickListener(userId)
    }

    companion object DiffCallback: DiffUtil.ItemCallback<UserDto>() {
        override fun areItemsTheSame(oldItem: UserDto, newItem: UserDto): Boolean {
            return oldItem.userId == newItem.userId
        }

        override fun areContentsTheSame(oldItem: UserDto, newItem: UserDto): Boolean {
            return oldItem == newItem
        }
    }

}