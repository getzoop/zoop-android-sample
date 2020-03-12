package com.example.zoopclientsample

import android.os.Bundle
import android.view.View
import android.widget.*
import com.zoop.zoopandroidsdk.TerminalListManager
import com.zoop.zoopandroidsdk.commons.ZLog
import com.zoop.zoopandroidsdk.terminal.DeviceSelectionListener
import org.json.JSONObject
import java.util.*

class ConfigPinPadActivity : BaseActivity() , DeviceSelectionListener {

    private val TAG = ConfigPinPadActivity::class.java.simpleName

    var terminalListManager: TerminalListManager? = null
    var adapter: SimpleAdapter? = null
    var lv: ListView? = null
    var arrayListZoopTerminalsListForUI: ArrayList<HashMap<String, Any>>? =
        null
    var iSelectedDeviceIndex = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config_pinpad)
        lv = findViewById<ListView>(R.id.listViewAvailableTerminals)
        setupListView()
        observeListView()
        terminalListManager = TerminalListManager(this, applicationContext)
        terminalListManager!!.startTerminalsDiscovery()
    }

    private fun setupListView() {
        lv!!.choiceMode = ListView.CHOICE_MODE_SINGLE
        arrayListZoopTerminalsListForUI = ArrayList<HashMap<String, Any>>()
        adapter = SimpleAdapter(this,
            arrayListZoopTerminalsListForUI,
            R.layout.item_list_terminals,
            arrayOf("name", "dateTimeDetected", "selected"),
            intArrayOf(R.id.textViewTerminalName, R.id.textViewDateTimeDetected, R.id.radioButton))
        lv!!.adapter = adapter
    }

    private fun updateListView() {
        adapter = SimpleAdapter(this,
            arrayListZoopTerminalsListForUI,
            R.layout.item_list_terminals,
            arrayOf("name", "dateTimeDetected", "selected"),
            intArrayOf(R.id.textViewTerminalName, R.id.textViewDateTimeDetected, R.id.radioButton))

        //TODO: adapter.setViewBinder precisa??

        lv!!.adapter = adapter
    }

    private fun observeListView() {
        lv?.setOnItemClickListener { _, view, position, _ ->
            iSelectedDeviceIndex = position
            val rb = view.findViewById(R.id.radioButton) as RadioButton
            if (!rb.isChecked) {
                val hmSelectedDevice: HashMap<String, Any>? =
                    arrayListZoopTerminalsListForUI?.get(iSelectedDeviceIndex)
                val joZoopDeviceSelectedByClick = hmSelectedDevice?.get("joZoopDevice") as JSONObject
                terminalListManager?.requestZoopDeviceSelection(joZoopDeviceSelectedByClick)
            }
            Toast.makeText(this, "Terminal selected by click: $position", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        terminalListManager?.finishTerminalDiscovery()
        super.onDestroy()
    }

    override fun showDeviceListForUserSelection(
        vectorZoopTerminals: Vector<JSONObject>
    ) {
        try {
            val joSelectedZoopTerminalName =
                TerminalListManager.getCurrentSelectedZoopTerminal()?.getString("name")

            arrayListZoopTerminalsListForUI = ArrayList()
            for (joZoopTerminal in vectorZoopTerminals) {
                val hashMapZoopTerminalStringsForUI = HashMap<String, Any>()
                joZoopTerminal?.let{
                    val joZoopTerminalCurrentlyName = joZoopTerminal.getString("name")
                    hashMapZoopTerminalStringsForUI["joZoopDevice"] = joZoopTerminal
                    hashMapZoopTerminalStringsForUI["name"] = joZoopTerminalCurrentlyName
                    hashMapZoopTerminalStringsForUI["dateTimeDetected"] = joZoopTerminal.getString("dateTimeDetected")
                    hashMapZoopTerminalStringsForUI["selected"] = (joZoopTerminalCurrentlyName == joSelectedZoopTerminalName)
                }
                arrayListZoopTerminalsListForUI!!.add(hashMapZoopTerminalStringsForUI)
            }
            updateListView()
//                terminalListManager?.requestZoopDeviceSelection(vectorZoopTerminals[0])
        } catch (e: Exception) {
            ZLog.exception(300064, e)
        }
    }

    override fun updateDeviceListForUserSelection(
        joNewlyFoundZoopDevice: JSONObject?,
        vectorZoopTerminals: Vector<JSONObject?>?, iNewlyFoundDeviceIndex: Int
    ) {
        try {
            val hashMapZoopTerminalStringsForUI = HashMap<String, Any>()
            joNewlyFoundZoopDevice?.let{
                val joSelectedZoopTerminalName =
                    TerminalListManager.getCurrentSelectedZoopTerminal().getString("name")
                val joNewlyFoundZoopDeviceName = joNewlyFoundZoopDevice.getString("name")

                hashMapZoopTerminalStringsForUI["joZoopDevice"] = joNewlyFoundZoopDevice
                hashMapZoopTerminalStringsForUI["name"] = joNewlyFoundZoopDeviceName
                hashMapZoopTerminalStringsForUI["dateTimeDetected"] = joNewlyFoundZoopDevice.getString("dateTimeDetected")
                hashMapZoopTerminalStringsForUI["selected"] = (joNewlyFoundZoopDeviceName == joSelectedZoopTerminalName)
            }
            arrayListZoopTerminalsListForUI!!.add(hashMapZoopTerminalStringsForUI)
            adapter!!.notifyDataSetChanged()
//            terminalListManager?.requestZoopDeviceSelection(joNewlyFoundZoopDevice)
        } catch (e: Exception) {
            ZLog.exception(677541, e)
        }
    }

    override fun bluetoothIsNotEnabledNotification() {
        terminalListManager?.enableDeviceBluetoothAdapter()
    }

    override fun deviceSelectedResult(
        joZoopSelectedDevice: JSONObject,
        vectorAllAvailableZoopTerminals: Vector<JSONObject?>?,
        iSelectedDeviceIndex: Int
    ) {
        try {
//        val namePinpad: String = joZoopSelectedDevice.getString("name")
            findViewById<Button>(R.id.buttonFinishConfiguration).visibility = View.VISIBLE
            adapter!!.notifyDataSetChanged()
        } catch (e: Exception) {
            ZLog.exception(300056, e)
        }
    }


}