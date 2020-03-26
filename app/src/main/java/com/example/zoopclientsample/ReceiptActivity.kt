package com.example.zoopclientsample

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import com.example.zoopclientsample.api.ReceiptService
import com.example.zoopclientsample.api.RetrofitInstance
import com.example.zoopclientsample.ui.AutoResizeTextView
import com.google.gson.Gson
import com.zoop.zoopandroidsdk.commons.ZLog
import com.zoop.zoopandroidsdk.terminal.ReceiptDeliveryListener
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class ReceiptActivity : BaseActivity(), ReceiptDeliveryListener {

    private var receiptId = ""
    private var marketplaceId = Credentials.MARKETPLACE_ID
    private var userToken = Credentials.USER_TOKEN
    private var errorMsg = ""
    private var joZoopReceipt: JSONObject? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receipt)
        val bundle: Bundle? = intent.extras
        val sJoTransactionResponse: String? = bundle?.getString("joTransactionResponse")
        findViewById<AutoResizeTextView>(R.id.autoResizeTextViewPrintReceipt).visibility = View.GONE
        findViewById<ProgressBar>(R.id.progressBarPrintReceipt).visibility = View.VISIBLE
        try {
            val joTransactionResponse = JSONObject(sJoTransactionResponse)
            receiptId = joTransactionResponse.getString("sales_receipt")
            getReceipt()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun getReceipt() {
        val receiptService: ReceiptService? =
            RetrofitInstance.retrofitInstance?.create(ReceiptService::class.java)
        val receiptCall =
            receiptService!!.getReceipt("Bearer $userToken", marketplaceId, receiptId)
        receiptCall!!.enqueue(object : Callback<Any?> {
            override fun onResponse(
                call: Call<Any?>,
                response: Response<Any?>
            ) {
                try {
                    if (response.isSuccessful) {
                        joZoopReceipt = JSONObject(Gson().toJson(response.body()))
                        setButtonsAction()
                        setAutoResizeTextView(joZoopReceipt!!,"sales_receipt_merchant")
                    } else {
                        errorMsg = if (response.code() == 401) {
                            resources.getString(R.string.token_error_activity_receipt)
                        } else {
                            resources.getString(R.string.error_activity_receipt)
                        }
                        hideProgressAndShowToast()
                    }
                } catch (e: JSONException) {
                    ZLog.exception(300063, e)
                }
            }

            override fun onFailure(call: Call<Any?>?, t: Throwable?) {
                errorMsg = resources.getString(R.string.unkown_error)
                hideProgressAndShowToast()
            }
        })
    }

    private fun hideProgressAndShowToast() {
        findViewById<ProgressBar>(R.id.progressBarPrintReceipt).visibility = View.GONE
        val autoResizeTextViewPrintReceipt = findViewById<AutoResizeTextView>(R.id.autoResizeTextViewPrintReceipt)
        autoResizeTextViewPrintReceipt.visibility = View.VISIBLE
        if (errorMsg.isNotEmpty()) {
            autoResizeTextViewPrintReceipt.text = errorMsg
            errorMsg = ""
        }
    }

    private fun setButtonsAction() {
        findViewById<Button>(R.id.buttonNewTransaction).setOnClickListener {
            startActivity(Intent(this, ChargeActivity::class.java))
        }
        findViewById<Button>(R.id.buttonVoidTransaction).setOnClickListener {
            //TODO:
            Toast.makeText(applicationContext, R.string.charge_button_void_transaction, Toast.LENGTH_SHORT).show()
        }
        findViewById<Button>(R.id.buttonPrintReceiptCardholderCopy).setOnClickListener {
            joZoopReceipt?.let {
                setAutoResizeTextView(it, "sales_receipt_cardholder")
            }
        }
        findViewById<Button>(R.id.buttonPrintReceiptMerchantCopy).setOnClickListener {
            joZoopReceipt?.let {
                setAutoResizeTextView(it, "sales_receipt_merchant")
            }
        }
    }

    private fun setAutoResizeTextView(joZoopReceipt: JSONObject, receiptType: String) {
        var sReceiptText: String? =
            joZoopReceipt.getJSONObject("original_receipt").getString(receiptType)
        if (sReceiptText != null) {
            sReceiptText = sReceiptText.replace("@", "\n")
            sReceiptText = sReceiptText.substring(2)
        }
        findViewById<ProgressBar>(R.id.progressBarPrintReceipt).visibility = View.GONE
        val autoResizeTextViewPrintReceipt = findViewById<AutoResizeTextView>(R.id.autoResizeTextViewPrintReceipt)
        autoResizeTextViewPrintReceipt.visibility = View.VISIBLE
        autoResizeTextViewPrintReceipt.text = sReceiptText
    }

    override fun smsReceiptResult(p0: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun emailReceiptResult(p0: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
