package com.pracheejaviya.dataclusterprototype.views

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

@RequiresApi(Build.VERSION_CODES.N)
class HomeActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    lateinit var currentFragment: Fragment
    var currentuserUID = FirebaseAuth.getInstance().currentUser!!.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setUpFragment()
        logV(currentuserUID + "USER IDDDDDDEEEEEEEEEEEEEEEEEEEEE TEHEHEHEHHE :D")
        init()

    }

    private fun init() {
        uploadUID(currentuserUID)
        uploadTasks()
    }


    //UID be added everytime the user logs in. Change to unique - check later
    private fun uploadUID(userID: String) {
        val id = hashMapOf(
            "USER ID" to userID
        )
        db.collection("users").document(currentuserUID)
            .set(id)
            .addOnSuccessListener { logV("SUCCESSFULLLLLLLLLLLLLLLLLLLLLLLLLLLL") }
            .addOnFailureListener { e -> Log.w("Error writing document", e) }
    }

    private fun randomNumberGenerator(): String {
        val source = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        var random = java.util.Random().ints(10, 0, source.length)
            .toArray()
            .map(source::get)
            .joinToString("")
        return random;
    }

    private fun uploadTasks() {
        val task1 = randomNumberGenerator()
        val task2 = randomNumberGenerator()

        //redundancy just for the sake of prototype
        val id = hashMapOf(
            "Title" to "Vehicles",
            "Points" to 1234
        )
        db.collection("tasks").document(task1)
            .set(id)
            .addOnSuccessListener { logV("Successful") }
            .addOnFailureListener { e -> Log.w("Error writing document", e) }

        db.collection("tasks").document(task2)
            .set(id)
            .addOnSuccessListener { logV("Successful") }
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
