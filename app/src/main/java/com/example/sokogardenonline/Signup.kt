package com.example.sokogardenonline

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.loopj.android.http.RequestParams

class Signup : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_signup)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // find all view by use of their ids
        val username = findViewById<EditText>(R.id.username)
        val email = findViewById<EditText>(R.id.email)
        val password = findViewById<EditText>(R.id.password)
        val phone = findViewById<EditText>(R.id.phone)
        val signupButton = findViewById<Button>(R.id.signupBtn)
        val signinTextView = findViewById<TextView>(R.id.signintxt)
        val responseTxt = findViewById<TextView>(R.id.responseTxt)


        //below when a person clicks on the TextView, he/she is navigated to the singin page
        signinTextView.setOnClickListener {
            val intent = Intent(applicationContext, Signin::class.java)
            startActivity(intent)
        }

        //on click of the signup Button, we want to register a person
        signupButton.setOnClickListener {
            // Specify the API endpoint
            val api = "https://kbenkamotho.alwaysdata.net/api/signup"

            // Create a RequestParams ~ it is where we are going to hold all the data
            val data = RequestParams()

            // Add/Append the username, email, password and phone on the data
            data.put("username", username.text.toString().trim())
            data.put("email", email.text.toString().trim())
            data.put("password", password.text.toString().trim())
            data.put("phone", phone.text.toString().trim())

            //import the ApiHelper Class
            val helper = ApiHelper(applicationContext)

            // Inside of the helper class, access the function post
            helper.postSignup(api, data) { message ->
                runOnUiThread {
                    responseTxt.text = message

                    if (message.contains("success")) {
                        email.text.clear()
                        password.text.clear()
                        phone.text.clear()
                        username.text.clear()
                    }
                }
                // clear the details
//            email.text.clear()
//            password.text.clear()
//            phone.text.clear()
//            username.text.clear()

                //intent to the mainActivity page
//            val intent = Intent(applicationContext, MainActivity::class.java)
//            startActivity(intent)


            }

        }
    }
}