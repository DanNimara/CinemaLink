package com.dnimara.cinemalink.ui.collectionscreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.dnimara.cinemalink.R
import com.dnimara.cinemalink.databinding.FragmentCollectionBinding
import com.dnimara.cinemalink.utils.SimpleDividerItemDecoration
import com.google.android.material.bottomnavigation.BottomNavigationView


class CollectionFragment : Fragment() {

    private lateinit var binding: FragmentCollectionBinding
    private lateinit var collectionViewModel: CollectionViewModel
    private lateinit var collectionAdapter: CollectionAdapter
    private var initSpinner: Boolean = true
    private var listLength: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_collection,
            container, false)

        collectionViewModel = ViewModelProvider(this)[CollectionViewModel::class.java]
        collectionViewModel.getUserCollection()

        collectionAdapter = CollectionAdapter(collectionViewModel,
            CollectionAdapter.CollectionListener { collectionViewModel.displayMovie(it) })

        binding.rvMovieCollectionList.apply {
            addItemDecoration(SimpleDividerItemDecoration(context, R.drawable.line_divider))
            adapter = collectionAdapter
        }

        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
        activity?.runOnUiThread {
            if (navBar.visibility == View.VISIBLE) {
                navBar.visibility = View.GONE
            }
        }

        collectionViewModel.collection.observe(viewLifecycleOwner) {
            it?.let {
                collectionAdapter.setData(it.movies.toMutableList())

                val genresOfMovies = mutableListOf("All")
                for (movie in collectionViewModel.collection.value?.movies!!) {
                    for (genre in movie.genres!!) {
                        genresOfMovies.add(genre)
                    }
                }
                val chosen = mutableSetOf<String>()
                for (genre in genresOfMovies) {
                    chosen.add(genre)
                }
                val listOfGenres = mutableListOf<String>()
                for (genre in chosen) {
                    listOfGenres.add(genre)
                }
                val aa = ArrayAdapter(requireContext(), R.layout.spinner_text, listOfGenres.toList())
                aa.setDropDownViewResource(R.layout.custom_spinner_dropdown_item)

                binding.filterSpinner.adapter = aa
                binding.filterSpinner.onItemSelectedListener = SpinnerStateChangeListener()
                initSpinner = false
            }
            this.activity?.runOnUiThread {
                if (binding.loadingMovieCollectionPanel.visibility == View.VISIBLE) {
                    binding.loadingMovieCollectionPanel.visibility = View.GONE
                }
                if (binding.clMovieCollection.visibility == View.GONE) {
                    binding.clMovieCollection.visibility = View.VISIBLE
                }
            }
        }

        collectionViewModel.filterScroll.observe(viewLifecycleOwner) {
            if (it == true) {
                binding.rvMovieCollectionList.postDelayed( {
                    binding.rvMovieCollectionList.scrollToPosition(0)
                }, 250)
                collectionViewModel.filterScrollDone()
            }
        }

        collectionViewModel.navigateToSelectedMovie.observe(viewLifecycleOwner) {
            if (null != it) {
                findNavController().navigate(
                    CollectionFragmentDirections.actionCollectionFragmentToMovieFragment(
                        it
                    )
                )
                collectionViewModel.displayMovieComplete()
            }
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        collectionViewModel.getUserCollection()
        initSpinner = true
    }

    inner class SpinnerStateChangeListener : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            collectionAdapter.getFilter()
                .filter(parent?.getItemAtPosition(position) as CharSequence)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
            // do sth here
        }
    }

}