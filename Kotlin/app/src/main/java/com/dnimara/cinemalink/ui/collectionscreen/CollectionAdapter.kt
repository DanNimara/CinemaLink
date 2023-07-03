package com.dnimara.cinemalink.ui.collectionscreen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dnimara.cinemalink.databinding.ListItemCollectionBinding
import com.dnimara.cinemalink.ui.moviescreen.OwnedStatus

class CollectionAdapter(
    private val viewModel: CollectionViewModel,
    private val clickListener: CollectionListener):
    ListAdapter<CollectionMovieDto, CollectionAdapter.ViewHolder>(DiffCallback) {
    private var list = mutableListOf<CollectionMovieDto>()

    var movieListFiltered: List<CollectionMovieDto> = ArrayList()

    class ViewHolder private constructor(val binding: ListItemCollectionBinding):
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CollectionMovieDto) {
            if (item.posterUrl == null) {
                item.posterUrl = ""
            }
            if (item.platforms.contains(OwnedStatus.VHS)) {
                binding.vhs.visibility = View.VISIBLE
            } else {
                binding.vhs.visibility = View.GONE
            }
            if (item.platforms.contains(OwnedStatus.DVD)) {
                binding.dvd.visibility = View.VISIBLE
            } else {
                binding.dvd.visibility = View.GONE
            }
            if (item.platforms.contains(OwnedStatus.BlURAY)) {
                binding.bluRay.visibility = View.VISIBLE
            } else {
                binding.bluRay.visibility = View.GONE
            }
            if (item.platforms.contains(OwnedStatus.THREED)) {
                binding.bluRay3d.visibility = View.VISIBLE
            } else {
                binding.bluRay3d.visibility = View.GONE
            }
            if (item.platforms.contains(OwnedStatus.FOURK)) {
                binding.fourK.visibility = View.VISIBLE
            } else {
                binding.fourK.visibility = View.GONE
            }
            if (item.outline == null) {
                binding.tvCollectionOutline.visibility = View.GONE
            } else {
                binding.tvCollectionOutline.visibility = View.VISIBLE
            }
            binding.movie = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemCollectionBinding.inflate(layoutInflater,
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
            clickListener.onClick(item.id)
        }
        holder.bind(item)
    }

    fun setData(list: MutableList<CollectionMovieDto>){
        this.list = list
        submitList(list)
        viewModel.filterScroll()
    }

    class CollectionListener(val clickListener: (movieId: String) -> Unit) {
        fun onClick(movieId: String) = clickListener(movieId)
    }

    companion object DiffCallback: DiffUtil.ItemCallback<CollectionMovieDto>() {
        override fun areItemsTheSame(oldItem: CollectionMovieDto, newItem: CollectionMovieDto): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CollectionMovieDto, newItem: CollectionMovieDto): Boolean {
            return oldItem == newItem
        }
    }

    fun getFilter(): Filter {
        return customFilter
    }

    private val customFilter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filteredList = mutableListOf<CollectionMovieDto>()
            if (constraint == null || constraint.isEmpty() || constraint == "All") {
                filteredList.addAll(list)
            } else {
                for (item in list) {
                    if (item.genres?.contains(constraint.toString())!!) {
                        filteredList.add(item)
                    }
                }
            }
            val results = FilterResults()
            results.values = filteredList
            return results
        }

        override fun publishResults(constraint: CharSequence?, filterResults: FilterResults?) {
            submitList(filterResults?.values as MutableList<CollectionMovieDto>)
            viewModel.filterScroll()
        }

    }

}