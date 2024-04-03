package com.trips.project.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.trips.project.R

class LoginActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val currentUser: FirebaseUser? = mAuth.currentUser
        if (currentUser != null) {
            navigateToMainList()
        }

        val buttonRegister: Button = findViewById(R.id.new_sign_up_btn)
        val buttonLogin: Button = findViewById(R.id.log_in_btn)
        buttonRegister.setOnClickListener {
            navigateToRegister()
        }
        buttonLogin.setOnClickListener {
            val email: String = findViewById<EditText>(R.id.email_login_input).text.toString()
            val password: String = findViewById<EditText>(R.id.password_login_input).text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            navigateToMainList()
                        } else {
                            Toast.makeText(this@LoginActivity, "Login Failed", Toast.LENGTH_LONG).show()
                        }
                    }
            } else {
                Toast.makeText(this@LoginActivity, "You did not enter your info", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun navigateToRegister() {
        val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToMainList() {
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
