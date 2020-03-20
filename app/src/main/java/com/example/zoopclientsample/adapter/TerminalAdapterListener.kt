package com.example.zoopclientsample.adapter

import com.example.zoopclientsample.model.TerminalModel

interface TerminalAdapterListener {
    fun terminalModelItemOnClick(model: TerminalModel?, position: Int)
}