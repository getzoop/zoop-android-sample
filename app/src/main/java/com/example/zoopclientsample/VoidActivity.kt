package com.example.zoopclientsample

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class VoidActivity : BaseActivity() {

    private var marketplaceId = Credentials.MARKETPLACE_ID
    private var sellerId = Credentials.SELLER_ID
    private var userToken = Credentials.USER_TOKEN
    private var publishableKey = Credentials.PUBLISAHBLE_KEY
    private var joTransactionResponse: JSONObject? = null
    private var joVoidResponse: JSONObject? = null
    private var valueWithCurrency = ""
    private var first4digitsCardNumber = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_void)
        val bundle: Bundle? = intent.extras
        val sJoTransactionResponse = bundle?.getString("joTransactionResponse")!!
        try {
            joTransactionResponse = JSONObject(sJoTransactionResponse)
            joTransactionResponse?.let {
                showTransactionToVoidDetails(it)
            }
            setParamsVoidQuestion(valueWithCurrency, first4digitsCardNumber)
            setupOperationButtons()
            findViewById<ConstraintLayout>(R.id.processingLayout).visibility = View.GONE
            findViewById<ConstraintLayout>(R.id.questionLayout).visibility = View.VISIBLE
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun setupOperationButtons() {
        findViewById<Button>(R.id.buttonConfirmOperation).setOnClickListener {
            //TODO:
            Toast.makeText(
                this,
                resources.getString(R.string.label_ok),
                Toast.LENGTH_SHORT
            ).show()
        }
        findViewById<Button>(R.id.buttonCancelOperation).setOnClickListener {
            //TODO:
            Toast.makeText(
                this,
                resources.getString(R.string.label_cancel),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun setParamsVoidQuestion(valueWithCurrency: String, first4digitsCardNumber: String) {
        findViewById<TextView>(R.id.textViewTransactionToVoidQuestion).text =
            resources.getString(R.string.dialog_void_receipt_transaction_text_confirm_void, valueWithCurrency, first4digitsCardNumber)
    }

    private fun showTransactionToVoidDetails(joTransactionResponse: JSONObject) {
        if (joTransactionResponse.has("amount")) {
            val value = joTransactionResponse.getString("amount")
            valueWithCurrency = value
            if (joTransactionResponse.has("currency")) {
                valueWithCurrency += " " + joTransactionResponse.getString("currency")
            }
        }
        var paymentType: String? = ""
        if (joTransactionResponse.has("payment_type")) {
            paymentType = joTransactionResponse.getString("payment_type").toUpperCase(Locale.ROOT)
        }
        var numberOfInstallments: String? = ""
        if (joTransactionResponse.has("installment_plan")) {
            numberOfInstallments = joTransactionResponse.getString("installment_plan")
        }
        if (joTransactionResponse.has("payment_method")) {
            first4digitsCardNumber =
                joTransactionResponse.getJSONObject("payment_method").getString("first4_digits")
        }
        findViewById<TextView>(R.id.textViewTransactionToVoidValue).text = valueWithCurrency
        findViewById<TextView>(R.id.textViewTransactionToVoidPaymentType).text = paymentType
        findViewById<TextView>(R.id.textViewTransactionToVoidNumberOfInstallments).text = numberOfInstallments
    }
}
