package ca.unb.mobiledev.saferideapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import ca.unb.mobiledev.saferideapp.sampledata.CredentialData
import ca.unb.mobiledev.saferideapp.utils.CredentialJsonUtils

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val logoImageView = findViewById<ImageView>(R.id.logoImageView)
        logoImageView.setImageResource(R.drawable.unb_safe_ride_logo)

        val etDriverUsername = findViewById<EditText>(R.id.etUsername)
        val etDriverPassword = findViewById<EditText>(R.id.etPassword)
        val btnDriverSignIn = findViewById<Button>(R.id.btnSignIn)

        btnDriverSignIn.setOnClickListener {
            val username = etDriverUsername.text.toString()
            val password = etDriverPassword.text.toString()

            val appContext: Context = this.applicationContext
            val credentialData = CredentialJsonUtils(appContext)
            val credentialList = credentialData.getCredentials()

            this.deleteDatabase("safeRideDB")
            this.deleteDatabase("scheduleDB")

            val signIn = isSignInValid(username, password, credentialList);
            if (signIn != -1) {

                val dashboardIntent = Intent(this, DriverDashboardActivity::class.java)
                dashboardIntent.putExtra("Name", credentialList[signIn].studentName)
                dashboardIntent.putExtra("Id", credentialList[signIn].idNum)
                startActivity(dashboardIntent)
                finish()
            } else {
                Toast.makeText(this, "Invalid credentials. Please try again!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isSignInValid(username: String, password: String, list: ArrayList<CredentialData>): Int {
        for (i in 0 until list.size) {
            val credObj = list[i]
            if (username == credObj.username) {
                if (password == credObj.pass) {
                    return i
                }
                return -1
            }
        }
        return -1
    }

}