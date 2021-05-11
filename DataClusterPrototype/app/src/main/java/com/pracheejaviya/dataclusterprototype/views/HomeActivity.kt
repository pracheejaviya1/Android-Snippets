package com.pracheejaviya.dataclusterprototype.views

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pracheejaviya.dataclusterprototype.R
import com.pracheejaviya.dataclusterprototype.extensions.logV
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    lateinit var currentFragment: Fragment
    var currentuserUID = FirebaseAuth.getInstance().currentUser!!.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setUpFragment()
        logV(currentuserUID + "USER IDDDDDDEEEEEEEEEEEEEEEEEEEEE")
        init()

    }

    private fun init() {
        uploadUID(currentuserUID)
    }


    //UID be added everytime the user logs in. This function will be called when a user creates an account
    private fun uploadUID(userID: String) {
        val id = hashMapOf(
            "USER ID" to userID
        )
        db.collection("Users").document(currentuserUID)
            .set(id)
            .addOnSuccessListener { logV("SUCCESSFULLLLLLLLLLLLLLLLLLLLLLLLLLLL") }
            .addOnFailureListener { e -> Log.w("Error writing document", e) }
    }



    private fun setUpFragment() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.newHomeContainer, HomeFragment.newInstance())
        fragmentTransaction.commit()
        currentFragment = Fragment()
    }



}
