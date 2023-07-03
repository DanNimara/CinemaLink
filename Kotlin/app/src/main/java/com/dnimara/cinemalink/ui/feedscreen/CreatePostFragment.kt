package com.dnimara.cinemalink.ui.feedscreen

import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.ImageSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import androidx.navigation.fragment.findNavController
import com.dnimara.cinemalink.R
import com.dnimara.cinemalink.databinding.FragmentCreatePostBinding
import com.dnimara.cinemalink.utils.SessionManager
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.textfield.TextInputEditText
import java.util.*

class CreatePostFragment : Fragment() {

    lateinit var binding: FragmentCreatePostBinding
    private lateinit var createPostViewModel: CreatePostViewModel
    private var searchTerm = ""
    private var spannedLength = 0
    private var toast: Toast? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        createPostViewModel = ViewModelProvider(this).get(CreatePostViewModel::class.java)

        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_create_post, container, false)

        val adapter = TagMovieAdapter(TagMovieAdapter.TagMovieListener{
            createPostViewModel.addTag(it)
        })
        binding.tagMovieList.apply {
            this.adapter = adapter
        }

        binding.etTagMovies.setOnFocusChangeListener {
            _, hasFocus -> run {
                if (hasFocus) {
                    this.activity?.runOnUiThread {
                        if (binding.flMovieTag.visibility == View.GONE) {
                            binding.flMovieTag.visibility = View.VISIBLE
                            createPostViewModel.search("")
                        }
                    }
                } else {
                    this.activity?.runOnUiThread {
                        if (binding.flMovieTag.visibility == View.VISIBLE) {
                            binding.flMovieTag.visibility = View.GONE
                        }
                    }
                }
            }
        }

        createPostViewModel.searchMovies.observe(viewLifecycleOwner) {

            it?.let {
                adapter.submitList(it)
                binding.tagMovieList.postDelayed({
                    binding.tagMovieList.scrollToPosition(0)
                }, 250)
            }
        }

        binding.createPostButton.setOnClickListener {
            if (binding.etPostContent.text.isNullOrBlank()) {
                toast?.cancel()
                toast = Toast.makeText(context, "There is no text for this post.", Toast.LENGTH_SHORT)
                toast?.show()
            } else if (binding.etPostContent.text.toString().length > 2000) {
                toast?.cancel()
                toast = Toast.makeText(context, "Post is too long.", Toast.LENGTH_SHORT)
                toast?.show()
            }
            else {
                createPostViewModel.createPost(
                    SessionManager.mInstance.getToken()!!,
                    binding.etPostContent.text.toString()
                )
            }
        }

        createPostViewModel.showToastTagLimit.observe(viewLifecycleOwner) {
            if (it == true) {
                toast?.cancel()
                toast = Toast.makeText(context, "You can tag only up to 4 movies in a post", Toast.LENGTH_SHORT)
                toast?.show()
                createPostViewModel.showToastTagLimitComplete()
            }
        }

        createPostViewModel.getFeedData.observe(viewLifecycleOwner) {
            if (it == true) {
                findNavController().navigate(
                    CreatePostFragmentDirections.actionCreatePostFragmentToFeedFragment()
                )
                createPostViewModel.getFeedDataComplete()
            }
        }
        createPostViewModel.tagMovies.observe(viewLifecycleOwner) {
            it?.let {
                val movieTitlesLength = it.map {movie -> movie.title}.joinToString(separator = "").length
                if (movieTitlesLength > spannedLength) {
                    val movie = it[it.size - 1]
                    val chip =
                        ChipDrawable.createFromResource(requireContext(), R.xml.standalone_chip)
                    chip.text = movie.title
                    chip.setBounds(0, 0, chip.intrinsicWidth, chip.intrinsicHeight)
                    val span = ImageSpan(chip)
                    if (binding.etTagMovies.text?.length!! == 0) {
                        binding.etTagMovies.setText(movie.title)
                    } else if (binding.etTagMovies.text?.length!! > 0) {
                        if (searchTerm == "") {
                            binding.etTagMovies.text!!.replace(
                                spannedLength,
                                spannedLength, movie.title
                            )
                        } else {
                            binding.etTagMovies.text!!.replace(
                                spannedLength,
                                spannedLength + searchTerm.length, movie.title
                            )
                        }

                    }
                    binding.etTagMovies.text?.setSpan(
                        span,
                        spannedLength,
                        spannedLength + movie.title.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    if (searchTerm != "") {
                        binding.etTagMovies.text = binding.etTagMovies.text!!.delete(
                            binding.etTagMovies.text!!.length - 1, binding.etTagMovies.text!!.length
                        )
                        searchTerm = ""
                    }
                    if (binding.etTagMovies.text?.length != null && binding.etTagMovies.text?.length != 0) {
                        var strLength = binding.etTagMovies.text?.length!!
                        var str = binding.etTagMovies.text.toString()
                        while (str[strLength - 1]  == ' ') {
                            binding.etTagMovies.text = binding.etTagMovies.text!!.delete(
                                binding.etTagMovies.text!!.length - 1, binding.etTagMovies.text!!.length)
                            strLength = binding.etTagMovies.text?.length!!
                            str = binding.etTagMovies.text.toString()
                        }
                    }
                    binding.etTagMovies.text = binding.etTagMovies.text!!.append(" ")
                    binding.etTagMovies.requestFocus()
                    binding.etTagMovies.placeCursorToEnd()

                    spannedLength += movie.title.length

                } else {
                    spannedLength = movieTitlesLength
                }
            }
        }

        val textWatcherSearch = object : TextWatcher {
            private var timer: Timer = Timer()
            private val DELAY: Long = 1000 // Milliseconds

            override fun afterTextChanged(s: Editable?) {
                timer.cancel()
                timer = Timer()
                timer.schedule(
                    object : TimerTask() {
                        override fun run() {
                            if (s?.length!! >= spannedLength) {
                                val searchText = s.substring(spannedLength).trim() ?: ""
                                if (searchText == searchTerm) return

                                searchTerm = searchText
                                createPostViewModel.search(searchTerm)
                            } else {
                                val movieTitlesLength = createPostViewModel.tagMovies.value?.map {movie -> movie.title}?.joinToString(separator = "")?.length
                                if (movieTitlesLength != null) {
                                    if (movieTitlesLength > 0 && s.length < spannedLength) {
                                        val length = createPostViewModel.removeTags(s.length)
                                        spannedLength = length
                                    } else if (movieTitlesLength == 0){
                                        spannedLength = 0
                                    }
                                } else {
                                    spannedLength = 0
                                }
                            }
                        }
                    },
                    DELAY
                )
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
        }

        binding.etTagMovies.addTextChangedListener(textWatcherSearch)

        binding.lifecycleOwner = this.viewLifecycleOwner

        return binding.root
    }

    fun TextInputEditText.placeCursorToEnd() {
        this.setSelection(this.text?.length!!)
    }

}