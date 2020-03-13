package com.example.zoopclientsample

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import com.zoop.zoopandroidsdk.ZoopTerminalPayment
import com.zoop.zoopandroidsdk.terminal.*
import org.json.JSONObject
import java.text.NumberFormat
import java.util.*

class ChargeActivity : BaseActivity() , TerminalPaymentListener, DeviceSelectionListener,
    ExtraCardInformationListener, ApplicationDisplayListener {

    var terminalPayment: ZoopTerminalPayment? = null
    var iNumberOfInstallments = 0

    var current = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_charge)
        setupButtons()
        setupSpinner()
        setupEditText()
        try {
            terminalPayment = ZoopTerminalPayment()
            terminalPayment!!.setTerminalPaymentListener(this@ChargeActivity)
            terminalPayment!!.setDeviceSelectionListener(this@ChargeActivity)
            terminalPayment!!.setExtraCardInformationListener(this@ChargeActivity)
            terminalPayment!!.setApplicationDisplayListener(this@ChargeActivity)
//            zoopTerminalPayment.charge(java.math.BigDecimal valueToCharge,
//                int paymentOption,int iNumberOfInstallments,
//                java.lang.String marketplaceId,
//                java.lang.String sellerId,
//                java.lang.String publishableKey)
        } catch (e : Exception) {
            Log.e("onClick exception", e.toString());
        }
    }

    private fun setupSpinner() {
        val spinner = findViewById<Spinner>(R.id.spinnerNumberOfInstallments)
        spinner?.let {
            val numberOfInstallmentsOpt = arrayOf("Vista", "2x ","3x","4x","5x","6x", "7x","8x","9x","10x","11x","12x")
            val arrayAdapter = ArrayAdapter(this, R.layout.simple_spinner_item, numberOfInstallmentsOpt)
            arrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
            spinner.adapter = arrayAdapter
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    Toast.makeText(this@ChargeActivity,"iNumberOfInstallments selected: ${numberOfInstallmentsOpt[position]}", Toast.LENGTH_SHORT).show()
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
        btnCreditOnly.setOnClickListener {
            Toast.makeText(this@ChargeActivity,"paymentOption clicked: btnCreditOnly", Toast.LENGTH_SHORT).show()
            spinner.setSelection(0)
            spinner.isEnabled = false
            spinner.isClickable = false
            updateButtons(btnCreditOnly, btnCreditWithInstallments, btnDebit)
        }
        btnCreditWithInstallments.setOnClickListener {
            Toast.makeText(this@ChargeActivity,"paymentOption clicked: btnCreditWithInstallments", Toast.LENGTH_SHORT).show()
            spinner.setSelection(1)
            spinner.isEnabled = true
            spinner.isClickable = true
            updateButtons(btnCreditWithInstallments, btnCreditOnly, btnDebit)
        }
        btnDebit.setOnClickListener {
            Toast.makeText(this@ChargeActivity,"paymentOption clicked: btnDebit", Toast.LENGTH_SHORT).show()
            spinner.setSelection(0)
            spinner.isEnabled = false
            spinner.isClickable = false
            updateButtons(btnDebit, btnCreditOnly, btnCreditWithInstallments)
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
        editTextValueToCharge.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString() != current) {
                    editTextValueToCharge.removeTextChangedListener(this)
                    val cleanString = s.toString().replace(Regex("[R$.,]"), "").trim()
                    val parsed = cleanString.toDouble()
                    val formatted = NumberFormat.getCurrencyInstance().format(parsed/100)
                    current = formatted
                    editTextValueToCharge.setText(formatted)
                    editTextValueToCharge.setSelection(formatted.length)
                    editTextValueToCharge.addTextChangedListener(this)
                }
            }
        })
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

    override fun showMessage(p0: String?, p1: TerminalMessageType?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showMessage(p0: String?, p1: TerminalMessageType?, p2: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
