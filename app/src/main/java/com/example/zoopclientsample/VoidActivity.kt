package com.example.zoopclientsample

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.zoop.zoopandroidsdk.ZoopTerminalVoidPayment
import com.zoop.zoopandroidsdk.commons.ZLog
import com.zoop.zoopandroidsdk.terminal.ApplicationDisplayListener
import com.zoop.zoopandroidsdk.terminal.DeviceSelectionListener
import com.zoop.zoopandroidsdk.terminal.TerminalMessageType
import com.zoop.zoopandroidsdk.terminal.VoidTransactionListener
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class VoidActivity : BaseActivity(), ApplicationDisplayListener, VoidTransactionListener,
    DeviceSelectionListener {

    private var status = TransactionStatus.READY
    private var marketplaceId = Credentials.MARKETPLACE_ID
    private var sellerId = Credentials.SELLER_ID
    private var publishableKey = Credentials.PUBLISHABLE_KEY
    private var joTransactionResponse: JSONObject? = null
    private var joVoidResponse: JSONObject? = null
    private var valueWithCurrency = ""
    private var first4digitsCardNumber = ""
    private var terminalVoid: ZoopTerminalVoidPayment? = null

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
            findViewById<ConstraintLayout>(R.id.progressBarLayout).visibility = View.GONE
            findViewById<ConstraintLayout>(R.id.questionLayout).visibility = View.VISIBLE
            setupActionButton()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun setupOperationButtons() {
        findViewById<Button>(R.id.buttonConfirmOperation).setOnClickListener {
            findViewById<ConstraintLayout>(R.id.questionLayout).visibility = View.GONE
            findViewById<ConstraintLayout>(R.id.progressBarLayout).visibility = View.VISIBLE
            hideResponseShowProgressBar()
            terminalVoid = ZoopTerminalVoidPayment()
            terminalVoid!!.setApplicationDisplayListener(this@VoidActivity)
            terminalVoid!!.setVoidPaymentListener(this)
            status = TransactionStatus.PROCESSING
            terminalVoid!!.voidTransaction(joTransactionResponse?.getString("id"), marketplaceId, sellerId, publishableKey)
        }
        findViewById<Button>(R.id.buttonCancelOperation).setOnClickListener {
            finish()
        }
    }

    private fun setParamsVoidQuestion(valueWithCurrency: String, first4digitsCardNumber: String) {
        findViewById<TextView>(R.id.textViewTransactionToVoidQuestion).text =
            resources.getString(R.string.dialog_void_receipt_transaction_text_confirm_void, valueWithCurrency, first4digitsCardNumber)
    }

    private fun showTransactionToVoidDetails(joTransactionResponse: JSONObject) {
        var amount = ""
        if (joTransactionResponse.has("amount")) {
            amount = joTransactionResponse.getString("amount")
        }
        valueWithCurrency = Extras.parseDoubleToCurrenyFormat(amount)
        var paymentType = ""
        if (joTransactionResponse.has("payment_type")) {
            paymentType = joTransactionResponse.getString("payment_type")
        }
        var numberOfInstallments = ""
        if (joTransactionResponse.has("installment_plan")) {
            numberOfInstallments = joTransactionResponse.getString("installment_plan")
        }
        if (numberOfInstallments == "null") {
            numberOfInstallments = "1"
        }
        if (joTransactionResponse.has("payment_method")) {
            first4digitsCardNumber =
                joTransactionResponse.getJSONObject("payment_method").getString("first4_digits")
        }
        findViewById<TextView>(R.id.textViewTransactionToVoidValue).text = valueWithCurrency
        findViewById<TextView>(R.id.textViewTransactionToVoidPaymentType).text = Extras.translatePaymentType(paymentType)
        findViewById<TextView>(R.id.textViewTransactionToVoidNumberOfInstallments).text = Extras.formatNumberOfInstallments(numberOfInstallments)
    }

    private fun setupActionButton() {
        val buttonAction = findViewById<Button>(R.id.buttonAction)
        buttonAction?.let { button ->
            button.setOnClickListener {
                when (status) {
                    TransactionStatus.READY, TransactionStatus.ERROR -> {
                        button.text = resources.getString(R.string.label_cancel)
                        hideResponseShowProgressBar()
                        status = TransactionStatus.PROCESSING
                        terminalVoid!!.voidTransaction(
                            joTransactionResponse?.getString("id"),
                            marketplaceId,
                            sellerId,
                            publishableKey)
                    }
                    TransactionStatus.PROCESSING -> {
                        status = TransactionStatus.READY
                        try {
                            terminalVoid!!.requestAbortCharge()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    TransactionStatus.FINISHED -> {
                        status = TransactionStatus.READY
                        if (joVoidResponse != null) {
                            val intent = Intent(this, ReceiptActivity::class.java)
                            val bundle = Bundle()
                            bundle.putString("joTransactionResponse", joVoidResponse.toString())
                            intent.putExtras(bundle)
                            startActivity(intent)
                        }
                    }
                }
            }
        }
    }

    private fun hideResponseShowProgressBar() {
        findViewById<ImageView>(R.id.imageViewResponse).visibility = View.GONE
        findViewById<TextView>(R.id.textViewResponse).visibility = View.GONE
        findViewById<ProgressBar>(R.id.progressBar).visibility = View.VISIBLE
        findViewById<TextView>(R.id.textViewProgressBar).visibility = View.VISIBLE
    }

    private fun hideProgressBarShowResponse() {
        findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
        findViewById<TextView>(R.id.textViewProgressBar).visibility = View.GONE
        findViewById<ImageView>(R.id.imageViewResponse).visibility = View.VISIBLE
        findViewById<TextView>(R.id.textViewResponse).visibility = View.VISIBLE
    }

    private fun setResponseImageView(idImage: Int, colorString: String) {
        val imageViewResponse = findViewById<ImageView>(R.id.imageViewResponse)
        imageViewResponse.setImageBitmap(
            BitmapFactory.decodeResource(resources, idImage)
        )
        imageViewResponse.adjustViewBounds = true
        imageViewResponse.setColorFilter(Color.parseColor(colorString))
    }

    private fun setResponseTextView(text: String, colorString: String) {
        val textViewResponse = findViewById<TextView>(R.id.textViewResponse)
        textViewResponse.text = text
        textViewResponse.setTextColor(Color.parseColor(colorString))
    }

    override fun voidTransactionSuccessful(joResponse: JSONObject?) {
        runOnUiThread {
            try {
                ZLog.t(300033)
                status = TransactionStatus.FINISHED
                hideProgressBarShowResponse()
                setResponseImageView(R.drawable.icon_approved, "#006400")
                if (joResponse != null) {
                    setResponseTextView(resources.getString(R.string.text_void_transaction_step4_approved), "#006400")
                    joVoidResponse = joResponse
                    findViewById<Button>(R.id.buttonAction).text = resources.getString(R.string.charge_button_receipt_label)
                }
            } catch (e: Exception) {
                ZLog.exception(300034, e)
            }
        }
    }

    override fun voidTransactionFailed(joResponse: JSONObject?) {
        runOnUiThread {
            try {
                ZLog.error(300031)
                status = TransactionStatus.ERROR
                hideProgressBarShowResponse()
                setResponseImageView(R.drawable.icon_denied, "#8B0000")
                findViewById<Button>(R.id.buttonAction).text = resources.getString(R.string.label_try_again)
                if (joResponse != null) {
                    var applicationMessage = ""
                    if (joResponse.has("response_code")) {
                        if (joResponse.getString("response_code") == "8781013") {
                            applicationMessage = resources.getString(R.string.label_title_error_brand)
                        }
                    }
                    if (joResponse.has("error")) {
                        val joErrorDetails = joResponse.getJSONObject("error")
                        if (joErrorDetails.has("i18n_checkout_message_explanation")) {
                            applicationMessage =
                                joErrorDetails.getString("i18n_checkout_message_explanation")
                        }
                        if (joErrorDetails.has("i18n_checkout_message")) {
                            applicationMessage =
                                joErrorDetails.getString("i18n_checkout_message")
                            ZLog.t(300028, applicationMessage)
                        }
                        if (joErrorDetails.has("message")) {
                            applicationMessage += "\n" + joErrorDetails.getString("message")
                            ZLog.t(300028, applicationMessage)
                        }
                    } else {
                        ZLog.error(300030)
                    }
                    if (applicationMessage.isEmpty()) {
                        applicationMessage = resources.getString(R.string.unkown_error)
                    }
                    setResponseTextView(applicationMessage, "#8B0000")
                }
            } catch (e: Exception) {
                ZLog.exception(300032, e)
            }
        }
    }

    private fun showVoidWarnning(applicationMessage: String) {
        hideProgressBarShowResponse()
        ZLog.t(300028, applicationMessage)
        setResponseImageView(R.drawable.icon_abort, "#CCCC00")
        setResponseTextView(applicationMessage, "#CCCC00")
        findViewById<Button>(R.id.buttonAction).text = resources.getString(R.string.label_try_again)
    }

    override fun voidAborted() {
        runOnUiThread {
            status = TransactionStatus.READY
            showVoidWarnning(resources.getString(R.string.label_title_abort_brand))
        }
    }

    override fun currentVoidTransactionCanBeAbortedByUser(canAbortCurrentCharge: Boolean) {
        runOnUiThread {
            findViewById<Button>(R.id.buttonAction).isEnabled = canAbortCurrentCharge
        }
    }

    override fun showMessage(message: String?, messageType: TerminalMessageType?) {
        runOnUiThread {
            showMessage(message, messageType, null)
        }
    }

    override fun showMessage(message: String?, messageType: TerminalMessageType?, explanationMessage: String?) {
        runOnUiThread {
            findViewById<TextView>(R.id.textViewProgressBar).text = message
        }
    }

    override fun showDeviceListForUserSelection(p0: Vector<JSONObject>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deviceSelectedResult(p0: JSONObject?, p1: Vector<JSONObject>?, p2: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateDeviceListForUserSelection(
        p0: JSONObject?,
        p1: Vector<JSONObject>?,
        p2: Int
    ) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun bluetoothIsNotEnabledNotification() {
        val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        mBluetoothAdapter.startDiscovery()
    }
}
