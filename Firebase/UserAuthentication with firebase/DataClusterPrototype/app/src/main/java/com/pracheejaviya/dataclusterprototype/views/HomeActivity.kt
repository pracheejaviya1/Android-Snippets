package com.pracheejaviya.dataclusterprototype.views

import android.os.Bundle
import android.text.Layout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.pracheejaviya.dataclusterprototype.R
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_home.*

class HomeActivity : AppCompatActivity() {
    lateinit var currentFragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setUpFragment()


    }

    private fun setUpFragment() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.newHomeContainer, HomeFragment.newInstance())
        fragmentTransaction.commit()
        currentFragment = Fragment()
    }
//    private fun setUpDrawer() {
//        navigationHome.setOnClickListener {
//            drawerLayout.closeDrawer(GravityCompat.END)
//          //  changeFragment(fragment = AdvisoryBoard.newInstance(), addToBackStack = true)
////            bottomNav.selectedItemId = R.id.navigationHome
//        }
//
//        navigationHome.setOnClickListener {
//            drawerLayout.closeDrawer(GravityCompat.END)
//            //changeFragment(fragment = RegisterFragment.newInstance(), addToBackStack = true)
//        }

    }
