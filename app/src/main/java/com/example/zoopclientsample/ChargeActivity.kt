package com.example.zoopclientsample

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.zoop.zoopandroidsdk.ZoopTerminalPayment
import com.zoop.zoopandroidsdk.commons.ZLog
import com.zoop.zoopandroidsdk.terminal.*
import org.json.JSONObject
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.*

class ChargeActivity : BaseActivity() , TerminalPaymentListener, DeviceSelectionListener,
    ExtraCardInformationListener, ApplicationDisplayListener {

    private var status = TransactionStatus.READY
    private var terminalPayment: ZoopTerminalPayment? = null
    private var sValueToCharge = ""
    private var iNumberOfInstallments = 0
    private var paymentOption = ZoopTerminalPayment.CHARGE_TYPE_CREDIT
    private var joTransactionResponse: JSONObject? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_charge)
        status = TransactionStatus.READY
        paymentOption = ZoopTerminalPayment.CHARGE_TYPE_CREDIT
        joTransactionResponse = null
        setupEditText()
        setupButtons()
        findViewById<Button>(R.id.buttonCreditOnly).performClick()
        setupSpinner()
        setupActionButton()
        try {
            terminalPayment = ZoopTerminalPayment()
            terminalPayment!!.setTerminalPaymentListener(this@ChargeActivity)
            terminalPayment!!.setDeviceSelectionListener(this@ChargeActivity)
            terminalPayment!!.setExtraCardInformationListener(this@ChargeActivity)
            terminalPayment!!.setApplicationDisplayListener(this@ChargeActivity)
        } catch (e: Exception) {
            Log.e("onClick exception", e.toString());
        }
    }

    private fun setupActionButton() {
        val buttonAction = findViewById<Button>(R.id.buttonAction)
        buttonAction?.let { button ->
            button.setOnClickListener {
                findViewById<ImageView>(R.id.imageViewResponse).visibility = View.GONE
                findViewById<TextView>(R.id.textViewResponse).visibility = View.GONE
                when (status) {
                    TransactionStatus.READY -> {
                        if (sValueToCharge.isNotEmpty()) {
                            button.text = resources.getString(R.string.label_cancel)
                            val valueToCharge: BigDecimal? = Extras.parseCurrencyFormatToBigDecimal(sValueToCharge)
                            status = TransactionStatus.PROCESSING
                            terminalPayment!!.charge(valueToCharge,
                                paymentOption,
                                iNumberOfInstallments,
                                resources.getString(R.string.marketplace_id),
                                getSellerId(),
                                resources.getString(R.string.publishable_key))
                        }
                    }
                    TransactionStatus.PROCESSING -> {
                        status = TransactionStatus.READY
                        try {
                            terminalPayment!!.requestAbortCharge()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    TransactionStatus.FINISHED -> {
                        status = TransactionStatus.READY
                        if (joTransactionResponse != null) {
                            val intent = Intent(this, ReceiptActivity::class.java)
                            val bundle = Bundle()
                            bundle.putString("joTransactionResponse", joTransactionResponse.toString())
                            intent.putExtras(bundle)
                            startActivity(intent)
                        }
                    }
                    TransactionStatus.ERROR -> {
                        status = TransactionStatus.READY
                        startActivity(Intent(this, ChargeActivity::class.java))
                    }
                }
            }
        }

    }

    private fun setupSpinner() {
        val spinner = findViewById<Spinner>(R.id.spinnerNumberOfInstallments)
        spinner?.let {
            val numberOfInstallmentsOpt = arrayOf("1x", "2x ","3x","4x","5x","6x", "7x","8x","9x","10x","11x","12x")
            val arrayAdapter = ArrayAdapter(this, R.layout.simple_spinner_item, numberOfInstallmentsOpt)
            arrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
            it.adapter = arrayAdapter
            it.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    iNumberOfInstallments = position + 1
                }
                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        }
    }

    private fun setupButtons() {
        val spinner = findViewById<Spinner>(R.id.spinnerNumberOfInstallments)
        val btnCreditOnly = findViewById<Button>(R.id.buttonCreditOnly)
        val btnCreditWithInstallments = findViewById<Button>(R.id.buttonCreditWithInstallments)
        val btnDebit = findViewById<Button>(R.id.buttonDebit)
        btnCreditOnly?.let { buttonPressed ->
            buttonPressed.setOnClickListener {
                spinner.setSelection(0)
                spinner.isEnabled = false
                spinner.isClickable = false
                updateButtons(buttonPressed, btnCreditWithInstallments, btnDebit)
                paymentOption = ZoopTerminalPayment.CHARGE_TYPE_CREDIT
            }
        }
        btnCreditWithInstallments?.let { buttonPressed ->
            buttonPressed.setOnClickListener {
                spinner.setSelection(1)
                spinner.isEnabled = true
                spinner.isClickable = true
                updateButtons(buttonPressed, btnCreditOnly, btnDebit)
                paymentOption = ZoopTerminalPayment.CHARGE_TYPE_CREDIT_WITH_INSTALLMENTS
            }
        }
        btnDebit?.let { buttonPressed ->
            buttonPressed.setOnClickListener {
                spinner.setSelection(0)
                spinner.isEnabled = false
                spinner.isClickable = false
                updateButtons(buttonPressed, btnCreditOnly, btnCreditWithInstallments)
                paymentOption = ZoopTerminalPayment.CHARGE_TYPE_DEBIT
            }
        }
    }

    private fun updateButtons(buttonPressed: Button, buttonUnpressed: Button, buttonUnpressedTwo: Button) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            buttonPressed.backgroundTintList =
                ContextCompat.getColorStateList(this, R.color.colorAccentAlt)
            buttonUnpressed.backgroundTintList =
                ContextCompat.getColorStateList(this, R.color.colorPrimaryAlt)
            buttonUnpressedTwo.backgroundTintList =
                ContextCompat.getColorStateList(this, R.color.colorPrimaryAlt)
        }
    }

    private fun setupEditText() {
        val editTextValueToCharge = findViewById<EditText>(R.id.editTextValueToCharge)
        editTextValueToCharge?.let {
            it.addTextChangedListener(object: TextWatcher {
                override fun afterTextChanged(s: Editable?) {}

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s.toString() != sValueToCharge) {
                        it.removeTextChangedListener(this)
                        val formatted = Extras.parseDoubleToCurrenyFormat(s.toString())
                        sValueToCharge = formatted
                        it.setText(formatted)
                        it.setSelection(formatted.length)
                        it.addTextChangedListener(this)
                    }
                }
            })
        }
    }

    override fun paymentSuccessful(joResponse: JSONObject?) {
        runOnUiThread {
            try {
                ZLog.t(300023)
                status = TransactionStatus.FINISHED
                hideProgressBarShowResponse()
                setResponseImageView(R.drawable.icon_approved, "#006400")
                if (joResponse != null) {
                    setResponseTextView(resources.getString(R.string.text_transaction_step4_approved), "#006400")
                    joTransactionResponse = joResponse
                    findViewById<Button>(R.id.buttonAction).text = resources.getString(R.string.charge_button_receipt_label)
                }
            } catch (e: Exception) {
                ZLog.exception(300024, e)
            }
        }
    }

    private fun showPaymentWarnning(applicationMessage: String) {
        hideProgressBarShowResponse()
        ZLog.t(300018, applicationMessage)
        setResponseImageView(R.drawable.icon_abort, "#CCCC00")
        setResponseTextView(applicationMessage, "#CCCC00")
        findViewById<Button>(R.id.buttonAction).text = resources.getString(R.string.charge_button_pay_label)
    }

    override fun paymentDuplicated(joResponse: JSONObject?) {
        runOnUiThread {
            status = TransactionStatus.READY
            showPaymentWarnning(resources.getString(R.string.text_transaction_step5_duplicated))
        }
    }

    override fun currentChargeCanBeAbortedByUser(canAbortCurrentCharge: Boolean) {
        runOnUiThread {
            findViewById<Button>(R.id.buttonAction).isEnabled = canAbortCurrentCharge
        }
    }

    override fun signatureResult(p0: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun paymentAborted() {
        runOnUiThread {
            status = TransactionStatus.READY
            showPaymentWarnning(resources.getString(R.string.label_title_abort_brand))
        }
    }

    override fun cardholderSignatureRequested() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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

    override fun paymentFailed(joResponse: JSONObject?) {
        runOnUiThread {
            try {
                ZLog.error(300021)
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
                            ZLog.t(300018, applicationMessage)
                        }
                        if (joErrorDetails.has("message")) {
                            applicationMessage += "\n" + joErrorDetails.getString("message")
                            ZLog.t(300018, applicationMessage)
                        }
                    } else {
                        ZLog.error(300020)
                    }
                    if (applicationMessage.isEmpty()) {
                        applicationMessage = resources.getString(R.string.unkown_error)
                    }
                    setResponseTextView(applicationMessage, "#8B0000")
                }
            } catch (e: Exception) {
                ZLog.exception(300022, e)
            }
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

    override fun cardLast4DigitsRequested() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun cardExpirationDateRequested() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun cardCVCRequested() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showMessage(stringMessage: String?, messageType: TerminalMessageType?) {
        runOnUiThread {
            showMessage(stringMessage, messageType, null)
        }
    }

    override fun showMessage(stringMessage: String?, messageType: TerminalMessageType?, sExplanation: String?) {
        runOnUiThread {
            findViewById<ProgressBar>(R.id.progressBar).visibility = View.VISIBLE
            val textViewProgressBar = findViewById<TextView>(R.id.textViewProgressBar)
            textViewProgressBar.visibility = View.VISIBLE
            textViewProgressBar.text = stringMessage
        }
    }
}
