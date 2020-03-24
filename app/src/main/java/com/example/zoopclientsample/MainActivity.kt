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

        btn_sales.setOnClickListener {
            startActivity(Intent(this, ChargeActivity::class.java))
        }

        btn_terminals.setOnClickListener {
            startActivity(Intent(this, ConfigPinPadActivity::class.java))
        }

        btn_others.setOnClickListener {
            Toast.makeText(
                this,
                resources.getString(R.string.others),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

}
