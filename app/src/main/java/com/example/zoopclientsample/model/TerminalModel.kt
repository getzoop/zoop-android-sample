package com.example.zoopclientsample.model

import com.zoop.zoopandroidsdk.commons.TypeTerminalKeyEnum
import org.json.JSONObject

class TerminalModel(
    val jsonTerminal: JSONObject
) {
    var name: String
    val dateTimeDetected: String
    var typeTerminalKeyEnum: TypeTerminalKeyEnum
    var selected: Boolean
    var showLoading: Boolean

    companion object {
        private const val NAME = "name"
        private const val DATE_TIME_DETECTED = "dateTimeDetected"
        private const val TYPE_TERMINAL = "typeTerminal"
    }

    init {
        name = jsonTerminal.getString(NAME)
        dateTimeDetected = jsonTerminal.getString(DATE_TIME_DETECTED)
        typeTerminalKeyEnum = TypeTerminalKeyEnum.values()[jsonTerminal.getInt(TYPE_TERMINAL)]
        selected = false
        showLoading = false
    }
}