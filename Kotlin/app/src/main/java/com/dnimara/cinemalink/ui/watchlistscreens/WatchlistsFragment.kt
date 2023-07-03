package com.dnimara.cinemalink.ui.watchlistscreens

import android.app.AlertDialog
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
import com.dnimara.cinemalink.databinding.DialogNewWatchlistCustomBinding
import com.dnimara.cinemalink.databinding.FragmentWatchlistsBinding
import com.dnimara.cinemalink.utils.SimpleDividerItemDecoration
import com.google.android.material.bottomnavigation.BottomNavigationView

class WatchlistsFragment : Fragment() {

    lateinit var binding: FragmentWatchlistsBinding
    private lateinit var watchlistsViewModel: WatchlistsViewModel
    private lateinit var watchlistsAdapter: WatchlistsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_watchlists,
            container, false)

        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
        activity?.runOnUiThread {
            if (navBar.visibility == View.VISIBLE) {
                navBar.visibility = View.GONE
            }
        }

        val application = CinemaLinkApplication.mInstance
        val dataSource = CinemaLinkDatabase.getDatabase(application)
        val viewModelFactory = WatchlistsViewModelFactory(dataSource, application)
        watchlistsViewModel = ViewModelProvider(this, viewModelFactory)
            .get(WatchlistsViewModel::class.java)

        watchlistsAdapter = WatchlistsAdapter(WatchlistsAdapter.WatchlistsListener {
            watchlistsViewModel.showWatchlist(it)
        })
        binding.rvWatchlists.apply {
            addItemDecoration(SimpleDividerItemDecoration(context, R.drawable.line_divider))
            adapter = watchlistsAdapter
        }

        watchlistsViewModel.watchlists.observe(viewLifecycleOwner) {
            this.activity?.runOnUiThread {
                if (binding.loadingWatchlistsPanel.visibility == View.VISIBLE)
                    binding.loadingWatchlistsPanel.visibility = View.GONE
                if (binding.clWatchlistsFragment.visibility == View.GONE)
                    binding.clWatchlistsFragment.visibility = View.VISIBLE
            }
            it?.apply {
                watchlistsAdapter.submitList(it)
            }
        }

        watchlistsViewModel.navigateToSelectedWatchlist.observe(viewLifecycleOwner) {
            it?.let {
                findNavController().navigate(
                    WatchlistsFragmentDirections.actionWatchlistsFragmentToShowWatchlistFragment(
                        it
                    )
                )
                watchlistsViewModel.showWatchlistComplete()
            }
        }

        watchlistsViewModel.eventNetworkError.observe(viewLifecycleOwner) {
            it?.let {
                val errorBuilder = AlertDialog.Builder(activity, R.style.MyDialogTheme)
                errorBuilder.setTitle("ERROR")
                errorBuilder.setMessage(it)
                errorBuilder.setPositiveButton("OK") { _, _ -> }
                errorBuilder.show()
                watchlistsViewModel.showErrorDone()
            }
        }

        binding.fabCreateWatchlist.setOnClickListener {
            displayCreateWatchlistDialog()
        }

        return binding.root
    }

    private fun displayCreateWatchlistDialog() {
        val addWatchlistBuilder = AlertDialog.Builder(activity)
        val addWatchlistDialogBinding: DialogNewWatchlistCustomBinding =
            DataBindingUtil.inflate(LayoutInflater.from(context),
                R.layout.dialog_new_watchlist_custom, null, false)
        addWatchlistBuilder.setView(addWatchlistDialogBinding.root)

        val dialogNewWatchlist = addWatchlistBuilder.create()

        dialogNewWatchlist.show()

        addWatchlistDialogBinding.createNewWatchlistCustomDialog.setOnClickListener {
            if (addWatchlistDialogBinding.etNewWatchlistNameDialog.text.toString().isNullOrBlank()) {
                Toast.makeText(
                    CinemaLinkApplication.mInstance.applicationContext,
                    "No text present.", Toast.LENGTH_SHORT
                ).show()
            } else if (addWatchlistDialogBinding.etNewWatchlistNameDialog.text.toString().length > 50) {
                Toast.makeText(
                    CinemaLinkApplication.mInstance.applicationContext,
                    "Chosen name is too long.", Toast.LENGTH_SHORT
                ).show()
            } else {
                watchlistsViewModel.createWatchlist(addWatchlistDialogBinding.etNewWatchlistNameDialog.text.toString())
                dialogNewWatchlist.dismiss()
            }
        }
        addWatchlistDialogBinding.cancelNewWatchlistButtonCustomDialog.setOnClickListener {
            dialogNewWatchlist.dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        watchlistsViewModel.refreshWatchlists()
    }

}