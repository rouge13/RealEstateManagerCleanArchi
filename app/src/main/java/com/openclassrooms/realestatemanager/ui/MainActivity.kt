package com.openclassrooms.realestatemanager.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.di.ViewModelFactory
import com.openclassrooms.realestatemanager.data.notification.NotificationHelper
import com.openclassrooms.realestatemanager.databinding.ActivityMainBinding
import com.openclassrooms.realestatemanager.databinding.ActivityMainNavHeaderBinding
import com.openclassrooms.realestatemanager.ui.alertDialog.SettingsCurrencyDateAlertDialog
import com.openclassrooms.realestatemanager.ui.sharedViewModel.SharedAgentViewModel
import com.openclassrooms.realestatemanager.ui.sharedViewModel.SharedNavigationViewModel
import com.openclassrooms.realestatemanager.ui.sharedViewModel.SharedPropertyViewModel
import com.openclassrooms.realestatemanager.ui.sharedViewModel.SharedUtilsViewModel
import com.openclassrooms.realestatemanager.ui.utils.Utils
import com.openclassrooms.realestatemanager.ui.viewmodel.InitializationViewModel
import kotlinx.coroutines.launch

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var activityMainBinding: ActivityMainBinding
    private lateinit var activityMainNavHeaderBinding: ActivityMainNavHeaderBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var sharedAgentViewModel: SharedAgentViewModel
    private lateinit var initializationViewModel: InitializationViewModel
    private lateinit var sharedNavigationViewModel: SharedNavigationViewModel
    private lateinit var sharedPropertyViewModel: SharedPropertyViewModel
    private lateinit var sharedUtilsViewModel: SharedUtilsViewModel

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)
        // Init the ViewModel
        initViewModels()
        drawerLayout = activityMainBinding.activityMainDrawerLayout
        navigationView = activityMainBinding.activityMainNavView
        // Set up the drawer toggle
        setupDrawerButton()
        // Get the header view and bind it to the activityMainNavHeaderBinding
        activityMainNavHeaderBinding =
            ActivityMainNavHeaderBinding.bind(navigationView.getHeaderView(0))
        // Initialize the app using the InitializationViewModel
        initializationViewModel.startInitialization(application as MainApplication)
        setDefaultValueOfDateFormat()
        sharedUtilsViewModel.setActiveSelectionMoneyRate(false)
        // Set up the NavController and connect it to the NavigationView
        setupNavigationController()
        checkHasInternet()
        checkHasPermissionToSendNotifications()
        checkIfWriteExternalStoragePermissionIsGranted()
        initAddOnClickListeners()
        initModifyOnClickListeners()
        initSearchOnClickListeners()
    }

    private fun checkIfWriteExternalStoragePermissionIsGranted() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            // Check if the current Android version is Marshmallow (6.0) or higher, but below Android Q (10.0)
            // Request the WRITE_EXTERNAL_STORAGE permission if not granted
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    PERMISSION_REQUEST_CODE
                )
            }
        }
    }

    private fun initModifyOnClickListeners() {
        sharedPropertyViewModel.getSelectedProperty.observe(this) { property ->
            activityMainBinding.btnModify.setOnClickListener {
                // Check if a property is selected for showing the AddOrModifyPropertyFragment
                if (property != null) {
                    if (navController.currentDestination?.id != R.id.addAndModifyPropertyFragment) {
                        navController.navigate(R.id.addAndModifyPropertyFragment)
                        sharedNavigationViewModel.setAddOrModifyClicked(true)
                    } else {
                        navController.popBackStack() // Go back to the previous fragment
                    }
                }
            }
        }
        activityMainBinding.btnModify.setOnClickListener {
            if (sharedPropertyViewModel.getSelectedProperty.value == null) {
                Toast.makeText(
                    this,
                    getString(R.string.no_property_selected),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun initAddOnClickListeners() {
        activityMainBinding.btnAdd.setOnClickListener {
            if (navController.currentDestination?.id != R.id.addAndModifyPropertyFragment) {
                navController.navigate(R.id.addAndModifyPropertyFragment)
                sharedNavigationViewModel.setAddOrModifyClicked(false)
            } else {
                navController.popBackStack() // Go back to the previous fragment
            }
        }
    }

    // Init all the ViewModels used in the MainActivity and the fragments attached to it (SharedViewModels) using the ViewModelFactory class (dependency injection) and the MainApplication class
    private fun initViewModels() {
        initializationViewModel = ViewModelProvider(
            this,
            ViewModelFactory(application as MainApplication)
        )[InitializationViewModel::class.java]
        sharedPropertyViewModel = ViewModelProvider(
            this,
            ViewModelFactory(application as MainApplication)
        )[SharedPropertyViewModel::class.java]
        sharedNavigationViewModel = ViewModelProvider(
            this,
            ViewModelFactory(application as MainApplication)
        )[SharedNavigationViewModel::class.java]
        sharedAgentViewModel = ViewModelProvider(
            this,
            ViewModelFactory(application as MainApplication)
        )[SharedAgentViewModel::class.java]
        sharedUtilsViewModel = ViewModelProvider(
            this,
            ViewModelFactory(application as MainApplication)
        )[SharedUtilsViewModel::class.java]
    }

    // Set default format of date to USA format (yyyy/MM/dd)
    private fun setDefaultValueOfDateFormat() {
        sharedUtilsViewModel.setDateFormatSelected(Utils.todayDateUsaFormat)
    }

    // Init the search button onClickListener to navigate to the SearchFragment or go back to the previous fragment if the user is already on the SearchFragment
    private fun initSearchOnClickListeners() {
        activityMainBinding.btnSearch.setOnClickListener {
            if (navController.currentDestination?.id != R.id.searchFragment) {
                navController.navigate(R.id.searchFragment)
            } else {
                navController.popBackStack() // Go back to the previous fragment
            }
        }
    }

    // Setup the navigation controller and connect it to the NavigationView
    private fun setupNavigationController() {
        // Init the NavController
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        navigationView.setupWithNavController(navController)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            handleNavigationItemSelected(menuItem, navController)
        }
    }

    // Handle the navigation item selected in the NavigationView (drawer) and navigate to the corresponding fragment or show the corresponding dialog
    private fun handleNavigationItemSelected(item: MenuItem, navController: NavController): Boolean {
        when (item.itemId) {
            R.id.nav_settings_utils -> {
                lifecycleScope.launch {
                    // Show the Currency and date dialog settings
                    SettingsCurrencyDateAlertDialog(context = this@MainActivity, sharedUtilsViewModel = sharedUtilsViewModel).showSettings()
                    drawerLayout.closeDrawer(GravityCompat.START) // Close the drawer
                }
                return true
            }

            else -> {
                // Let the NavController handle the other menu items
                val handled = item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
                if (handled) {
                    // Close the drawer if an item is handled
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
                return handled
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkHasInternet() {
        sharedAgentViewModel.agentHasInternet().observe(this, Observer {
            if (it == true) {
                // Navigation has internet access
                sharedNavigationViewModel.setOnlineNavigation(true)
                val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    val builder = AlertDialog.Builder(this)
                    builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                        .setCancelable(false)
                        .setPositiveButton("Yes") { _, _ -> startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }
                        .setNegativeButton("No") { dialog, _ -> dialog.cancel() }
                    val alert = builder.create()
                    alert.show()
                } else {
                    // If the agent has internet access and the GPS is enabled, start the agent location updates to show on the map the agent's location
                    agentLocationUpdates()
                }
                // Setup the bottom navigation view and set the current item to the PropertyListFragment (first fragment) if the agent has internet access
                setupBottomNavigation()
                activityMainBinding.viewPager.currentItem = 0 // Switch to the PropertyListFragment
            } else {
                // Navigation has no internet access show a toast and hide the bottom navigation view if the agent has no internet access
                Toast.makeText(this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show()
                activityMainBinding.bottomNavigationView.visibility = View.GONE
            }
        })
    }

    // Check permission to send notifications
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkHasPermissionToSendNotifications() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestSinglePermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    // Check permission to access the user's location for the agent location updates
    private fun agentLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            requestLocationUpdates()
        } else {
            requestSinglePermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // Request permission to access the user's location for the agent location updates
    private val requestSinglePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                requestLocationUpdates()
            } else {
                Toast.makeText(this, getString(R.string.gps_enavailable), Toast.LENGTH_LONG).show()
            }
        }

    // Start the agent location updates to show on the map the agent's location using the sharedAgentViewModel
    private fun requestLocationUpdates() {
        sharedAgentViewModel.startLocationUpdates()
    }

    // Setup the bottom navigation view and set the current item to the PropertyListFragment (first fragment) if the agent has internet access
    private fun setupBottomNavigation() {
        activityMainBinding.bottomNavigationView.visibility = View.VISIBLE
        activityMainBinding.bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.list_fragment -> {
                    navController.popBackStack(R.id.propertyListFragment, false)
                    navController.navigate(R.id.propertyListFragment)
                    true
                }

                R.id.map_fragment -> {
                    navController.popBackStack(R.id.mapFragment, false)
                    navController.navigate(R.id.mapFragment)
                    true
                }

                else -> false
            }
        }
    }

    // Override the create options menu to inflate the menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_main_agent_info_menu, menu)
        return true
    }

    // Override the on options item selected to handle the menu item selected
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    drawerLayout.openDrawer(GravityCompat.START)
                    invalidateOptionsMenu()
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // Setup the drawer button to open and close the drawer layout and sync the state of the drawer layout with the drawer button
    private fun setupDrawerButton() {
        val actionBarDrawerToggle = ActionBarDrawerToggle(this, drawerLayout, activityMainBinding.activityMainToolbar, R.string.drawer_open, R.string.drawer_close)
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
    }

    // Override the on back pressed to close the drawer layout if it is open
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(navigationView)) {
            drawerLayout.closeDrawer(navigationView)
        } else {
            super.onBackPressed()
        }
    }

    // Override the dispatch touch event to hide the keyboard if it is open when the user touches outside the keyboard
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }

    // Override the on request permissions result to handle the permission result to send notifications
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, show the notification
                val notificationHelper = NotificationHelper(this)
                notificationHelper.showPropertyInsertedNotification()
            } else {
                // Permission denied, handle it accordingly (e.g., show a message)
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        const val PERMISSION_REQUEST_CODE = 1
    }
}