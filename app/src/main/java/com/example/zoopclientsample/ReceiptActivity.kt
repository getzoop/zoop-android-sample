package com.example.zoopclientsample

import android.os.Bundle

class ReceiptActivity : BaseActivity() {

    private val TAG = ReceiptActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receipt)
    }
}
