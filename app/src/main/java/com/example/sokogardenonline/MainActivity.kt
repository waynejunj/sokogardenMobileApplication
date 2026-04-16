package com.example.sokogardenonline

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    lateinit var signupBtn: Button
    lateinit var signinBtn: Button
    lateinit var welcomeText: TextView
    lateinit var logoutBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

//        find the buttons by use of their ids
        val SigninButton = findViewById<Button>(R.id.signinBtn)
        val signupButton = findViewById<Button>(R.id.signupBtn)
        val uploadBtn = findViewById<Button>(R.id.uploadBtn)

//        Create the intents to the two activities
        signupButton.setOnClickListener {
            val intent = Intent(applicationContext, Signup::class.java)
            startActivity(intent)
        }
//        =====================================
        SigninButton.setOnClickListener {
            val intent = Intent(applicationContext, Signin::class.java)
            startActivity(intent)
        }

        uploadBtn.setOnClickListener {
            val intent = Intent(applicationContext, Addproducts::class.java)
            startActivity(intent)
        }

        signupBtn = findViewById(R.id.signupBtn)
        signinBtn = findViewById(R.id.signinBtn)
        welcomeText = findViewById(R.id.welcomeText)
        logoutBtn = findViewById(R.id.logoutBtn)

        val prefs = getSharedPreferences("user_session", MODE_PRIVATE)
        val username = prefs.getString("username", null)

        if (username != null) {
// ✅ User is logged in
            welcomeText.text = "Welcome $username"
            welcomeText.visibility = View.VISIBLE
            logoutBtn.visibility = View.VISIBLE

            signupBtn.visibility = View.GONE
            signinBtn.visibility = View.GONE
        } else {
// ❌ User not logged in
            signupBtn.visibility = View.VISIBLE
            signinBtn.visibility = View.VISIBLE

            welcomeText.visibility = View.GONE
            logoutBtn.visibility = View.GONE
        }

// 🔓 Logout logic
        logoutBtn.setOnClickListener {
            val editor = prefs.edit()
            editor.clear()
            editor.apply()

            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()

// Refresh activity
            startActivity(Intent(this, com.example.sokogardenonline.MainActivity::class.java))
            finish()
        }

        // find the recyclerView and the progress bar by use of their IDs
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val progressbar = findViewById<ProgressBar>(R.id.progressbar)

        //specify the API URL endpoint for fetching the products (alwaysData)
        val url = "https://kbenkamotho.alwaysdata.net/api/get_products"

        // import the helper class
        val helper = ApiHelper(applicationContext)

        //inside of the helper class, access the function loadproducts
        helper.loadProducts(url, recyclerView, progressbar)

    }
}