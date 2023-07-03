package com.dnimara.cinemalink.ui.loginscreen

import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.opengl.Visibility
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.*
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.auth0.android.jwt.JWT
import com.dnimara.cinemalink.MainActivity
import com.dnimara.cinemalink.R
import com.dnimara.cinemalink.application.AppLogoLink
import com.dnimara.cinemalink.application.CinemaLinkApplication
import com.dnimara.cinemalink.database.CinemaLinkDatabase
import com.dnimara.cinemalink.databinding.FragmentLoginBinding
import com.dnimara.cinemalink.utils.SessionManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class LoginFragment : Fragment() {

    lateinit var binding: FragmentLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_login, container, false)
        binding.applogo= AppLogoLink()

        binding.loginButton.setOnClickListener {
            val username = binding.loginUsername.text
            val password = binding.loginPassword.text
            if(username.isNullOrBlank() || password.isNullOrBlank()) {
                binding.loginUsername.error = if(username.isNullOrBlank())
                    "Email is required!" else null
                binding.loginPassword.error = if(password.isNullOrBlank())
                    "Password is required!" else null
            } else if (!isValidEmail(username.toString())) {
                binding.loginUsername.error = "Email is not valid!"
            } else {
                viewModel.login(
                    username.toString(),
                    password.toString()
                )
            }
        }

        val navBar: BottomNavigationView? = activity?.findViewById(R.id.nav_view)
        navBar?.visibility=View.GONE

        binding.registerButton.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment())
        }

        viewModel.eventNetworkError.observe(viewLifecycleOwner) {
            it?.let {
                val errorBuilder = AlertDialog.Builder(activity, R.style.MyDialogTheme)
                errorBuilder.setTitle("ERROR")
                errorBuilder.setMessage(it)
                errorBuilder.setPositiveButton("OK") { _, _ -> }
                errorBuilder.show()
                viewModel.showErrorDone()
            }
        }

        viewModel.loggedIn.observe(viewLifecycleOwner) { loggedIn ->
            if (loggedIn) {
                val application = CinemaLinkApplication.mInstance
                val dataSource = CinemaLinkDatabase.getDatabase(application)
                dataSource.postDao.clear()
                dataSource.postDao.clearPostsMovies()
                dataSource.watchlistDao.clear()
                dataSource.watchlistDao.clearWatchlistMovies()
                SessionManager.mInstance.setToken("Bearer " + viewModel.token.value?.accessToken!!)
                val intent = Intent(context, MainActivity::class.java)
                startActivity(intent)
                viewModel.onNotLoggedIn()
            }
        }

        return binding.root
    }

    private fun isValidEmail(target: CharSequence): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

}