package com.dnimara.cinemalink.ui.recommendationscreen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.dnimara.cinemalink.R
import com.dnimara.cinemalink.databinding.FragmentRecommendationBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class RecommendationFragment : Fragment() {

    private lateinit var binding: FragmentRecommendationBinding
    private lateinit var recommendationViewModel: RecommendationViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        recommendationViewModel = ViewModelProvider(this).get(RecommendationViewModel::class.java)

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_recommendation,
            container, false)

        (activity as AppCompatActivity).supportActionBar?.title = "Recommendations"

        val adapter = RecommendationAdapter(RecommendationAdapter.RecommendationListener {
            recommendationViewModel.displayMovie(it)
        })
        binding.movieRecommendationList.apply {
            this.layoutManager = GridLayoutManager(context, 3, GridLayoutManager.VERTICAL, false)
            this.adapter = adapter
        }

        recommendationViewModel.recommendations.observe(viewLifecycleOwner) {
            this.activity?.runOnUiThread {
                if (binding.loadingRecommendationsPanel.visibility == View.VISIBLE)
                    binding.loadingRecommendationsPanel.visibility = View.GONE
                if (binding.clRecommendations.visibility == View.GONE)
                    binding.clRecommendations.visibility = View.VISIBLE
            }
            it?.let {
                adapter.submitList(it)
            }
        }

        recommendationViewModel.navigateToSelectedMovie.observe(viewLifecycleOwner) {
            if (it != null) {
                findNavController().navigate(RecommendationFragmentDirections
                    .actionRecommendationFragmentToMovieFragment(it))
                recommendationViewModel.displayMovieComplete()
            }
        }

        binding.lifecycleOwner = this.viewLifecycleOwner
        return binding.root

    }

    override fun onStart() {
        super.onStart()

        recommendationViewModel.getRecommendations()

        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
        activity?.runOnUiThread {
            if (navBar.visibility == View.GONE) {
                navBar.visibility = View.VISIBLE
            }
        }
    }

}