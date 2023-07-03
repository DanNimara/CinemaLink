package com.dnimara.cinemalink.ui.reviewscreen

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.dnimara.cinemalink.R
import com.dnimara.cinemalink.application.CinemaLinkApplication
import com.dnimara.cinemalink.databinding.FragmentReviewsBinding
import com.dnimara.cinemalink.ui.feedscreen.FeedFragmentDirections
import com.dnimara.cinemalink.utils.SimpleDividerItemDecoration
import com.google.android.material.bottomnavigation.BottomNavigationView

class ReviewsFragment : Fragment() {

    lateinit var binding: FragmentReviewsBinding
    private lateinit var reviewsViewModel: ReviewsViewModel
    private lateinit var reviewsAdapter: ReviewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_reviews,
            container, false)

        reviewsViewModel = ViewModelProvider(this)[ReviewsViewModel::class.java]

        val args = ReviewsFragmentArgs.fromBundle(requireArguments())
        reviewsViewModel.refreshReviews(args.movieId)

        reviewsAdapter = ReviewsAdapter(reviewsViewModel)
        binding.rvReviewList.apply {
            addItemDecoration(SimpleDividerItemDecoration(context, R.drawable.line_divider))
            adapter = reviewsAdapter
        }

        reviewsViewModel.reviews.observe(viewLifecycleOwner) {
            this.activity?.runOnUiThread {
                if (binding.loadingReviewsPanel.visibility == View.VISIBLE) {
                    binding.loadingReviewsPanel.visibility = View.GONE
                }
                if (binding.clReviewsFragment.visibility == View.GONE) {
                    binding.clReviewsFragment.visibility = View.VISIBLE
                }
            }
            it?.apply {
                reviewsAdapter.submitList(it)
            }
        }

        reviewsViewModel.shareContent.observe(viewLifecycleOwner) {
            it?.let {
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(
                        Intent.EXTRA_TEXT, CinemaLinkApplication.mInstance
                            .applicationContext.resources.getString(R.string.share_content,
                                it.username, it.content, it.reference))
                    type = "text/plain"
                }

                val shareIntent = Intent.createChooser(sendIntent, null)
                startActivity(shareIntent)
                reviewsViewModel.shareComplete()
            }
        }

        reviewsViewModel.navigateToUserProfile.observe(viewLifecycleOwner) {
            it?.let {
                findNavController().navigate(
                    ReviewsFragmentDirections.actionReviewsFragmentToUserFragment(
                        it
                    )
                )
                reviewsViewModel.navigateToUserProfileComplete()
            }
        }

        reviewsViewModel.navigateToPostScreen.observe(viewLifecycleOwner) {
            it?.let {
                findNavController().navigate(
                    ReviewsFragmentDirections.actionReviewsFragmentToReviewFragment(it)
                )
                reviewsViewModel.navigateToReviewComplete()
            }
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        val args = ReviewsFragmentArgs.fromBundle(requireArguments())
        reviewsViewModel.refreshReviews(args.movieId)

        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
        activity?.runOnUiThread {
            if (navBar.visibility == View.GONE) {
                navBar.visibility = View.VISIBLE
            }
        }
    }

}