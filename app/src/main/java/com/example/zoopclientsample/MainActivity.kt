package com.example.zoopclientsample

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.zoop.zoopandroidsdk.ZoopAPI
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ZoopAPI.initialize(application)

        buttonSales.setOnClickListener {
            startActivity(Intent(this, ChargeActivity::class.java))
        }

        buttonTerminals.setOnClickListener {
            startActivity(Intent(this, ConfigPinPadActivity::class.java))
        }

        buttonOthers.setOnClickListener {
            Toast.makeText(
                this,
                resources.getString(R.string.others),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

}
