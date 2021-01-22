package com.yashas.autowhatsapp.activities

import android.Manifest
import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.text.TextUtils.SimpleStringSplitter
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.yashas.autowhatsapp.R
import com.yashas.autowhatsapp.fragments.CustomReplyFragment
import com.yashas.autowhatsapp.fragments.NotificationRepliedFragment

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var coordinatorLayout: CoordinatorLayout
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var frameLayout: FrameLayout
    lateinit var navigationView: NavigationView
    private var previousMenuItem: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setup()
    }

    @SuppressLint("Recycle")
    private fun setup() {
        notificationAccessCheck()
        contactAccessCheck()
        initUI()
        setUpToolbar()
        actionBarToggle()
        listener()
        setupHome()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Contacts read permission denied", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun contactAccessCheck() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CONTACTS), 1)
        }
    }

    private fun notificationAccessCheck() {
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (!notificationManager.isNotificationPolicyAccessGranted) {
            MaterialAlertDialogBuilder(this)
                .setTitle("Access Needed")
                .setMessage("Grant notification reading access")
                .setPositiveButton("Yes") { _, _ ->
                    startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
                }
                .setNeutralButton("No") { _, _ ->
                    finishAffinity()
                }
                .show()
        }
    }

    private fun setupHome() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame, NotificationRepliedFragment())
            .commit()
        supportActionBar?.title = getString(R.string.anyStr, "Notifications")
        navigationView.setCheckedItem(R.id.home)
    }

    private fun listener() {
        navigationView.setNavigationItemSelectedListener {
            if (previousMenuItem != null) {
                it.isChecked = false
            }
            it.isCheckable = true
            it.isChecked = true
            previousMenuItem = it
            navigationView.setCheckedItem(R.id.home)
            when (it.itemId) {
                R.id.home -> {
                    setupHome()
                    drawerLayout.closeDrawers()
                }

                R.id.create -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame, CustomReplyFragment())
                        .commit()
                    supportActionBar?.title = getString(R.string.anyStr, "Custom Replies")
                    navigationView.setCheckedItem(R.id.create)
                    drawerLayout.closeDrawers()
                }

            }
            return@setNavigationItemSelectedListener true
        }
    }

    private fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.elevation = 0f
        supportActionBar?.title = getString(R.string.anyStr, "Auto WhatsApp")
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initUI() {
        drawerLayout = findViewById(R.id.drawerLayout)
        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        toolbar = findViewById(R.id.toolbar)
        frameLayout = findViewById(R.id.frame)
        navigationView = findViewById(R.id.navigationView)
    }

    private fun actionBarToggle() {
        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this@MainActivity,
            drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )
        actionBarDrawerToggle.drawerArrowDrawable.color = ResourcesCompat.getColor(
            resources,
            R.color.text_color, null
        )
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }
}