package com.example.zoopclientsample

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.example.zoopclientsample.model.ChargeStatus
import com.zoop.zoopandroidsdk.ZoopTerminalPayment
import com.zoop.zoopandroidsdk.commons.ZLog
import com.zoop.zoopandroidsdk.terminal.*
import org.json.JSONObject
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.*

class ChargeActivity : BaseActivity() , TerminalPaymentListener, DeviceSelectionListener,
    ExtraCardInformationListener, ApplicationDisplayListener {

    private var status = ChargeStatus.READY
    private var terminalPayment: ZoopTerminalPayment? = null
    private var sValueToCharge = ""
    private var iNumberOfInstallments = 0
    private var paymentOption = ZoopTerminalPayment.CHARGE_TYPE_CREDIT
    private var marketplaceId = "insert your marketplaceId here"
    private var sellerId = "insert your sellerId here"
    private var publishableKey: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_charge)
        status = ChargeStatus.READY
        paymentOption = ZoopTerminalPayment.CHARGE_TYPE_CREDIT
        setupEditText()
        setupButtons()
        findViewById<Button>(R.id.btn_credit_only).performClick()
        setupSpinner()
        callCharge()
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

    private fun callCharge() {
        val buttonPay = findViewById<Button>(R.id.btn_pay)
        buttonPay?.let {
            it.setOnClickListener {
                when (status) {
                    ChargeStatus.READY -> {
                        if (sValueToCharge.isNotEmpty()) {
                            buttonPay.text = resources.getString(R.string.charge_button_cancel_label)
                            val cleanString = sValueToCharge.replace("R$", "").replace("," , ".").trim()
                            val valueToCharge: BigDecimal? = BigDecimal(cleanString)
                            status = ChargeStatus.PROCESSING
                            terminalPayment!!.charge(valueToCharge,
                                paymentOption,
                                iNumberOfInstallments,
                                marketplaceId,
                                sellerId,
                                publishableKey)
                        }
                    }
                    ChargeStatus.PROCESSING -> {
                        try {
                            terminalPayment!!.requestAbortCharge()
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                    }
                    ChargeStatus.FINISHED -> {
                        //do nothing
                    }
                }
            }
        }

    }

    private fun setupSpinner() {
        val spinner = findViewById<Spinner>(R.id.spinnerNumberOfInstallments)
        spinner?.let {
            val numberOfInstallmentsOpt = arrayOf("Vista", "2x ","3x","4x","5x","6x", "7x","8x","9x","10x","11x","12x")
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
        val btnCreditOnly = findViewById<Button>(R.id.btn_credit_only)
        val btnCreditWithInstallments = findViewById<Button>(R.id.btn_credit_with_installments)
        val btnDebit = findViewById<Button>(R.id.btn_debit)
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
                ContextCompat.getColorStateList(this, R.color.colorAccent)
            buttonUnpressed.backgroundTintList =
                ContextCompat.getColorStateList(this, R.color.colorPrimary)
            buttonUnpressedTwo.backgroundTintList =
                ContextCompat.getColorStateList(this, R.color.colorPrimary)
        }
    }

    private fun setupEditText() {
        val editTextValueToCharge = findViewById<EditText>(R.id.editTextValueToCharge)
        editTextValueToCharge?.let {
            it.addTextChangedListener(object: TextWatcher {
                override fun afterTextChanged(s: Editable?) {}

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if(s.toString() != sValueToCharge) {
                        it.removeTextChangedListener(this)
                        val cleanString = s.toString().replace(Regex("[R$.,]"), "").trim()
                        val parsed = cleanString.toDouble()
                        val formatted = NumberFormat.getCurrencyInstance().format(parsed/100)
                        sValueToCharge = formatted
                        it.setText(formatted)
                        it.setSelection(formatted.length)
                        it.addTextChangedListener(this)
                    }
                }
            })
        }
    }

    override fun paymentSuccessful(p0: JSONObject?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun paymentDuplicated(p0: JSONObject?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun currentChargeCanBeAbortedByUser(p0: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun signatureResult(p0: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun paymentAborted() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun cardholderSignatureRequested() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun paymentFailed(p0: JSONObject?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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
            try {
                ZLog.t(677303, stringMessage)
                val progressBarLayout = findViewById<ConstraintLayout>(R.id.progressBarLayout)
                progressBarLayout.visibility = View.VISIBLE
                val textViewProgressBar = findViewById<TextView>(R.id.textViewProgressBar)
                textViewProgressBar.text = stringMessage
            } catch (e: Exception) {
                ZLog.exception(677520, e)
            }
        }
    }
}
