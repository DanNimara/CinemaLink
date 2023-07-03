package com.dnimara.cinemalink.ui.reviewscreen

import android.annotation.SuppressLint
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dnimara.cinemalink.R
import com.dnimara.cinemalink.databinding.ListItemCommentBinding
import com.dnimara.cinemalink.domain.FeedPost
import com.dnimara.cinemalink.ui.feedscreen.CommentDto
import com.dnimara.cinemalink.ui.reviewscreen.ReviewViewModel
import com.dnimara.cinemalink.utils.SessionManager
import java.util.*

class ReviewCommentAdapter(val viewModel: ReviewViewModel):
    ListAdapter<CommentDto, ReviewCommentAdapter.ViewHolder>(DiffCallback) {

    class ViewHolder private constructor(val binding: ListItemCommentBinding):
        RecyclerView.ViewHolder(binding.root) {

            @SuppressLint("ClickableViewAccessibility")
            fun bind(item: CommentDto, viewModel: ReviewViewModel) {
                binding.comment = item
                binding.tvCommentCreated.text = DateUtils.getRelativeTimeSpanString(item.commentTime,
                    Calendar.getInstance().timeInMillis, DateUtils.MINUTE_IN_MILLIS)
                binding.likeCommentButton.setOnClickListener {
                    viewModel.reactToComment(item.id, true)
                }
                binding.dislikeCommentButton.setOnClickListener {
                    viewModel.reactToComment(item.id, false)
                }
                binding.tvCommentPosterUsername.setOnClickListener {
                    viewModel.navigateToUserProfile(item.userId)
                }
                binding.ivCommentProfilePic.setOnTouchListener { v, event ->
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
                    binding.likeCommentButton.setImageResource(R.drawable.ic_arrow_up_12_white)
                    binding.dislikeCommentButton.setImageResource(R.drawable.ic_arrow_down_12_white)
                } else if (item.liked == true) {
                    binding.likeCommentButton.setImageResource(R.drawable.ic_arrow_up_12_blue)
                    binding.dislikeCommentButton.setImageResource(R.drawable.ic_arrow_down_12_white)
                } else if (item.liked == false) {
                    binding.likeCommentButton.setImageResource(R.drawable.ic_arrow_up_12_white)
                    binding.dislikeCommentButton.setImageResource(R.drawable.ic_arrow_down_12_red)
                }
                if (item.userId == SessionManager.mInstance.getUserId()) {
                    binding.ibDeleteComment.visibility = View.VISIBLE
                    binding.ibEditComment.visibility = View.VISIBLE
                } else {
                    binding.ibDeleteComment.visibility = View.GONE
                    binding.ibEditComment.visibility = View.GONE
                }
                binding.ibEditComment.setOnClickListener {
                    viewModel.editComment(item.id, item.content)
                }
                binding.ibDeleteComment.setOnClickListener {
                    viewModel.deleteComment(item.id)
                }
                binding.executePendingBindings()
            }

            companion object {
                fun from(parent: ViewGroup): ViewHolder {
                    val layoutInflater = LayoutInflater.from(parent.context)
                    val binding = ListItemCommentBinding.inflate(layoutInflater,
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

    companion object DiffCallback: DiffUtil.ItemCallback<CommentDto>() {

        override fun areItemsTheSame(oldItem: CommentDto, newItem: CommentDto): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CommentDto, newItem: CommentDto): Boolean {
            return oldItem == newItem
        }

    }

}