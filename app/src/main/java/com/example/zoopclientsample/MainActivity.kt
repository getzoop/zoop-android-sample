package com.example.zoopclientsample

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.zoop.zoopandroidsdk.ZoopAPI
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        val isLogged = checkIfIsLogged()

        if (!isLogged) {

            startActivity(Intent(this, LoginActivity::class.java))

        } else {

            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)

            ZoopAPI.initialize(application)

            buttonSales.setOnClickListener {
                startActivity(Intent(this, ChargeActivity::class.java))
            }

            buttonTerminals.setOnClickListener {
                startActivity(Intent(this, ConfigPinPadActivity::class.java))
            }

            buttonLogout.setOnClickListener {
                startActivity(Intent(this, LoginActivity::class.java))
            }

        }
    }

    private fun checkIfIsLogged(): Boolean {
        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        val sUsername = sharedPref.getString("USERNAME", "")
        val sPassword = sharedPref.getString("PASSWORD", "")
        val token = sharedPref.getString("USER_TOKEN", "")
        val sellerId = sharedPref.getString("SELLER_ID", "")
        if (sUsername.isNullOrEmpty() || sPassword.isNullOrEmpty() || token.isNullOrEmpty() || sellerId.isNullOrEmpty()) {
            return false
        }
        return true
    }

}
