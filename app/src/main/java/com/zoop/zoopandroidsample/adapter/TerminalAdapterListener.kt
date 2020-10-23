package com.zoop.zoopandroidsample.adapter

import com.zoop.zoopandroidsample.model.TerminalModel

interface TerminalAdapterListener {
    fun terminalModelItemOnClick(model: TerminalModel?, position: Int)
}