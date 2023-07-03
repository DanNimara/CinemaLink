package com.dnimara.cinemalink.ui.profilescreen

import android.text.*
import android.text.format.DateUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.text.bold
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dnimara.cinemalink.R
import com.dnimara.cinemalink.database.PostDto
import com.dnimara.cinemalink.databinding.ListItemPostProfileBinding
import com.dnimara.cinemalink.domain.ShareDto
import java.util.*

class ProfileAdapter(val viewModel: ProfileViewModel) : ListAdapter<PostDto, ProfileAdapter.ViewHolder>(DiffCallback) {

    class ViewHolder private constructor(val binding: ListItemPostProfileBinding):
            RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PostDto, viewModel: ProfileViewModel) {
            binding.post = item
            binding.tvFeedCreated.text = DateUtils.getRelativeTimeSpanString(item.postingTime,
                Calendar.getInstance().timeInMillis, DateUtils.MINUTE_IN_MILLIS)
            binding.likeFeedPostButton.setOnClickListener {
                viewModel.react(item.postId, true)
            }
            binding.dislikeFeedPostButton.setOnClickListener {
                viewModel.react(item.postId, false)
            }
            binding.commentFeedButton.setOnClickListener {
                viewModel.navigateToPost(item.postId)
            }
            binding.shareFeedButton.setOnClickListener {
                var tags = ""
                if (!item.tags.isNullOrEmpty()) {
                    tags = item.tags.map { mov -> mov.title }.joinToString(separator = ", ")
                }
                viewModel.share(ShareDto(item.username, item.content, tags))
            }
            if (item.liked == null) {
                binding.likeFeedPostButton.setImageResource(R.drawable.ic_thumb_up_24)
                binding.dislikeFeedPostButton.setImageResource(R.drawable.ic_thumb_down_24)
            } else if (item.liked == true) {
                binding.likeFeedPostButton.setImageResource(R.drawable.ic_thumb_up_24_blue)
                binding.dislikeFeedPostButton.setImageResource(R.drawable.ic_thumb_down_24)
            } else if (item.liked == false) {
                binding.likeFeedPostButton.setImageResource(R.drawable.ic_thumb_up_24)
                binding.dislikeFeedPostButton.setImageResource(R.drawable.ic_thumb_down_24_red)
            }
            if (item.tags != null && item.tags.isNotEmpty()) {
                if (binding.tvTagsPostItem.visibility == View.GONE)
                    binding.tvTagsPostItem.visibility = View.VISIBLE
                binding.tvTagsPostItem.text = SpannableStringBuilder()
                    .bold{append("Referencing: ")}
                    .append(item.tags.map {
                            tag -> tag.title
                    }.joinToString(separator = ", "))
                var startIndexOfLink = 13
                var spannableString = SpannableString(binding.tvTagsPostItem.text.toString())
                for (tag in item.tags) {
                    val clickableSpan = object: ClickableSpan() {
                        override fun updateDrawState(ds: TextPaint) {
                            ds.color = ds.linkColor
                            ds.isFakeBoldText = true
                        }

                        override fun onClick(v: View) {
                            Selection.setSelection((v as TextView).text as Spannable, 0)
                            v.invalidate()
                            viewModel.displayMovie(tag.id)
                        }
                    }
                    spannableString.setSpan(
                        clickableSpan, startIndexOfLink, startIndexOfLink + tag.title.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    startIndexOfLink += tag.title.length + 2
                }
                binding.tvTagsPostItem.movementMethod = LinkMovementMethod.getInstance()
                binding.tvTagsPostItem.setText(spannableString, TextView.BufferType.SPANNABLE)
            } else {
                if (binding.tvTagsPostItem.visibility == View.VISIBLE)
                    binding.tvTagsPostItem.visibility = View.GONE
            }
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemPostProfileBinding.inflate(layoutInflater,
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
        holder.bind(item, viewModel)
    }

    companion object DiffCallback: DiffUtil.ItemCallback<PostDto>() {

        override fun areItemsTheSame(oldItem: PostDto, newItem: PostDto): Boolean {
            return oldItem.postId == newItem.postId
        }

        override fun areContentsTheSame(oldItem: PostDto, newItem: PostDto): Boolean {
            return oldItem == newItem
        }

    }

}