package com.dnimara.cinemalink.ui.userscreen

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.dnimara.cinemalink.R
import com.dnimara.cinemalink.application.CinemaLinkApplication
import com.dnimara.cinemalink.databinding.DialogFollowCustomBinding
import com.dnimara.cinemalink.databinding.FragmentUserBinding
import com.dnimara.cinemalink.ui.feedscreen.FeedFragmentDirections
import com.dnimara.cinemalink.ui.profilescreen.ProfileAdapter
import com.dnimara.cinemalink.ui.profilescreen.ProfileFragmentArgs
import com.dnimara.cinemalink.ui.profilescreen.ProfileFragmentDirections
import com.dnimara.cinemalink.ui.profilescreen.ProfileViewModel
import com.dnimara.cinemalink.ui.searchuserscreen.SearchUserAdapter
import com.dnimara.cinemalink.ui.searchuserscreen.UserDto
import com.dnimara.cinemalink.utils.SimpleDividerItemDecoration
import com.google.android.material.bottomnavigation.BottomNavigationView

class UserFragment : Fragment() {

    lateinit var binding: FragmentUserBinding
    private lateinit var userViewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_user, container, false)

        userViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)

        val args = ProfileFragmentArgs.fromBundle(requireArguments())
        userViewModel.getProfileInfo(args.userId)

        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
        activity?.runOnUiThread {
            if (navBar.visibility == View.VISIBLE) {
                navBar.visibility = View.GONE
            }
        }

        val userAdapter = ProfileAdapter(userViewModel)
        binding.rvProfileVisitedUserPosts.apply {
            addItemDecoration(SimpleDividerItemDecoration(context, R.drawable.line_divider))
            adapter = userAdapter
        }

        userViewModel.profileInfo.observe(viewLifecycleOwner) {
            it?.apply {
                (activity as AppCompatActivity).supportActionBar?.title = it.username
                binding.profile = it
                activity?.runOnUiThread {
                    if (!it.allowFollow) {
                        binding.followButtonUserProfile.visibility = View.GONE
                    } else {
                        binding.followButtonUserProfile.visibility = View.VISIBLE
                    }
                    if (it.followed) {
                        binding.followButtonUserProfile.text = "following"
                    } else {
                        binding.followButtonUserProfile.text = "follow"
                    }
                }
                userAdapter.submitList(it.posts)
            }
            if (binding.loadingUserPanel.visibility == View.VISIBLE) {
                binding.loadingUserPanel.visibility = View.GONE
            }
            if (binding.mlUser.visibility == View.GONE) {
                binding.mlUser.visibility = View.VISIBLE
            }
        }

        userViewModel.navigateToSelectedMovie.observe(viewLifecycleOwner) {
            if (it != null) {
                findNavController().navigate(
                    UserFragmentDirections.actionUserFragmentToMovieFragment(
                        it
                    )
                )
                userViewModel.displayMovieComplete()
            }
        }
        userViewModel.navigateToSelectedUserProfile.observe(viewLifecycleOwner) {
            if (it != null) {
                findNavController().navigate(
                    UserFragmentDirections
                        .actionUserFragmentSelf(it))
                userViewModel.displayUserProfileComplete()
            }
        }
        userViewModel.navigateToPostScreen.observe(viewLifecycleOwner) {
            it?.let {
                findNavController().navigate(
                    UserFragmentDirections.actionUserFragmentToPostFragment(it)
                )
                userViewModel.navigateToPostComplete()
            }
        }

        userViewModel.shareContent.observe(viewLifecycleOwner) {
            it?.let {
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(
                        Intent.EXTRA_TEXT, if (it.reference == "") CinemaLinkApplication
                        .mInstance.applicationContext.resources.getString(
                            R.string.share_content_no_tag, it.username, it.content)
                    else CinemaLinkApplication.mInstance.applicationContext.resources.getString(
                        R.string.share_content, it.username, it.content, it.reference))
                    type = "text/plain"
                }

                val shareIntent = Intent.createChooser(sendIntent, null)
                startActivity(shareIntent)
                userViewModel.shareComplete()
            }
        }

        binding.followButtonUserProfile.setOnClickListener {
            userViewModel.followUser()
        }

        binding.followerStatUser.setOnClickListener {
            if (!userViewModel.profileInfo.value?.followerUsers.isNullOrEmpty()) {
                displayShowFollowDialog(userViewModel.profileInfo.value?.followerUsers!!)
            }
        }
        binding.followingStatUser.setOnClickListener {
            if (!userViewModel.profileInfo.value?.followingUsers.isNullOrEmpty()) {
                displayShowFollowDialog(userViewModel.profileInfo.value?.followingUsers!!)
            }
        }

        binding.lifecycleOwner = this.viewLifecycleOwner
        return binding.root
    }

    private fun displayShowFollowDialog(users: List<UserDto>) {
        val builder = AlertDialog.Builder(activity)
        lateinit var dialog: AlertDialog
        val dialogBinding: DialogFollowCustomBinding = DataBindingUtil.inflate(LayoutInflater.from(context),
            R.layout.dialog_follow_custom, null, false)
        val userAdapter = SearchUserAdapter(SearchUserAdapter.SearchUserListener {
            userViewModel.displayUserProfile(it)
            dialog.dismiss()
        })
        dialogBinding.rvFollowDialog.apply {
            addItemDecoration(SimpleDividerItemDecoration(context, R.drawable.line_divider))
            adapter = userAdapter
        }
        userAdapter.submitList(users)
        builder.setView(dialogBinding.root)

        dialog = builder.create()
        dialog.show()
    }

}