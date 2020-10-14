package com.example.zoopclientsample

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.zoop.zoopandroidsdk.ZoopAPI


class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (isLogged()) {

            setContentView(R.layout.activity_main)

            ZoopAPI.initialize(application)

            mountWelcomeMessage()

            findViewById<Button>(R.id.buttonSales).setOnClickListener {
                startActivity(Intent(this, ChargeActivity::class.java))
            }

            findViewById<Button>(R.id.buttonTerminals).setOnClickListener {
                startActivity(Intent(this, ConfigPinPadActivity::class.java))
            }

            findViewById<Button>(R.id.buttonLogout).setOnClickListener {
                showAlertDialog()
            }

        } else {

            startActivity(Intent(this, LoginActivity::class.java))

        }
    }

    private fun mountWelcomeMessage() {
        var exhibitionName = Preferences(this).getStoredString(Constants.FIRST_NAME)
        if (exhibitionName.isNullOrEmpty()) {
            exhibitionName = Preferences(this).getStoredString(Constants.USERNAME)
        }
        findViewById<TextView>(R.id.textViewWelcome).text = getString(R.string.welcome_message, exhibitionName)
    }

    override fun onResume() {
        super.onResume()
        if (!isLogged()) {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun showAlertDialog() {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle(getString(R.string.dialog_logout_title))
            .setMessage(getString(R.string.dialog_logout_message))
            .setPositiveButton(getString(R.string.label_yes)) { dialog, whichButton ->
                logout()
                startActivity(Intent(this, LoginActivity::class.java))
            }
            .setNegativeButton(getString(R.string.label_no)) { dialog, whichButton ->
                dialog.cancel()
            }
        dialog.show()
    }

    override fun onBackPressed() {
        TODO("Not yet implemented")
    }

}
