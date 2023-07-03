package com.dnimara.cinemalink.ui.feedscreen

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.*
import android.text.format.DateUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.*
import androidx.fragment.app.Fragment
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.text.bold
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.dnimara.cinemalink.R
import com.dnimara.cinemalink.application.CinemaLinkApplication
import com.dnimara.cinemalink.databinding.DialogEditCustomBinding
import com.dnimara.cinemalink.databinding.DialogEditPostCustomBinding
import com.dnimara.cinemalink.databinding.FragmentPostBinding
import com.dnimara.cinemalink.utils.SessionManager
import com.dnimara.cinemalink.utils.SimpleDividerItemDecoration
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.*

class PostFragment : Fragment() {

    lateinit var binding: FragmentPostBinding
    private lateinit var postViewModel: PostViewModel
    private lateinit var commentAdapter: CommentAdapter

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_post,
            container, false)

        postViewModel = ViewModelProvider(this)[PostViewModel::class.java]

        val args = PostFragmentArgs.fromBundle(requireArguments())
        postViewModel.getPost(args.id)

        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
        activity?.runOnUiThread {
            if (navBar.visibility == View.VISIBLE) {
                navBar.visibility = View.GONE
            }
        }

        commentAdapter = CommentAdapter(postViewModel)
        binding.rvPostComments.apply {
            addItemDecoration(SimpleDividerItemDecoration(context, R.drawable.line_divider))
            adapter = commentAdapter
        }

        postViewModel.post.observe(viewLifecycleOwner) {
            it?.let {
                binding.post = it
                commentAdapter.submitList(it.comments)
            }
            activity?.runOnUiThread {
                binding.tvPostCreated.text = DateUtils.getRelativeTimeSpanString(it.postingTime,
                    Calendar.getInstance().timeInMillis, DateUtils.MINUTE_IN_MILLIS)
                if (it.liked == null) {
                    binding.likePostButton.setImageResource(R.drawable.ic_thumb_up_24)
                    binding.dislikePostButton.setImageResource(R.drawable.ic_thumb_down_24)
                } else if (it.liked == true) {
                    binding.likePostButton.setImageResource(R.drawable.ic_thumb_up_24_blue)
                    binding.dislikePostButton.setImageResource(R.drawable.ic_thumb_down_24)
                } else if (it.liked == false) {
                    binding.likePostButton.setImageResource(R.drawable.ic_thumb_up_24)
                    binding.dislikePostButton.setImageResource(R.drawable.ic_thumb_down_24_red)
                }
                if (it.edited) {
                    binding.tvPostEdited.visibility = View.VISIBLE
                } else {
                    binding.tvPostEdited.visibility = View.GONE
                }
                if (it.tags != null && it.tags.isNotEmpty()) {
                    if (binding.tvTagsPostItem.visibility == View.GONE)
                        binding.tvTagsPostItem.visibility = View.VISIBLE
                    binding.tvTagsPostItem.text = SpannableStringBuilder()
                        .bold{append("Referencing: ")}
                        .append(it.tags.map {
                                tag -> tag.title
                        }.joinToString(separator = ", "))
                    var startIndexOfLink = 13
                    val spannableString = SpannableString(binding.tvTagsPostItem.text.toString())
                    for (tag in it.tags) {
                        val clickableSpan = object: ClickableSpan() {
                            override fun updateDrawState(ds: TextPaint) {
                                ds.color = ds.linkColor
                                ds.isFakeBoldText = true
                            }

                            override fun onClick(v: View) {
                                Selection.setSelection((v as TextView).text as Spannable, 0)
                                v.invalidate()
                                postViewModel.displayMovie(tag.id)
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
                if (it.userId == SessionManager.mInstance.getUserId()) {
                    binding.ibEditPost.visibility = View.VISIBLE
                    binding.ibDeletePost.visibility = View.VISIBLE
                } else {
                    binding.ibEditPost.visibility = View.GONE
                    binding.ibDeletePost.visibility = View.GONE
                }
                if (binding.loadingPostPanel.visibility == View.VISIBLE) {
                    binding.loadingPostPanel.visibility = View.GONE
                }
                if (binding.clPostFragment.visibility == View.GONE) {
                    binding.clPostFragment.visibility = View.VISIBLE
                }
            }
        }
        postViewModel.navigateToUserProfile.observe(viewLifecycleOwner) {
            it?.let {
                findNavController().navigate(
                    PostFragmentDirections.actionPostFragmentToUserFragment(it)
                )
                postViewModel.navigateToUserProfileComplete()
            }
        }
        postViewModel.navigateToFeed.observe(viewLifecycleOwner) {
            if (it != false) {
                findNavController().navigate(
                    PostFragmentDirections.actionPostFragmentToFeedFragment()
                )
                postViewModel.navigateToFeedComplete()
            }
        }
        postViewModel.navigateToSelectedMovie.observe(viewLifecycleOwner) {
            if (it != null) {
                findNavController().navigate(
                    PostFragmentDirections.actionPostFragmentToMovieFragment(
                        it
                    )
                )
                postViewModel.displayMovieComplete()
            }
        }
        postViewModel.editComment.observe(viewLifecycleOwner) {
            it?.let {
                displayEditCommentAlertDialog(postViewModel.editComment.value!!, postViewModel.editContent.value!!)
            }
        }
        postViewModel.deleteComment.observe(viewLifecycleOwner) {
            it?.let {
                displayAreYouSureDeleteCommentAlertDialog(it)
            }
        }

        val textWatcherComment = object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {
                activity?.runOnUiThread {
                    if (s?.isBlank() == false) {
                        binding.addPostCommentButton.setImageResource(R.drawable.ic_send_24_blue)
                        binding.addPostCommentButton.isClickable = true
                    } else {
                        binding.addPostCommentButton.setImageResource(R.drawable.ic_send_24)
                        binding.addPostCommentButton.isClickable = false
                    }
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
        }

        binding.etComment.addTextChangedListener(textWatcherComment)

        binding.ivPostProfilePic.setOnTouchListener { v, event ->
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
                    postViewModel.navigateToUserProfile(postViewModel.post.value?.userId!!)
                }
            }
            true
        }
        binding.tvPostPosterUsername.setOnClickListener {
            postViewModel.navigateToUserProfile(postViewModel.post.value?.userId!!)
        }

        binding.addPostCommentButton.setOnClickListener {
            if (binding.etComment.text.toString().trim().length > 1000) {
                Toast.makeText(context, "Comments cannot be longer than " +
                        "2000 characters.", Toast.LENGTH_SHORT).show()
            } else {
                if (binding.etComment.text.toString().trim().isNotEmpty()) {
                    hideKeyboard()
                    postViewModel.addComment(
                        postViewModel.post.value?.postId!!,
                        binding.etComment.text.toString().trim()
                    )
                    binding.etComment.setText("")
                }
            }
        }
        binding.addPostCommentButton.isClickable = false

        binding.ibEditPost.setOnClickListener {
            displayEditPostAlertDialog(postViewModel.post.value?.postId!!,
                                       postViewModel.post.value?.content!!)
        }
        binding.ibDeletePost.setOnClickListener {
            displayAreYouSureDeletePostAlertDialog(postViewModel.post.value?.postId!!)
        }
        binding.sharePostButton.setOnClickListener {
            var tags = ""
            if (!postViewModel.post.value?.tags.isNullOrEmpty()) {
                tags = postViewModel.post.value?.tags?.map { mov -> mov.title }?.joinToString(separator = ", ")
                    .toString()
            }

            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(
                    Intent.EXTRA_TEXT, if (tags == "") CinemaLinkApplication
                    .mInstance.applicationContext.resources.getString(
                        R.string.share_content_no_tag, postViewModel.post.value?.username,
                            postViewModel.post.value?.content)
                else CinemaLinkApplication.mInstance.applicationContext.resources.getString(
                    R.string.share_content, postViewModel.post.value?.username,
                        postViewModel.post.value?.content, tags))
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }
        binding.likePostButton.setOnClickListener {
            postViewModel.reactToPost(postViewModel.post.value?.postId!!, true)
        }
        binding.dislikePostButton.setOnClickListener {
            postViewModel.reactToPost(postViewModel.post.value?.postId!!, false)
        }

        return binding.root
    }

    private fun displayEditPostAlertDialog(postId: Long, content: String) {
        val builder = AlertDialog.Builder(activity)
        val dialogEditCustomBinding: DialogEditPostCustomBinding =
            DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_edit_post_custom,
                null, false)
        dialogEditCustomBinding.etEditPostDialog.setText(content)
        builder.setCancelable(false)
        builder.setView(dialogEditCustomBinding.root)

        val dialog = builder.create()
        dialog.show()

        dialogEditCustomBinding.cancelEditPostDialog.setOnClickListener {
            dialog.dismiss()
        }
        dialogEditCustomBinding.buttonEditPostDialog.setOnClickListener {
            if (dialogEditCustomBinding.etEditPostDialog.text.toString().isBlank()) {
                dialogEditCustomBinding.etEditPostDialog.error =
                    "A post content is required, otherwise delete!"
            } else if (dialogEditCustomBinding.etEditPostDialog.text.toString().length > 2000) {
                Toast.makeText(
                    CinemaLinkApplication.mInstance.applicationContext,
                    "The post is too long.", Toast.LENGTH_SHORT
                ).show()
            } else {
                postViewModel.editPostReq(
                    postId, EditDto(dialogEditCustomBinding
                        .etEditPostDialog.text.toString().trim()))
                dialog.dismiss()
                Toast.makeText(
                    CinemaLinkApplication.mInstance.applicationContext,
                    "Post edited.", Toast.LENGTH_SHORT
                ).show()
            }
        }
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
            postViewModel.editCommentComplete()
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
                postViewModel.editCommentReq(
                    commentId, EditDto(dialogEditCommentCustomBinding
                        .etEditDialog.text.toString().trim()))
                postViewModel.editCommentComplete()
                dialog.dismiss()
                Toast.makeText(
                    CinemaLinkApplication.mInstance.applicationContext,
                    "Comment edited.", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun displayAreYouSureDeletePostAlertDialog(postId: Long) {
        val builder = AlertDialog.Builder(activity, R.style.MyDialogTheme)
        builder.setMessage("Are you sure you want to delete this post?")
            .setPositiveButton("YES") { _, _ ->
                run {
                    postViewModel.deletePostReq(postId)
                    postViewModel.navigateToFeed()
                }
            }
            .setNegativeButton("CANCEL") { _, _ ->  }
            .show()
    }

    private fun displayAreYouSureDeleteCommentAlertDialog(commentId: Long) {
        val builder = AlertDialog.Builder(activity, R.style.MyDialogTheme)
        builder.setMessage("Are you sure you want to delete this comment?")
            .setPositiveButton("YES") { _, _ ->
                run {
                    postViewModel.deleteCommentReq(commentId)
                    postViewModel.deleteCommentComplete()
                }
            }
            .setNegativeButton("CANCEL") { _, _ -> run { postViewModel.deleteCommentComplete() } }
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