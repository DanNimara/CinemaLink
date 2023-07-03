package com.dnimara.cinemalink

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavArgument
import androidx.navigation.NavType
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.dnimara.cinemalink.application.AppLogoLink
import com.dnimara.cinemalink.databinding.ActivityMainBinding
import com.dnimara.cinemalink.databinding.NavHeaderBinding
import com.dnimara.cinemalink.ui.loginscreen.AuthActivity
import com.dnimara.cinemalink.utils.SessionManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var drawerLayout: DrawerLayout
    private lateinit var toolbar: Toolbar
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        drawerLayout = binding.drawerLayout
        val navHeaderBinding: NavHeaderBinding = DataBindingUtil.inflate(layoutInflater,
            R.layout.nav_header, binding.navDrawerView, false)
        binding.navDrawerView.addHeaderView(navHeaderBinding.root)
        navHeaderBinding.applogo = AppLogoLink()
        val navigationView: NavigationView = findViewById(R.id.nav_drawer_view)
        toolbar = findViewById(R.id.toolbar_included)
        setSupportActionBar(toolbar)

        toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_open,
            R.string.nav_close)
        toggle.isDrawerIndicatorEnabled = true
        drawerLayout.addDrawerListener(toggle)

        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        toggle.syncState()

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment)
        val profileDestination = navController.graph.findNode(R.id.profileFragment)
        profileDestination?.addArgument("userId", NavArgument.Builder()
            .setType(NavType.LongType)
            .setDefaultValue(SessionManager.mInstance.getUserId())
            .build())
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.feedFragment,
                R.id.searchMovieFragment,
                R.id.recommendationFragment,
                R.id.profileFragment
            ),
            drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navigationView.setupWithNavController(navController)
        toolbar.setNavigationOnClickListener {
            findNavController(R.id.nav_host_fragment).navigateUp(appBarConfiguration)
        }

    }

    fun logout(item: MenuItem) {
        SessionManager.mInstance.unsetToken()
        val intent = Intent(applicationContext, AuthActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        return when (item.itemId) {
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp() = findNavController(R.id.nav_host_fragment).navigateUp()
            || super.onSupportNavigateUp()

}

