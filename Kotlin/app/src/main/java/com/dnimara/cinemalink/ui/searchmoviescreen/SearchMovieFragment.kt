package com.dnimara.cinemalink.ui.searchmoviescreen

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dnimara.cinemalink.R
import com.dnimara.cinemalink.databinding.FragmentSearchMovieBinding
import com.dnimara.cinemalink.utils.SimpleDividerItemDecoration
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.*


class SearchMovieFragment : Fragment() {

    lateinit var binding: FragmentSearchMovieBinding
    private lateinit var searchMovieViewModel: SearchMovieViewModel
    private var searchTerm = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        searchMovieViewModel = ViewModelProvider(this).get(SearchMovieViewModel::class.java)

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search_movie,
                container, false)

        val adapter = SearchMovieAdapter(SearchMovieAdapter.SearchMovieListener {
            searchMovieViewModel.displayMovie(it)
        })
        binding.movieSearchList.apply {
            addItemDecoration(SimpleDividerItemDecoration(context, R.drawable.line_divider))
            this.adapter = adapter
        }

        searchMovieViewModel.searchMovies.observe(viewLifecycleOwner) {
            this.activity?.runOnUiThread {
                if (binding.loadingMovieSearchPanel.visibility == View.VISIBLE) {
                    binding.loadingMovieSearchPanel.visibility = View.GONE
                }
                if (binding.clMovieSearch.visibility == View.GONE) {
                    binding.clMovieSearch.visibility = View.VISIBLE
                }
            }
            it?.let {
                adapter.submitList(it)
                binding.movieSearchList.postDelayed( {
                    binding.movieSearchList.scrollToPosition(0)
                }, 250)
            }
        }
        searchMovieViewModel.navigateToSelectedMovie.observe(viewLifecycleOwner) {
            if (null != it) {
                findNavController().navigate(
                    SearchMovieFragmentDirections.actionSearchMovieFragmentToMovieFragment(
                        it.id
                    )
                )
                searchMovieViewModel.displayMovieComplete()
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
                            searchMovieViewModel.search(searchTerm)
                        }
                    },
                    DELAY
                )
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
        }

        binding.etSearchMovie.addTextChangedListener(textWatcher)

        binding.lifecycleOwner = this.viewLifecycleOwner

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        searchMovieViewModel.search(binding.etSearchMovie.text.toString().trim())

        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
        activity?.runOnUiThread {
            if (navBar.visibility == View.GONE) {
                navBar.visibility = View.VISIBLE
            }
        }
    }

}