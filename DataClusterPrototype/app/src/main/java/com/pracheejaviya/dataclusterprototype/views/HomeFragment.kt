package com.pracheejaviya.dataclusterprototype.views

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.pracheejaviya.dataclusterprototype.R
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as HomeActivity).drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        navbarMenu.setOnClickListener {
            if (activity is HomeActivity) {
                (activity as Activity).drawerLayout.openDrawer(GravityCompat.END)
            }
        }

        btnCamera.setOnClickListener {(setupFragment(NewReadingFragment.newInstance()))}
        btnSignOut.setOnClickListener {
            val intent =  Intent(context, SigninActivity::class.java)
            startActivity(intent)
        }

    }

    private fun setupFragment(fragment: Fragment) {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.newHomeContainer, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
    companion object {
        fun newInstance() = HomeFragment()
    }


}