package com.dnimara.cinemalink.ui.searchuserscreen

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dnimara.cinemalink.R
import com.dnimara.cinemalink.databinding.FragmentSearchUserBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.*

class SearchUserFragment : Fragment() {

    private lateinit var binding: FragmentSearchUserBinding
    private lateinit var searchUserViewModel: SearchUserViewModel
    private var searchTerm = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        searchUserViewModel = ViewModelProvider(this).get(SearchUserViewModel::class.java)

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search_user,
            container, false)

        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
        activity?.runOnUiThread {
            if (navBar.visibility == View.VISIBLE) {
                navBar.visibility = View.GONE
            }
        }

        val adapter = SearchUserAdapter(SearchUserAdapter.SearchUserListener {
            searchUserViewModel.displayUserProfile(it)
        })
        binding.userSearchList.apply {
            this.layoutManager = LinearLayoutManager(context)
            this.adapter = adapter
        }

        searchUserViewModel.users.observe(viewLifecycleOwner) {
            this.activity?.runOnUiThread {
                if (binding.loadingUserSearchPanel.visibility == View.VISIBLE) {
                    binding.loadingUserSearchPanel.visibility = View.GONE
                }
                if (binding.clUserSearch.visibility == View.GONE) {
                    binding.clUserSearch.visibility = View.VISIBLE
                }
            }
            it?.let {
                adapter.submitList(it)
                binding.userSearchList.postDelayed( {
                    binding.userSearchList.scrollToPosition(0)
                }, 250)
            }
        }
        searchUserViewModel.navigateToSelectedUserProfile.observe(viewLifecycleOwner) {
            if (it != null) {
                findNavController().navigate(SearchUserFragmentDirections
                    .actionSearchUserFragmentToUserFragment(it))
                searchUserViewModel.displayUserProfileComplete()
            }
        }

        val textWatcher = object : TextWatcher {
            private var timer: Timer = Timer()
            private val DELAY: Long = 1000 // Milliseconds

            override fun afterTextChanged(s: Editable?) {
                timer.cancel()
                timer = Timer()
                timer.schedule(
                    object : TimerTask() {
                        override fun run() {
                            val searchText = s.toString().trim()
                            if (searchText == searchTerm) return

                            searchTerm = searchText
                            searchUserViewModel.search(searchTerm)
                        }
                    },
                    DELAY
                )
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
        }
        binding.etSearchUser.addTextChangedListener(textWatcher)

        binding.lifecycleOwner = this.viewLifecycleOwner
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        searchUserViewModel.search(binding.etSearchUser.text.toString().trim())
    }

}