package com.example.zoopclientsample

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import com.zoop.zoopandroidsdk.ZoopAPI
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (isLogged()) {

            setContentView(R.layout.activity_main)

            ZoopAPI.initialize(application)

            buttonSales.setOnClickListener {
                startActivity(Intent(this, ChargeActivity::class.java))
            }

            buttonTerminals.setOnClickListener {
                startActivity(Intent(this, ConfigPinPadActivity::class.java))
            }

            buttonLogout.setOnClickListener {
                showAlertDialog()
            }

        } else {

            startActivity(Intent(this, LoginActivity::class.java))

        }
    }

    override fun onResume() {
        super.onResume()
        if (!isLogged()) {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun showAlertDialog() {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle(resources.getString(R.string.dialog_logout_title))
            .setMessage(resources.getString(R.string.dialog_logout_message))
            .setPositiveButton(resources.getString(R.string.label_yes)) { dialog, whichButton ->
                logout()
                startActivity(Intent(this, LoginActivity::class.java))
            }
            .setNegativeButton(resources.getString(R.string.label_no)) { dialog, whichButton ->
                dialog.cancel()
            }
        dialog.show()
    }

}
