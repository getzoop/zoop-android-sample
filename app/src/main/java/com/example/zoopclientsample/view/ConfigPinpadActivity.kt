package com.example.zoopclientsample.view

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import com.example.zoopclientsample.R
import com.example.zoopclientsample.adapter.TerminalAdapter
import com.example.zoopclientsample.adapter.TerminalAdapterListener
import com.example.zoopclientsample.model.TerminalModel
import com.karumi.dexter.Dexter
import com.karumi.dexter.listener.multi.DialogOnAnyDeniedMultiplePermissionsListener
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.zoop.zoopandroidsdk.TerminalListManager
import com.zoop.zoopandroidsdk.commons.TypeTerminalKeyEnum
import com.zoop.zoopandroidsdk.commons.TypeTerminalKeyErrorEnum
import com.zoop.zoopandroidsdk.commons.ZLog
import com.zoop.zoopandroidsdk.terminal.DeviceSelectionListener
import com.zoop.zoopandroidsdk.terminal.ZoopTerminalKeyValidatorListener
import org.json.JSONObject
import java.util.*

class ConfigPinpadActivity : BaseActivity(), DeviceSelectionListener,
    ZoopTerminalKeyValidatorListener, TerminalAdapterListener {

    private var terminalListManager: TerminalListManager? = null
    private var terminalAdapter: TerminalAdapter? = null
    private var lv: ListView? = null
    private var isCheckingTerminal = false
    private var buttonFinishConfiguration: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config_pinpad)
        setupListView()
        setupButton()
        terminalListManager = TerminalListManager(this, applicationContext)
        terminalListManager?.startTerminalsDiscovery()
        callPermissionsListener()
    }

    private fun setupListView() {
        lv = findViewById<ListView>(R.id.listViewAvailableTerminals)
        lv!!.choiceMode = ListView.CHOICE_MODE_SINGLE
        terminalAdapter = TerminalAdapter(this,
            R.layout.item_list_terminals, this)
        lv!!.adapter = terminalAdapter
    }

    private fun setupButton() {
        buttonFinishConfiguration = findViewById<Button>(R.id.buttonFinishConfiguration)
        buttonFinishConfiguration?.let { button ->
            button.setOnClickListener {
                if (isCheckingTerminal) {
                    //abort terminal compatibility checking
                    terminalListManager!!.interruptCurrentThreadThatCheckTerminalCompatibility()
                    button.setText(R.string.button_terminal_configuration_finished)
                    isCheckingTerminal = false
                } else {
                    val chargeIntent = Intent(this@ConfigPinpadActivity, ChargeActivity::class.java)
                    startActivity(chargeIntent)
                    finish()
                }
            }
        }
    }

    private fun callPermissionsListener() {
        val dialogMultiplePermissionsListener: MultiplePermissionsListener =
            DialogOnAnyDeniedMultiplePermissionsListener.Builder
                .withContext(this)
                .withTitle(R.string.permission_alert_title)
                .withMessage(R.string.permission_alert_text)
                .withButtonText(android.R.string.ok)
                .build()

        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            .withListener(dialogMultiplePermissionsListener)
            .check()
    }

    override fun onDestroy() {
        terminalListManager?.finishTerminalDiscovery()
        super.onDestroy()
    }

    override fun showDeviceListForUserSelection(
        vectorZoopTerminals: Vector<JSONObject>?
    ) {
        try {
            val labelTerminalList = findViewById<TextView>(R.id.textViewTerminalList)
            if (vectorZoopTerminals == null) {
                lv = findViewById<ListView>(R.id.listViewAvailableTerminals)
                lv?.let {
                    it.visibility = View.GONE
                }
                labelTerminalList?.let {
                    it.text = resources.getString(R.string.label_no_terminal_available)
                }
                return
            } else {
                labelTerminalList?.let {
                    it.text = resources.getString(R.string.label_select_available_terminal)
                }
            }
            //parsing JSON object terminal vector to terminal model list
            val terminalModels = ArrayList<TerminalModel>()
            for (joZoopTerminal in vectorZoopTerminals) {
                val model = TerminalModel(joZoopTerminal)
                var isSelected = false
                val uri = joZoopTerminal.getString("uri")
                val joSelectedZoopTerminal = TerminalListManager.getCurrentSelectedZoopTerminal()
                isSelected = if (joSelectedZoopTerminal?.getString("uri") != null) {
                    0 == uri.compareTo(joSelectedZoopTerminal.getString("uri"))
                } else {
                    false
                }
                model.selected = isSelected
                terminalModels.add(model)
            }
            terminalAdapter?.setTerminalList(terminalModels)
        } catch (e: Exception) {
            ZLog.exception(300064, e)
        }
    }

    override fun updateDeviceListForUserSelection(
        joNewlyFoundZoopDevice: JSONObject?,
        vectorZoopTerminals: Vector<JSONObject?>?, iNewlyFoundDeviceIndex: Int
    ) {
        try {
            joNewlyFoundZoopDevice?.let {
                val model = TerminalModel(it)
                var isSelected = false
                val uri = it.getString("uri")
                val joSelectedZoopTerminal = TerminalListManager.getCurrentSelectedZoopTerminal()
                isSelected = if (joSelectedZoopTerminal?.getString("uri") != null) {
                    0 == uri.compareTo(joSelectedZoopTerminal.getString("uri"))
                } else {
                    false
                }
                model.selected = isSelected
                terminalAdapter?.addTerminal(model)
            }
        } catch (e: Exception) {
            ZLog.exception(677541, e)
        }
    }

    override fun bluetoothIsNotEnabledNotification() {
        terminalListManager?.enableDeviceBluetoothAdapter()
    }

    override fun deviceSelectedResult(
        joZoopSelectedDevice: JSONObject?,
        vectorAllAvailableZoopTerminals: Vector<JSONObject?>?,
        iSelectedDeviceIndex: Int
    ) {
        try {
            if (joZoopSelectedDevice != null) {
                //unchecked all device itens and checked the selected device
                terminalAdapter?.checkRadioButton()
                val btn = findViewById<Button>(R.id.buttonFinishConfiguration)
                btn.visibility = View.VISIBLE
                terminalAdapter!!.notifyDataSetChanged()
            }
        } catch (e: Exception) {
            ZLog.exception(300056, e)
        }
    }

    private fun verifyJSONObjectTerminalIsZoopTerminal(jsTerminal: JSONObject) {
        try {
            terminalAdapter!!.setEnable(false)
            //hide text "Maquininha Incompatível
            terminalAdapter!!.hideErrorTextViewInfoSelected()
            terminalAdapter!!.showLoading()
            terminalListManager!!.checkTerminalCompatibility(jsTerminal, this)
        } catch (e: Exception) {
            ZLog.exception(300064, e)
            //hide loading of all itens
            terminalAdapter!!.hideLoadingUIComponents()
            terminalAdapter!!.showErrorTextView(R.string.error_validator_terminal)
        }
    }

    override fun terminalModelItemOnClick(model: TerminalModel?, position: Int) {
        try {
            terminalAdapter!!.setEnable(true)
            isCheckingTerminal = true
            buttonFinishConfiguration?.text = resources.getString(R.string.button_terminal_configuration_finished_cancel)
            model?.jsonTerminal?.let {
                verifyJSONObjectTerminalIsZoopTerminal(it)
                ZLog.t("Selected device by click: " + it.toString(3))
                terminalListManager!!.requestZoopDeviceSelection(it)
            }
        } catch (e: Exception) {
            ZLog.exception(677601, e)
        }
    }

    override fun compatibilityResult(typeTerminalKeyEnum: TypeTerminalKeyEnum?, jsonObject: JSONObject?) {
        runOnUiThread {
            when (typeTerminalKeyEnum) {
                TypeTerminalKeyEnum.KEY_COMPATIBLE -> {
                    terminalAdapter!!.hideLoadingUIComponents()
                    terminalAdapter!!.updateTypeTerminalModelSelected(TypeTerminalKeyEnum.KEY_COMPATIBLE)
                    buttonFinishConfiguration!!.setText(R.string.button_terminal_configuration_finished)
                    isCheckingTerminal = false
                    terminalAdapter!!.setEnable(true)
                }
                TypeTerminalKeyEnum.KEY_PARTIALLY_COMPATIBLE -> {
                    terminalAdapter!!.hideLoadingUIComponents()
                    terminalAdapter!!.updateTypeTerminalModelSelected(TypeTerminalKeyEnum.KEY_PARTIALLY_COMPATIBLE)
                    buttonFinishConfiguration!!.setText(R.string.button_terminal_configuration_finished)
                    isCheckingTerminal = false
                    terminalAdapter!!.setEnable(true)
                }
                TypeTerminalKeyEnum.KEY_INCOMPATIBLE -> {
                    terminalAdapter!!.hideLoadingUIComponents()
                    terminalAdapter!!.updateTypeTerminalModelSelected(TypeTerminalKeyEnum.KEY_INCOMPATIBLE)
                    buttonFinishConfiguration!!.setText(R.string.button_terminal_configuration_finished)
                    isCheckingTerminal = false
                    terminalAdapter!!.setEnable(true)
                }
                else -> {
                    //do nothing
                }
            }
        }
    }

    override fun compatibilityError(typeTerminalKeyErrorEnum: TypeTerminalKeyErrorEnum?, s: String?) {
        runOnUiThread {
            if (typeTerminalKeyErrorEnum == TypeTerminalKeyErrorEnum.PROCESS_VALIDATOR_CANCELLED) {
                terminalAdapter!!.hideErrorTextViewInfoSelected()
                terminalAdapter!!.hideLoadingUIComponents()
            } else {
                terminalAdapter!!.hideLoadingUIComponents()
                terminalAdapter!!.showErrorTextView(R.string.error_validator_terminal)
            }
            //deselecting terminal model selected
            try {
                terminalAdapter!!.uncheckRadioButtonSelected()
                TerminalListManager.resetSelectedTerminal()
            } catch (e: Exception) {
                ZLog.exception(677602, e)
            }
            terminalAdapter!!.updateTypeTerminalModelSelected(TypeTerminalKeyEnum.UNKNOWN)
            buttonFinishConfiguration!!.setText(R.string.button_terminal_configuration_finished)
            isCheckingTerminal = false
            terminalAdapter!!.setEnable(true)
        }
    }

}