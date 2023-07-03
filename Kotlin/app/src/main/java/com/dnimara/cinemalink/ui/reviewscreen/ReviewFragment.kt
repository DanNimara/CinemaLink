package com.dnimara.cinemalink.ui.reviewscreen

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.*
import android.text.format.DateUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.dnimara.cinemalink.R
import com.dnimara.cinemalink.application.CinemaLinkApplication
import com.dnimara.cinemalink.databinding.DialogEditCustomBinding
import com.dnimara.cinemalink.databinding.FragmentReviewBinding
import com.dnimara.cinemalink.ui.feedscreen.EditDto
import com.dnimara.cinemalink.utils.SimpleDividerItemDecoration
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.*

class ReviewFragment : Fragment() {

    lateinit var binding: FragmentReviewBinding
    private lateinit var reviewViewModel: ReviewViewModel
    private lateinit var commentAdapter: ReviewCommentAdapter

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_review,
            container, false)

        reviewViewModel = ViewModelProvider(this)[ReviewViewModel::class.java]

        val args = ReviewFragmentArgs.fromBundle(requireArguments())
        reviewViewModel.getReview(args.id)

        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
        activity?.runOnUiThread {
            if (navBar.visibility == View.VISIBLE) {
                navBar.visibility = View.GONE
            }
        }

        commentAdapter = ReviewCommentAdapter(reviewViewModel)
        binding.rvReviewComments.apply {
            addItemDecoration(SimpleDividerItemDecoration(context, R.drawable.line_divider))
            adapter = commentAdapter
        }

        reviewViewModel.review.observe(viewLifecycleOwner) {
            it?.let {
                binding.review = it
                commentAdapter.submitList(it.comments)
            }
            activity?.runOnUiThread {
                binding.tvReviewCreated.text = DateUtils.getRelativeTimeSpanString(it.reviewTime,
                    Calendar.getInstance().timeInMillis, DateUtils.MINUTE_IN_MILLIS)
                if (it.liked == null) {
                    binding.likeReviewButton.setImageResource(R.drawable.ic_thumb_up_24)
                    binding.dislikeReviewButton.setImageResource(R.drawable.ic_thumb_down_24)
                } else if (it.liked == true) {
                    binding.likeReviewButton.setImageResource(R.drawable.ic_thumb_up_24_blue)
                    binding.dislikeReviewButton.setImageResource(R.drawable.ic_thumb_down_24)
                } else if (it.liked == false) {
                    binding.likeReviewButton.setImageResource(R.drawable.ic_thumb_up_24)
                    binding.dislikeReviewButton.setImageResource(R.drawable.ic_thumb_down_24_red)
                }

                if (binding.loadingReviewPanel.visibility == View.VISIBLE) {
                    binding.loadingReviewPanel.visibility = View.GONE
                }
                if (binding.clReviewFragment.visibility == View.GONE) {
                    binding.clReviewFragment.visibility = View.VISIBLE
                }
            }
        }

        reviewViewModel.navigateToUserProfile.observe(viewLifecycleOwner) {
            it?.let {
                findNavController().navigate(
                    ReviewFragmentDirections.actionReviewFragmentToUserFragment(it)
                )
                reviewViewModel.navigateToUserProfileComplete()
            }
        }

        reviewViewModel.editComment.observe(viewLifecycleOwner) {
            it?.let {
                displayEditCommentAlertDialog(reviewViewModel.editComment.value!!, reviewViewModel.editContent.value!!)
            }
        }
        reviewViewModel.deleteComment.observe(viewLifecycleOwner) {
            it?.let {
                displayAreYouSureDeleteCommentAlertDialog(it)
            }
        }

        val textWatcherComment = object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {
                activity?.runOnUiThread {
                    if (s?.isBlank() == false) {
                        binding.addReviewCommentButton.setImageResource(R.drawable.ic_send_24_blue)
                        binding.addReviewCommentButton.isClickable = true
                    } else {
                        binding.addReviewCommentButton.setImageResource(R.drawable.ic_send_24)
                        binding.addReviewCommentButton.isClickable = false
                    }
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
        }

        binding.etReviewComment.addTextChangedListener(textWatcherComment)

        binding.ivReviewProfilePic.setOnTouchListener { v, event ->
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
                    reviewViewModel.navigateToUserProfile(reviewViewModel.review.value?.userId!!)
                }
            }
            true
        }
        binding.tvReviewPosterUsername.setOnClickListener {
            reviewViewModel.navigateToUserProfile(reviewViewModel.review.value?.userId!!)
        }

        binding.addReviewCommentButton.setOnClickListener {
            if (binding.etReviewComment.text.toString().trim().length > 1000) {
                Toast.makeText(context, "Comments cannot be longer than " +
                        "1000 characters.", Toast.LENGTH_SHORT).show()
            } else {
                if (binding.etReviewComment.text.toString().trim().isNotEmpty()) {
                    hideKeyboard()
                    reviewViewModel.addComment(
                        reviewViewModel.review.value?.id!!,
                        binding.etReviewComment.text.toString().trim()
                    )
                    binding.etReviewComment.setText("")
                }
            }
        }
        binding.addReviewCommentButton.isClickable = false

        binding.shareReviewButton.setOnClickListener {

            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(
                    Intent.EXTRA_TEXT, CinemaLinkApplication.mInstance.applicationContext.resources.getString(
                        R.string.share_content, reviewViewModel.review.value?.username,
                        reviewViewModel.review.value?.content, reviewViewModel.review.value?.movieTitle))
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }

        binding.likeReviewButton.setOnClickListener {
            reviewViewModel.reactToReview(reviewViewModel.review.value?.id!!, true)
        }
        binding.dislikeReviewButton.setOnClickListener {
            reviewViewModel.reactToReview(reviewViewModel.review.value?.id!!, false)
        }

        return binding.root
    }

    private fun displayEditCommentAlertDialog(commentId: Long, content: String) {
        val builder = AlertDialog.Builder(activity)
        val dialogEditCommentCustomBinding: DialogEditCustomBinding =
            DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_edit_custom,
                null, false)
        dialogEditCommentCustomBinding.etEditDialog.setText(content)
        builder.setCancelable(false)
        builder.setView(dialogEditCommentCustomBinding.root)

        val dialog = builder.create()
        dialog.show()

        dialogEditCommentCustomBinding.cancelEditDialog.setOnClickListener {
            reviewViewModel.editCommentComplete()
            dialog.dismiss()
        }
        dialogEditCommentCustomBinding.buttonEditDialog.setOnClickListener {
            if (dialogEditCommentCustomBinding.etEditDialog.text.toString().isBlank()) {
                dialogEditCommentCustomBinding.etEditDialog.error =
                    "A comment is required, otherwise delete!"
            } else if (dialogEditCommentCustomBinding.etEditDialog.text.toString().length > 1000) {
                Toast.makeText(
                    CinemaLinkApplication.mInstance.applicationContext,
                    "The comment is too long.", Toast.LENGTH_SHORT
                ).show()
            } else {
                reviewViewModel.editCommentReq(
                    commentId, EditDto(dialogEditCommentCustomBinding
                        .etEditDialog.text.toString().trim())
                )
                reviewViewModel.editCommentComplete()
                dialog.dismiss()
                Toast.makeText(
                    CinemaLinkApplication.mInstance.applicationContext,
                    "Comment edited!", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun displayAreYouSureDeleteCommentAlertDialog(commentId: Long) {
        val builder = AlertDialog.Builder(activity, R.style.MyDialogTheme)
        builder.setMessage("Are you sure you want to delete this comment?")
            .setPositiveButton("YES") { _, _ ->
                run {
                    reviewViewModel.deleteCommentReq(commentId)
                    reviewViewModel.deleteCommentComplete()
                }
            }
            .setNegativeButton("CANCEL") { _, _ -> run { reviewViewModel.deleteCommentComplete() } }
            .setCancelable(false)
            .show()
    }

    fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

}