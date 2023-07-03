package com.dnimara.cinemalink.ui.watchlistscreens

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.dnimara.cinemalink.R
import com.dnimara.cinemalink.application.CinemaLinkApplication
import com.dnimara.cinemalink.database.CinemaLinkDatabase
import com.dnimara.cinemalink.databinding.DialogRenameCustomBinding
import com.dnimara.cinemalink.databinding.FragmentShowWatchlistBinding
import com.dnimara.cinemalink.utils.SimpleDividerItemDecoration
import java.text.SimpleDateFormat
import java.util.*

class ShowWatchlistFragment : Fragment() {

    lateinit var binding: FragmentShowWatchlistBinding
    private lateinit var watchlistViewModel: ShowWatchlistViewModel
    private lateinit var watchlistMovieAdapter: WatchlistMovieAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_show_watchlist,
            container, false)

        val application = CinemaLinkApplication.mInstance
        val dataSource = CinemaLinkDatabase.getDatabase(application)

        val viewModelFactory = ShowWatchlistViewModelFactory(dataSource, application)
        watchlistViewModel = ViewModelProvider(this, viewModelFactory)[ShowWatchlistViewModel::class.java]

        watchlistMovieAdapter = WatchlistMovieAdapter(watchlistViewModel,
            WatchlistMovieAdapter.WatchlistMovieListener {
            watchlistViewModel.showMovie(it)
        })

        binding.rvWatchlistMovies.apply {
            addItemDecoration(SimpleDividerItemDecoration(context, R.drawable.line_divider))
            isNestedScrollingEnabled = false
            adapter = watchlistMovieAdapter
        }

        val args = ShowWatchlistFragmentArgs.fromBundle(requireArguments())
        watchlistViewModel.getWatchlist(args.id)

        watchlistViewModel.watchlist.observe(viewLifecycleOwner) {
            this.activity?.runOnUiThread {
                if (it.watchlistStatus == "WATCHED") {
                    binding.renameWatchlistButton.visibility = View.GONE
                    binding.deleteWatchlistButton.visibility = View.GONE
                } else {
                    binding.renameWatchlistButton.visibility = View.VISIBLE
                    binding.deleteWatchlistButton.visibility = View.VISIBLE
                }
                if (binding.loadingWatchlistPanel.visibility == View.VISIBLE) {
                    binding.loadingWatchlistPanel.visibility = View.GONE
                }
                if (binding.clShowWatchlist.visibility == View.GONE) {
                    binding.clShowWatchlist.visibility = View.VISIBLE
                }
                val sdf = SimpleDateFormat("MMM dd yyyy", Locale.ENGLISH)
                binding.tvWatchlistCreated.text =
                    CinemaLinkApplication.mInstance.applicationContext.resources.getString(
                        R.string.created_watchlist, sdf.format(it.createdTime))
            }
            it?.let {
                (activity as AppCompatActivity).supportActionBar?.title = it.name
                binding.watchlist = it
                watchlistMovieAdapter.submitList(it.movies)
            }
        }
        watchlistViewModel.navigateToSelectedMovie.observe(viewLifecycleOwner) {
            it?.let {
                findNavController().navigate(ShowWatchlistFragmentDirections
                    .actionShowWatchlistFragmentToMovieFragment(it))
                watchlistViewModel.showMovieComplete()
            }
        }
        watchlistViewModel.deleteMovie.observe(viewLifecycleOwner) {
            it?.let {
                displayAreYouSureDeleteMovieAlertDialog(it)
            }
        }

        binding.renameWatchlistButton.setOnClickListener {
            displayRenameWatchlistDialog()
        }
        binding.deleteWatchlistButton.setOnClickListener {
            displayAreYouSureAlertDialog()
        }

        return binding.root
    }

    private fun displayAreYouSureAlertDialog() {
        val builder = AlertDialog.Builder(activity, R.style.MyDialogTheme)
        builder.setMessage("Are you sure you want to delete this watchlist?")
            .setPositiveButton("YES") { _, _ ->
                run {
                    watchlistViewModel.deleteWatchlist(watchlistViewModel.watchlist.value?.id!!)
                    findNavController().navigate(ShowWatchlistFragmentDirections.actionShowWatchlistFragmentToWatchlistsFragment())
                }
            }
            .setNegativeButton("CANCEL") { _, _ -> }
            .show()
    }

    private fun displayAreYouSureDeleteMovieAlertDialog(movieId: String) {
        val builder = AlertDialog.Builder(activity, R.style.MyDialogTheme)
        builder.setMessage("Are you sure you want to delete this movie from the watchlist?")
            .setPositiveButton("YES") { _, _ ->
                run {
                    watchlistViewModel.deleteMovie(watchlistViewModel.watchlist.value?.id!!, movieId)
                    watchlistViewModel.deleteMovieComplete()
                }
            }
            .setNegativeButton("CANCEL") { _, _ -> run { watchlistViewModel.deleteMovieComplete() } }
            .setCancelable(false)
            .show()
    }

    private fun displayRenameWatchlistDialog() {
        val renameBuilder = AlertDialog.Builder(activity)
        val renameDialogBinding: DialogRenameCustomBinding =
            DataBindingUtil.inflate(LayoutInflater.from(context),
                R.layout.dialog_rename_custom, null, false)
        renameBuilder.setView(renameDialogBinding.root)

        val dialogRenameWatchlist = renameBuilder.create()

        dialogRenameWatchlist.show()

        renameDialogBinding.okRenameCustomDialog.setOnClickListener {
            if (renameDialogBinding.etRenameDialog.text.isNullOrBlank()) {
                Toast.makeText(
                    CinemaLinkApplication.mInstance.applicationContext,
                    "No text present.", Toast.LENGTH_SHORT
                ).show()
            } else if (renameDialogBinding.etRenameDialog.text.toString().length > 50) {
                Toast.makeText(
                    CinemaLinkApplication.mInstance.applicationContext,
                    "Chosen name is too long.", Toast.LENGTH_SHORT
                ).show()
            }
            else {

                watchlistViewModel.renameWatchlist(watchlistViewModel.watchlist.value?.id!!,
                    renameDialogBinding.etRenameDialog.text.toString())
                dialogRenameWatchlist.dismiss()
            }
        }
        renameDialogBinding.cancelRenameButtonCustomDialog.setOnClickListener {
            dialogRenameWatchlist.dismiss()
        }
    }

    override fun onStart() {
        super.onStart()

        if (watchlistViewModel.watchlist.value != null) {
            watchlistViewModel.refreshWatchlist(watchlistViewModel.watchlist.value?.id!!)
        }
    }

}