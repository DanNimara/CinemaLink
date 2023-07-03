package com.dnimara.cinemalink.ui.feedscreen

import android.annotation.SuppressLint
import android.text.*
import android.text.format.DateUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.bold
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dnimara.cinemalink.R
import com.dnimara.cinemalink.databinding.ListItemPostBinding
import com.dnimara.cinemalink.domain.FeedPost
import com.dnimara.cinemalink.domain.ShareDto
import com.dnimara.cinemalink.utils.SessionManager
import java.util.*


class FeedAdapter(val viewModel: FeedViewModel):
    ListAdapter<FeedPost, FeedAdapter.ViewHolder>(DiffCallback) {

    class ViewHolder private constructor(val binding: ListItemPostBinding):
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("ClickableViewAccessibility")
        fun bind(item: FeedPost, viewModel: FeedViewModel) {
            binding.post = item
            binding.tvFeedCreated.text = DateUtils.getRelativeTimeSpanString(item.postingTime,
                Calendar.getInstance().timeInMillis, DateUtils.MINUTE_IN_MILLIS)
            binding.likeFeedPostButton.setOnClickListener {
                viewModel.react(item.postId, true)
            }
            binding.dislikeFeedPostButton.setOnClickListener {
                viewModel.react(item.postId, false)
            }
            binding.tvFeedPosterUsername.setOnClickListener {
                viewModel.navigateToUserProfile(item.userId)
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
            binding.ivFeedProfilePic.setOnTouchListener { v, event ->
                    when(event.action)
                    {
                        MotionEvent.ACTION_DOWN -> (v as ImageView).imageAlpha = 200
                        MotionEvent.ACTION_MOVE ->
                        {
                            if (event.x > 0 && event.x < v.width && event.y > 0 && event.y < v.height) {
                                (v as ImageView).imageAlpha = 200
                            } else {
                                (v as ImageView).imageAlpha = 255
                            }
                        }
                        MotionEvent.ACTION_UP -> {
                            (v as ImageView).imageAlpha = 255
                            viewModel.navigateToUserProfile(item.userId)
                        }
                    }
                    true
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
                if (binding.tvTagsFeedItem.visibility == View.GONE)
                    binding.tvTagsFeedItem.visibility = View.VISIBLE
                binding.tvTagsFeedItem.text = SpannableStringBuilder()
                    .bold{append("Referencing: ")}
                    .append(item.tags.map {
                        tag -> tag.title
                    }.joinToString(separator = ", "))
                var startIndexOfLink = 13
                var spannableString = SpannableString(binding.tvTagsFeedItem.text.toString())
                for (tag in item.tags) {
                    val clickableSpan = object: ClickableSpan() {
                        override fun updateDrawState(ds: TextPaint) {
                            ds.color = ds.linkColor
                            ds.isFakeBoldText = true
                        }

                        override fun onClick(v: View) {
                            Selection.setSelection((v as TextView).text as Spannable, 0)
                            v.invalidate()
                            viewModel.displayMovie(tag.movieId)
                        }
                    }
                    spannableString.setSpan(
                        clickableSpan, startIndexOfLink, startIndexOfLink + tag.title.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    startIndexOfLink += tag.title.length + 2
                }
                binding.tvTagsFeedItem.movementMethod = LinkMovementMethod.getInstance()
                binding.tvTagsFeedItem.setText(spannableString, TextView.BufferType.SPANNABLE)
            } else {
                if (binding.tvTagsFeedItem.visibility == View.VISIBLE)
                    binding.tvTagsFeedItem.visibility = View.GONE
            }
            if (item.userId == SessionManager.mInstance.getUserId()) {
                binding.ibDeleteFeed.visibility = View.VISIBLE
                binding.ibEditFeed.visibility = View.VISIBLE
            } else {
                binding.ibDeleteFeed.visibility = View.GONE
                binding.ibEditFeed.visibility = View.GONE
            }
            binding.ibEditFeed.setOnClickListener {
                viewModel.editPost(item.postId, item.content)
            }
            binding.ibDeleteFeed.setOnClickListener {
                viewModel.deletePost(item.postId)
            }
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemPostBinding.inflate(layoutInflater,
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

    companion object DiffCallback: DiffUtil.ItemCallback<FeedPost>() {

        override fun areItemsTheSame(oldItem: FeedPost, newItem: FeedPost): Boolean {
            return oldItem.postId == newItem.postId
        }

        override fun areContentsTheSame(oldItem: FeedPost, newItem: FeedPost): Boolean {
            return oldItem == newItem
        }

    }

}