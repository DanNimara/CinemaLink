package com.dnimara.cinemalink.ui.feedscreen

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.dnimara.cinemalink.R
import com.dnimara.cinemalink.application.CinemaLinkApplication
import com.dnimara.cinemalink.database.CinemaLinkDatabase
import com.dnimara.cinemalink.databinding.DialogEditPostCustomBinding
import com.dnimara.cinemalink.databinding.FragmentFeedBinding
import com.dnimara.cinemalink.utils.SessionManager
import com.dnimara.cinemalink.utils.SimpleDividerItemDecoration
import com.google.android.material.bottomnavigation.BottomNavigationView


class FeedFragment : Fragment() {

    lateinit var binding: FragmentFeedBinding
    private lateinit var feedViewModel: FeedViewModel
    private lateinit var feedAdapter: FeedAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_feed,
            container, false)

        val application = CinemaLinkApplication.mInstance
        val dataSource = CinemaLinkDatabase.getDatabase(application)
        val viewModelFactory = FeedViewModelFactory(dataSource, application)
        feedViewModel = ViewModelProvider(this, viewModelFactory)
            .get(FeedViewModel::class.java)

        binding.fabNewPost.setOnClickListener {
            findNavController().navigate(FeedFragmentDirections.actionFeedFragmentToCreatePostFragment())
        }

        feedAdapter = FeedAdapter(feedViewModel)
        binding.rvFeed.apply {
            addItemDecoration(SimpleDividerItemDecoration(context, R.drawable.line_divider))
            adapter = feedAdapter
        }

        feedViewModel.feed.observe(viewLifecycleOwner) {
            it?.apply {
                val oldSize = feedAdapter.currentList.size
                feedAdapter.submitList(it)
                feedViewModel.loadingComplete()
                if (oldSize < it.size) {
                    binding.rvFeed.postDelayed({
                        binding.rvFeed.scrollToPosition(0)
                    }, 250)
                }
            }
        }
        feedViewModel.loadingScreen.observe(viewLifecycleOwner) {
            this.activity?.runOnUiThread {
                if (it) {
                    if (binding.loadingFeedPanel.visibility == View.GONE) {
                        binding.loadingFeedPanel.visibility = View.VISIBLE
                    }
                    if (binding.clFeedFragment.visibility == View.VISIBLE) {
                        binding.clFeedFragment.visibility = View.GONE
                    }
                } else {
                    if (binding.loadingFeedPanel.visibility == View.VISIBLE) {
                        binding.loadingFeedPanel.visibility = View.GONE
                    }
                    if (binding.clFeedFragment.visibility == View.GONE) {
                        binding.clFeedFragment.visibility = View.VISIBLE
                    }
                }
            }
        }
        feedViewModel.navigateToUserProfile.observe(viewLifecycleOwner) {
            it?.let {
                findNavController().navigate(
                    FeedFragmentDirections.actionFeedFragmentToUserFragment(
                        it
                    )
                )
                feedViewModel.navigateToUserProfileComplete()
            }
        }
        feedViewModel.navigateToSelectedMovie.observe(viewLifecycleOwner) {
            if (it != null) {
                findNavController().navigate(
                    FeedFragmentDirections.actionFeedFragmentToMovieFragment(
                        it
                    )
                )
                feedViewModel.displayMovieComplete()
            }
        }
        feedViewModel.navigateToPostScreen.observe(viewLifecycleOwner) {
            it?.let {
                findNavController().navigate(
                    FeedFragmentDirections.actionFeedFragmentToPostFragment(it)
                )
                feedViewModel.navigateToPostComplete()
            }
        }
        feedViewModel.editPost.observe(viewLifecycleOwner) {
            it?.let {
                displayEditPostAlertDialog(feedViewModel.editPost.value!!, feedViewModel.editContent.value!!)
            }
        }
        feedViewModel.deletePost.observe(viewLifecycleOwner) {
            it?.let {
                displayAreYouSureDeletePostAlertDialog(it)
            }
        }
        feedViewModel.shareContent.observe(viewLifecycleOwner) {
            it?.let {
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, if (it.reference == "") CinemaLinkApplication
                        .mInstance.applicationContext.resources.getString(
                            R.string.share_content_no_tag, it.username, it.content)
                    else CinemaLinkApplication.mInstance.applicationContext.resources.getString(
                            R.string.share_content, it.username, it.content, it.reference))
                    type = "text/plain"
                }

                val shareIntent = Intent.createChooser(sendIntent, null)
                startActivity(shareIntent)
                feedViewModel.shareComplete()
            }
        }

        binding.lifecycleOwner = this
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
            feedViewModel.editPostComplete()
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
                feedViewModel.editPostReq(
                    postId, EditDto(dialogEditCustomBinding
                        .etEditPostDialog.text.toString().trim()))
                feedViewModel.editPostComplete()
                dialog.dismiss()
                Toast.makeText(
                    CinemaLinkApplication.mInstance.applicationContext,
                    "Post edited.", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun displayAreYouSureDeletePostAlertDialog(postId: Long) {
        val builder = AlertDialog.Builder(activity, R.style.MyDialogTheme)
        builder.setMessage("Are you sure you want to delete this post?")
            .setPositiveButton("YES") { _, _ ->
                run {
                    feedViewModel.deletePostReq(postId)
                    feedViewModel.deletePostComplete()
                }
            }
            .setNegativeButton("CANCEL") { _, _ -> run {
                                                feedViewModel.deletePostComplete()
                                                }
            }
            .setCancelable(false)
            .show()
    }

    override fun onStart() {
        super.onStart()

        feedViewModel.refreshFeed(SessionManager.mInstance.getToken()!!)

        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
        activity?.runOnUiThread {
            if (navBar.visibility == View.GONE) {
                navBar.visibility = View.VISIBLE
            }
        }
    }

}