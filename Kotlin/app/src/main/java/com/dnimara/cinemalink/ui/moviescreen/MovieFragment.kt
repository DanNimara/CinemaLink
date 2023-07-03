package com.dnimara.cinemalink.ui.moviescreen

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.format.DateUtils
import android.view.*
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.bold
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.dnimara.cinemalink.R
import com.dnimara.cinemalink.application.CinemaLinkApplication
import com.dnimara.cinemalink.databinding.*
import com.dnimara.cinemalink.ui.feedscreen.EditDto
import com.dnimara.cinemalink.ui.reviewscreen.AddReviewDto
import com.dnimara.cinemalink.utils.SimpleDividerItemDecoration
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.text.DateFormatSymbols
import java.util.*
import kotlin.math.roundToInt

class MovieFragment : Fragment() {

    lateinit var binding: FragmentMovieBinding
    private lateinit var movieViewModel: MovieViewModel

    @SuppressLint("SetTextI18n", "NewApi")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        movieViewModel = ViewModelProvider(this).get(MovieViewModel::class.java)

        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_movie, container, false)

        val args = MovieFragmentArgs.fromBundle(requireArguments())
        movieViewModel.showInfo(args.id)

        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
        activity?.runOnUiThread {
            if (navBar.visibility == View.VISIBLE) {
                navBar.visibility = View.GONE
            }
        }

        movieViewModel.movie.observe(viewLifecycleOwner) {
            this.activity?.runOnUiThread {
                if (it.posterUrl == null) {
                    it.posterUrl = ""
                }
                if (it.availableOnNetflix == true) {
                    binding.availableOnNetflixButton.backgroundTintList = resources
                        .getColorStateList(R.color.netflix, context?.theme)
                    binding.availableOnNetflixButton.text = "On Netflix"
                } else {
                    binding.availableOnNetflixButton.backgroundTintList = resources
                        .getColorStateList(R.color.disabled_button, context?.theme)
                    if (it.availableOnNetflix == null) {
                        binding.availableOnNetflixButton.text = "No data for Netflix"
                    } else {
                        binding.availableOnNetflixButton.text = "Not on Netflix"
                    }
                }
                if (it.availableOnHBOMax == true) {
                    binding.availableOnHboButton.backgroundTintList = resources
                        .getColorStateList(R.color.hbo, context?.theme)
                    binding.availableOnHboButton.text = "On HBO Max"
                } else {
                    binding.availableOnHboButton.backgroundTintList = resources
                        .getColorStateList(R.color.disabled_button, context?.theme)
                    if (it.availableOnHBOMax == null) {
                        binding.availableOnHboButton.text = "No data for HBO Max"
                    } else {
                        binding.availableOnHboButton.text = "Not on HBO Max"
                    }
                }
                if (it.availableOnDisneyPlus == true) {
                    binding.availableOnDisneyButton.backgroundTintList = resources
                        .getColorStateList(R.color.disney, context?.theme)
                    binding.availableOnDisneyButton.text = "On Disney Plus"
                } else {
                    binding.availableOnDisneyButton.backgroundTintList = resources
                        .getColorStateList(R.color.disabled_button, context?.theme)
                    if (it.availableOnDisneyPlus == null) {
                        binding.availableOnDisneyButton.text = "No data for Disney Plus"
                    } else {
                        binding.availableOnDisneyButton.text = "Not on Disney Plus"
                    }
                }
                if (it.availableOnAmazonPrime == true) {
                    binding.availableOnAmazonButton.backgroundTintList = resources
                        .getColorStateList(R.color.amazon, context?.theme)
                    binding.availableOnAmazonButton.text = "On Amazon Prime"
                } else {
                    binding.availableOnAmazonButton.backgroundTintList = resources
                        .getColorStateList(R.color.disabled_button, context?.theme)
                    if (it.availableOnAmazonPrime == null) {
                        binding.availableOnAmazonButton.text = "No data for Amazon Prime"
                    } else {
                        binding.availableOnAmazonButton.text = "Not on Amazon Prime"
                    }
                }
                if (it.userRating != null) {
                    binding.rbMovie.rating = it.userRating!!.toFloat()
                    binding.ibRemoveRating.visibility = View.VISIBLE
                } else {
                    binding.rbMovie.rating = 0f
                    binding.ibRemoveRating.visibility = View.GONE
                }
                if (it.internalRating != null) {
                    binding.internalRatingDetail.text = ((it.internalRating!! * 100.0).roundToInt() / 100.0).toString() + "/5"
                } else {
                    binding.internalRatingDetail.text = "X/5"
                }
                if (it.imdbRating != null) {
                    binding.imdbRatingDetail.text = it.imdbRating!!.toString() + "/10"
                } else {
                    binding.imdbRatingDetail.text = "TBD"
                }
                if (it.rating != null) {
                    when(it.rating) {
                        "G" -> binding.ratingUsaImageDetail.setImageResource(R.drawable.g)
                        "PG" -> binding.ratingUsaImageDetail.setImageResource(R.drawable.pg)
                        "PG-13" -> binding.ratingUsaImageDetail.setImageResource(R.drawable.pg_13)
                        "R" -> binding.ratingUsaImageDetail.setImageResource(R.drawable.r)
                        "NC-17" -> binding.ratingUsaImageDetail.setImageResource(R.drawable.nc_17)
                        "Unrated" -> binding.ratingUsaImageDetail.setImageResource(R.drawable.unrated)
                        else -> binding.ratingUsaImageDetail.setImageResource(R.drawable.not_rated)
                    }
                } else {
                    binding.ratingUsaImageDetail.setImageResource(R.drawable.not_rated)
                }
                if (it.ratingReason != null) {
                    if (binding.ratingReasonDetail.visibility == View.GONE)
                        binding.ratingReasonDetail.visibility = View.VISIBLE
                    binding.ratingReasonDetail.text = it.ratingReason
                } else {
                    if (binding.ratingReasonDetail.visibility == View.VISIBLE)
                        binding.ratingReasonDetail.visibility = View.GONE
                }
                if (it.runtimeInSeconds != null) {
                    binding.durationDetail.text = (it.runtimeInSeconds!! / 60).toString() + " minutes"
                } else {
                    binding.durationDetail.text = "TBA"
                }
                if (it.plot != null) {
                    if (binding.tvPlot.visibility == View.GONE)
                        binding.tvPlot.visibility = View.VISIBLE
                } else {
                    if (binding.tvPlot.visibility == View.VISIBLE)
                        binding.tvPlot.visibility = View.GONE
                }
                if (it.releaseYear != null && it.releaseMonth != null && it.releaseDay != null) {

                    val monthString = DateFormatSymbols(Locale.US).months[it.releaseMonth!! - 1]
                    val dateString = it.releaseDay.toString()+ ' ' + monthString + ' ' + it.releaseYear

                    binding.tvReleaseDate.text = SpannableStringBuilder()
                        .bold { append(
                            CinemaLinkApplication.mInstance.applicationContext.resources.getString(
                                R.string.release_date, dateString
                            ))
                        }
                } else {
                    binding.tvReleaseDate.text = CinemaLinkApplication
                        .mInstance.applicationContext.resources.getString(
                        R.string.no_release_date)
                }
                if (it.crew != null) {
                    if (it.crew?.filter { crew -> crew.category == "Director" ||
                                crew.category == "Directors" }?.isNullOrEmpty() == true) {
                        binding.tvDirected.visibility = View.GONE
                    } else {
                        binding.tvDirected.visibility = View.VISIBLE
                    }
                    if (it.crew?.filter { crew -> crew.category == "Writer" ||
                                crew.category == "Writers" }?.isNullOrEmpty() == true) {
                        binding.tvWritten.visibility = View.GONE
                    } else {
                        binding.tvWritten.visibility = View.VISIBLE
                    }
                    if (it.crew?.filter { crew -> crew.category == "Stars"
                        }?.isNullOrEmpty() == true) {
                        binding.tvStarring.visibility = View.GONE
                    } else {
                        binding.tvStarring.visibility = View.VISIBLE
                    }
                }
                if (it.genres != null) {
                    binding.tvGenres.visibility = View.VISIBLE
                } else {
                    binding.tvGenres.visibility = View.GONE
                }
                if (it.tagline != null) {
                    binding.tvTagline.visibility = View.VISIBLE
                } else {
                    binding.tvTagline.visibility = View.GONE
                }
                if (it.userReview != null) {
                    binding.clUserReview.visibility = View.VISIBLE
                    binding.fabAddReview.visibility = View.GONE
                    binding.tvUserReviewCreated.text = DateUtils.getRelativeTimeSpanString(it.userReview?.reviewTime!!,
                        Calendar.getInstance().timeInMillis, DateUtils.MINUTE_IN_MILLIS)
                    if (it.userReview?.recommended!!) {
                            binding.tvUserRecommendedReview.setText(
                                Html.fromHtml(
                                CinemaLinkApplication.mInstance.applicationContext.resources.getString(R.string.would_recommend),
                                Html.FROM_HTML_MODE_LEGACY))
                    } else {
                            binding.tvUserRecommendedReview.setText(
                                Html.fromHtml(
                                CinemaLinkApplication.mInstance.applicationContext.resources.getString(R.string.would_not_recommend),
                                Html.FROM_HTML_MODE_LEGACY))
                    }
                } else {
                    binding.clUserReview.visibility = View.GONE
                    binding.fabAddReview.visibility = View.VISIBLE
                }
                if (binding.loadingMoviePanel.visibility == View.VISIBLE) {
                    binding.loadingMoviePanel.visibility = View.GONE
                }
                if (binding.constraintLayout.visibility == View.GONE) {
                    binding.constraintLayout.visibility = View.VISIBLE
                }
            }
            it?.let {
                (activity as AppCompatActivity).supportActionBar?.title = it.title
                if (it.tagline != null) {
                    it.tagline = it.tagline!!.uppercase()
                }
                binding.moviesh = it
                binding.tvGenres.text = SpannableStringBuilder()
                    .bold { append("Genres: ")}
                    .append(it.genres?.joinToString(separator = ", "))
                binding.tvDirected.text = SpannableStringBuilder()
                    .bold { append("Directed by ") }
                    .append(it.crew?.filter {
                            crew -> crew.category == "Director" || crew.category == "Directors"
                    }?.map { crew -> crew.name }?.joinToString(separator = " & "))
                binding.tvWritten.text = SpannableStringBuilder()
                    .bold { append("Written by ") }
                    .append(it.crew?.filter {
                        crew -> crew.category == "Writer" || crew.category == "Writers"
                    }?.map { crew -> crew.name }?.joinToString(separator = " & "))
                binding.tvStarring.text = SpannableStringBuilder()
                    .bold { append("Starring ")}
                    .append(it.crew?.filter {
                        crew ->
                        crew.category == "Stars"
                    }?.map { crew -> crew.name }?.joinToString(separator = ", "))
            }
        }
        movieViewModel.eventNetworkError.observe(viewLifecycleOwner) {
            it?.let {
                val errorBuilder = AlertDialog.Builder(activity, R.style.MyDialogTheme)
                errorBuilder.setTitle("ERROR")
                errorBuilder.setMessage(it)
                errorBuilder.setPositiveButton("OK") { _, _ -> }
                errorBuilder.show()
                movieViewModel.showErrorDone()
            }
        }

        binding.ibAddToWatchlist.setOnClickListener {
            showAddToWatchlistDialog()
        }

        binding.ibAddToCollection.setOnClickListener {
            showAddToCollectionDialog()
        }

        binding.rateButton.setOnClickListener {
            if (binding.rbMovie.rating != 0f) {
                movieViewModel.rateMovie(
                    RateMovieDto(movieViewModel.movie.value?.id!!,
                    binding.rbMovie.rating.toDouble())
                )
            }
        }
        binding.ibRemoveRating.setOnClickListener {
            movieViewModel.unrateMovie(movieViewModel.movie.value?.id!!)
        }

        binding.availableOnNetflixButton.setOnClickListener {
            showPopup(it)
        }
        binding.availableOnHboButton.setOnClickListener {
            showPopup(it)
        }
        binding.availableOnDisneyButton.setOnClickListener {
            showPopup(it)
        }
        binding.availableOnAmazonButton.setOnClickListener {
            showPopup(it)
        }
        binding.ibEditUserReview.setOnClickListener {
            displayEditReviewAlertDialog(movieViewModel.movie.value?.userReview?.id!!,
                movieViewModel.movie.value?.userReview?.content!!)
        }
        binding.ibDeleteUserReview.setOnClickListener {
            displayAreYouSureDeleteReviewAlertDialog(movieViewModel.movie.value?.userReview?.id!!)
        }
        binding.commentUserReviewButton.setOnClickListener {
            findNavController().navigate(
                MovieFragmentDirections.actionMovieFragmentToReviewFragment(
                    movieViewModel.movie.value?.userReview?.id!!)
            )
        }
        binding.fabAddReview.setOnClickListener {
            showAddReviewDialog()
        }
        binding.ibMovieReviews.setOnClickListener {
            findNavController().navigate(
                MovieFragmentDirections.actionMovieFragmentToReviewsFragment(
                    movieViewModel.movie.value?.id!!)
            )
        }

        return binding.root
    }

    private fun showPopup(v: View) {
        lateinit var platform: ReportType
        when (v.id) {
            R.id.available_on_netflix_button -> platform = ReportType.NETFLIX
            R.id.available_on_hbo_button -> platform = ReportType.HBO_MAX
            R.id.available_on_disney_button -> platform = ReportType.DISNEY_PLUS
            R.id.available_on_amazon_button -> platform = ReportType.AMAZON_PRIME
        }
        val popupMenu = PopupMenu(context, v)
        popupMenu.menuInflater.inflate(R.menu.availability_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_available -> {
                    movieViewModel.reportAvailability(
                        AvailabilityReportDto(
                        movieViewModel.movie.value?.id, platform, true)
                    )

                    Toast.makeText(context, "You clicked available on $platform",
                        Toast.LENGTH_SHORT).show()
                }
                R.id.menu_unavailable -> {
                    movieViewModel.reportAvailability(
                        AvailabilityReportDto(
                        movieViewModel.movie.value?.id, platform, false)
                    )

                    Toast.makeText(context, "You clicked unavailable on $platform",
                        Toast.LENGTH_SHORT).show()
                }
            }
            true
        }
        popupMenu.show()
    }

    private fun showAddToCollectionDialog() {
        val selectedItems = ArrayList<Int>() // Where we track the selected items
        val builder = AlertDialog.Builder(activity, R.style.MyDialogTheme)
        val listString = movieViewModel.movie.value?.ownedStatuses?.map { it.statusName }?.toTypedArray()
        val checked = movieViewModel.movie.value?.ownedStatuses?.map { it.owned }?.toBooleanArray()
        for (i in checked?.indices!!) {
            if (checked[i]) {
                selectedItems.add(i)
            }
        }
        builder.setTitle("Add to collection")
            // Specify the list array, the items to be selected by default (null for none),
            // and the listener through which to receive callbacks when items are selected
            .setMultiChoiceItems(listString, checked)
            { _, which, isChecked ->
                if (isChecked) {
                    // If the user checked the item, add it to the selected items
                    selectedItems.add(which)
                } else if (selectedItems.contains(which)) {
                    // Else, if the item is already in the array, remove it
                    selectedItems.remove(which)
                }
            }
            // Set the action buttons
            .setPositiveButton("add")
            { _, _ ->
                val ownedStatuses = mutableListOf<OwnedStatus>()
                selectedItems.forEach{el -> ownedStatuses.add(OwnedStatus.values()[el])}
                if (checked.isEmpty() && ownedStatuses.isEmpty()) {
                    Toast.makeText(
                        CinemaLinkApplication.mInstance.applicationContext,
                        "No data to add", Toast.LENGTH_SHORT
                    ).show()
                } else {
                    movieViewModel.updateMovieForCollection(movieViewModel.movie.value?.id!!,
                        ownedStatuses)
                    Toast.makeText(
                        CinemaLinkApplication.mInstance.applicationContext,
                        "Updated collection data", Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .setNegativeButton("cancel"
            ) { _, _ ->
                Toast.makeText(
                    CinemaLinkApplication.mInstance.applicationContext,
                    "Cancelled", Toast.LENGTH_SHORT
                ).show()
            }
            .show()
    }

    private fun showAddToWatchlistDialog() {
        val builder = AlertDialog.Builder(activity)
        val dialogBinding: DialogWatchlistCustomBinding = DataBindingUtil.inflate(LayoutInflater.from(context),
            R.layout.dialog_watchlist_custom, null, false)
        val addToWatchlistAdapter = WatchlistChoiceAdapter(movieViewModel)
        dialogBinding.rvWatchlistsDialog.apply {
            addItemDecoration(SimpleDividerItemDecoration(context, R.drawable.line_divider))
            adapter = addToWatchlistAdapter
        }
        addToWatchlistAdapter.submitList(movieViewModel.movie.value?.watchlists)
        builder.setView(dialogBinding.root)

        movieViewModel.watchlist.observe(viewLifecycleOwner) {
            it?.let{
                addToWatchlistAdapter.submitList(it)
            }
        }

        val dialog = builder.create()
        dialog.show()

        val addWatchlistBuilder = AlertDialog.Builder(activity)
        val addWatchlistDialogBinding: DialogNewWatchlistCustomBinding =
            DataBindingUtil.inflate(LayoutInflater.from(context),
                R.layout.dialog_new_watchlist_custom, null, false)
        addWatchlistBuilder.setView(addWatchlistDialogBinding.root)

        val dialogNewWatchlist = addWatchlistBuilder.create()

        dialogBinding.createWatchlistDialogButton.setOnClickListener {
            dialog.dismiss()
            dialogNewWatchlist.show()

            addWatchlistDialogBinding.createNewWatchlistCustomDialog.setOnClickListener {
                if (addWatchlistDialogBinding.etNewWatchlistNameDialog.text.toString().isBlank()) {
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
                    movieViewModel.createWatchlist(addWatchlistDialogBinding.etNewWatchlistNameDialog.text.toString())
                    dialog.show()
                    dialogNewWatchlist.dismiss()
                }
            }
            addWatchlistDialogBinding.cancelNewWatchlistButtonCustomDialog.setOnClickListener {
                dialog.show()
                dialogNewWatchlist.dismiss()
            }
        }
        dialogBinding.cancelButtonCustomDialog.setOnClickListener {
            dialog.dismiss()
        }
        dialogBinding.addButtonCustomDialog.setOnClickListener {
            if (movieViewModel.selectedWatchList.value != null) {
                movieViewModel.addMovieToWatchlist(AddMovieToWatchlistDto(movieViewModel.movie.value?.id!!,
                movieViewModel.selectedWatchList.value!!, dialogBinding.etNoteMovieDialog.text.toString()))
                dialog.dismiss()
            } else {
                Toast.makeText(
                    CinemaLinkApplication.mInstance.applicationContext,
                    "No Watchlist selected", Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    private fun showAddReviewDialog() {
        val builder = AlertDialog.Builder(activity)
        val reviewDialogBinding: DialogAddReviewBinding = DataBindingUtil.inflate(LayoutInflater.from(context),
            R.layout.dialog_add_review, null, false)

        builder.setView(reviewDialogBinding.root)

        val dialog = builder.create()
        dialog.show()
        var checked: Boolean? = null

        reviewDialogBinding.addReviewRecommendButton.setOnClickListener {
            if (checked == null) {
                checked = true
                reviewDialogBinding.addReviewRecommendButton.setImageResource(R.drawable.ic_check_green_24)
            } else if (checked == false) {
                checked = true
                reviewDialogBinding.addReviewNotRecommendButton.setImageResource(R.drawable.ic_close_24)
                reviewDialogBinding.addReviewRecommendButton.setImageResource(R.drawable.ic_check_green_24)
            } else {
                checked = null
                reviewDialogBinding.addReviewRecommendButton.setImageResource(R.drawable.ic_check_24)
            }
        }
        reviewDialogBinding.addReviewNotRecommendButton.setOnClickListener {
            if (checked == null) {
                checked = false
                reviewDialogBinding.addReviewNotRecommendButton.setImageResource(R.drawable.ic_close_red_24)
            } else if (checked == true) {
                checked = false
                reviewDialogBinding.addReviewRecommendButton.setImageResource(R.drawable.ic_check_24)
                reviewDialogBinding.addReviewNotRecommendButton.setImageResource(R.drawable.ic_close_red_24)
            } else {
                checked = null
                reviewDialogBinding.addReviewNotRecommendButton.setImageResource(R.drawable.ic_close_24)
            }
        }
        reviewDialogBinding.cancelReviewButtonDialog.setOnClickListener {
            dialog.dismiss()
        }
        reviewDialogBinding.addReviewButtonDialog.setOnClickListener {
            if (checked != null) {

                if (reviewDialogBinding.etReviewDialog.text.toString().isBlank()) {
                    reviewDialogBinding.etReviewDialog.error = "Review is required!"
                } else if (reviewDialogBinding.etReviewDialog.text.toString().length > 4000) {
                    Toast.makeText(
                        CinemaLinkApplication.mInstance.applicationContext,
                        "The review is too long.", Toast.LENGTH_SHORT
                    ).show()
                } else {
                    movieViewModel.addReview(
                        AddReviewDto(
                            movieViewModel.movie.value?.id!!, checked!!,
                            reviewDialogBinding.etReviewDialog.text.toString()
                        )
                    )
                    dialog.dismiss()
                }
            } else {
                Toast.makeText(
                    CinemaLinkApplication.mInstance.applicationContext,
                    "Please choose a recommendation! " + ("\ud83d\ude07"), Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun displayEditReviewAlertDialog(reviewId: Long, content: String) {
        val builder = AlertDialog.Builder(activity)
        val dialogEditCustomBinding: DialogEditReviewCustomBinding =
            DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_edit_review_custom,
                null, false)
        dialogEditCustomBinding.etEditReviewDialog.setText(content)
        builder.setCancelable(false)
        builder.setView(dialogEditCustomBinding.root)

        val dialog = builder.create()
        dialog.show()

        dialogEditCustomBinding.cancelEditReviewDialog.setOnClickListener {
            dialog.dismiss()
        }
        dialogEditCustomBinding.buttonEditReviewDialog.setOnClickListener {
            if (dialogEditCustomBinding.etEditReviewDialog.text.toString().isBlank()) {
                dialogEditCustomBinding.etEditReviewDialog.error =
                    "A review content is required, otherwise delete!"
            } else if (dialogEditCustomBinding.etEditReviewDialog.text.toString().length > 4000) {
                Toast.makeText(
                    CinemaLinkApplication.mInstance.applicationContext,
                    "The review is too long.", Toast.LENGTH_SHORT
                ).show()
            } else {
                movieViewModel.editReviewReq(
                    reviewId, EditDto(dialogEditCustomBinding
                        .etEditReviewDialog.text.toString().trim())
                )
                dialog.dismiss()
                Toast.makeText(
                    CinemaLinkApplication.mInstance.applicationContext,
                    "Review edited.", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun displayAreYouSureDeleteReviewAlertDialog(reviewId: Long) {
        val builder = AlertDialog.Builder(activity, R.style.MyDialogTheme)
        builder.setMessage("Are you sure you want to delete this review?")
            .setPositiveButton("YES") { _, _ ->
                run {
                    movieViewModel.deleteReviewReq(reviewId)
                }
            }
            .setNegativeButton("CANCEL") { _, _ ->  }
            .show()
    }

}