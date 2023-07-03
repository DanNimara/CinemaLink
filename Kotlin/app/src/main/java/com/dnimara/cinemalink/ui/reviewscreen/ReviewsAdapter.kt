package com.dnimara.cinemalink.ui.reviewscreen

import android.annotation.SuppressLint
import android.text.Html
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
import com.dnimara.cinemalink.application.CinemaLinkApplication
import com.dnimara.cinemalink.databinding.ListItemReviewBinding
import com.dnimara.cinemalink.domain.ShareDto
import java.util.*

class ReviewsAdapter(val viewModel: ReviewsViewModel):
    ListAdapter<ReviewDto, ReviewsAdapter.ViewHolder>(DiffCallback) {

    class ViewHolder private constructor(val binding: ListItemReviewBinding):
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("ClickableViewAccessibility", "NewApi")
        fun bind(item: ReviewDto, viewModel: ReviewsViewModel) {
            binding.review = item
            binding.tvReviewsCreated.text = DateUtils.getRelativeTimeSpanString(item.reviewTime,
                Calendar.getInstance().timeInMillis, DateUtils.MINUTE_IN_MILLIS)
            binding.likeReviewsButton.setOnClickListener {
                viewModel.react(item.id, item.movieId, true)
            }
            binding.dislikeReviewsButton.setOnClickListener {
                viewModel.react(item.id, item.movieId, false)
            }
            binding.tvReviewsPosterUsername.setOnClickListener {
                viewModel.navigateToUserProfile(item.userId)
            }
            binding.commentReviewsButton.setOnClickListener {
                viewModel.navigateToReview(item.id)
            }
            binding.shareReviewsButton.setOnClickListener {
                viewModel.share(ShareDto(item.username, item.content, item.movieTitle))
            }
            binding.ivReviewsProfilePic.setOnTouchListener { v, event ->
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
                binding.likeReviewsButton.setImageResource(R.drawable.ic_thumb_up_24)
                binding.dislikeReviewsButton.setImageResource(R.drawable.ic_thumb_down_24)
            } else if (item.liked == true) {
                binding.likeReviewsButton.setImageResource(R.drawable.ic_thumb_up_24_blue)
                binding.dislikeReviewsButton.setImageResource(R.drawable.ic_thumb_down_24)
            } else if (item.liked == false) {
                binding.likeReviewsButton.setImageResource(R.drawable.ic_thumb_up_24)
                binding.dislikeReviewsButton.setImageResource(R.drawable.ic_thumb_down_24_red)
            }
            if (item.rating != null) {
                binding.rbReviews.rating = item.rating.toFloat()
                binding.rbReviews.visibility = View.VISIBLE
            } else {
                binding.rbReviews.rating = 0f
                binding.rbReviews.visibility = View.GONE
            }
            if (item.recommended) {
                binding.tvRecommendedReviews.setText(Html.fromHtml(
                    CinemaLinkApplication.mInstance.applicationContext.resources.getString(R.string.would_recommend),
                    Html.FROM_HTML_MODE_LEGACY))
            } else {
                binding.tvRecommendedReviews.setText(Html.fromHtml(
                    CinemaLinkApplication.mInstance.applicationContext.resources.getString(R.string.would_not_recommend),
                    Html.FROM_HTML_MODE_LEGACY))
            }
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemReviewBinding.inflate(layoutInflater,
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

    companion object DiffCallback: DiffUtil.ItemCallback<ReviewDto>() {

        override fun areItemsTheSame(oldItem: ReviewDto, newItem: ReviewDto): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ReviewDto, newItem: ReviewDto): Boolean {
            return oldItem == newItem
        }

    }


}