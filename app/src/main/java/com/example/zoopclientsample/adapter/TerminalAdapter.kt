package com.example.zoopclientsample.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.zoopclientsample.R
import com.example.zoopclientsample.model.TerminalModel
import com.zoop.zoopandroidsdk.commons.TypeTerminalKeyEnum
import java.util.*

class TerminalAdapter(
    context: Context?,
    resource: Int,
    private val listener: TerminalAdapterListener
) :
    ArrayAdapter<TerminalModel>(context!!, resource) {
    private val layoutResource: Int = resource
    private var terminals: MutableList<TerminalModel> =
        ArrayList()
    private var terminalName: TextView? = null
    private var compatible: TextView? = null
    private var incompatible: TextView? = null
    private var dateTimeDetected: TextView? = null
    private var selected: RadioButton? = null
    private var itemViewSelected: View? = null
    private var itemView: View? = null
    private var positionSelected = 0
    private var resourceId = 0
    private var isEnable = true

    override fun getCount(): Int {
        return terminals.size
    }

    override fun getItem(position: Int): TerminalModel {
        return terminals[position]
    }

    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {

        itemView = convertView

        if (count > 0) {

            if (itemView == null) {
                val layoutInflater = LayoutInflater.from(context)
                itemView = layoutInflater.inflate(layoutResource, null)
            }

            val model: TerminalModel = getItem(position)

            if (isEnable) setOnClickListener(itemView, model, position)

            terminalName = textViewTerminalName
            compatible = textViewCompatible
            incompatible = textViewIncompatible
            dateTimeDetected = textViewDateTimeDetected
            selected = radioButton

            terminalName?.text = model.name

            if (dateTimeDetected != null) {
                dateTimeDetected!!.visibility = View.VISIBLE
                dateTimeDetected!!.text = model.dateTimeDetected
            }

            when {
                model.typeTerminalKeyEnum === TypeTerminalKeyEnum.KEY_INCOMPATIBLE -> {
                    incompatible!!.setText(R.string.terminal_incompatible_text)
                    incompatible!!.visibility = View.VISIBLE
                }
                model.typeTerminalKeyEnum === TypeTerminalKeyEnum.KEY_COMPATIBLE -> {
                    compatible!!.setText(R.string.terminal_compatible_text)
                    compatible!!.visibility = View.VISIBLE
                }
                model.typeTerminalKeyEnum === TypeTerminalKeyEnum.KEY_PARTIALLY_COMPATIBLE -> {
                    compatible!!.setText(R.string.terminal_partially_compatible_text)
                    compatible!!.visibility = View.VISIBLE
                }
            }

            if (selected != null) {
                selected!!.visibility = View.VISIBLE
                selected!!.isChecked = model.selected
                if (model.showLoading) showProgressbarContainer() else hideProgressbarContainer()
            }

        }

        return itemView!!
    }

    fun setTerminalList(terminals: MutableList<TerminalModel>) {
        this.terminals.clear()
        this.terminals = terminals
        notifyDataSetChanged()
    }

    fun addTerminal(terminal: TerminalModel) {
        terminals.add(terminal)
        notifyDataSetChanged()
    }

    fun updateTypeTerminalModelSelected(typeTerminalKeyEnum: TypeTerminalKeyEnum?) {
        if (typeTerminalKeyEnum != null) {
            terminalModelSelected.typeTerminalKeyEnum = typeTerminalKeyEnum
        }
    }

    fun checkRadioButton() {
        deselectedAllRadiosButton()
        checkRadioButtonSelected()
    }

    fun showLoadingWithTextInfo(resourceId: Int) {
        this.resourceId = resourceId
        resetLoadingToModels()
        terminalModelSelected.showLoading = true
        notifyDataSetChanged()
    }

    fun hideLoadingUIComponents() {
        resetLoadingToModels()
        notifyDataSetChanged()
    }

    fun showErrorTextView(resourceId: Int) {
        val tvCompatible = itemViewSelected!!.findViewById<TextView>(R.id.textViewCompatible)
        tvCompatible.visibility = View.GONE
        val tvIncompatible = itemViewSelected!!.findViewById<TextView>(R.id.textViewIncompatible)
        tvIncompatible.text = itemViewSelected!!.context.getText(resourceId)
        tvIncompatible.visibility = View.VISIBLE
    }

    fun setEnable(isEnable: Boolean) {
        this.isEnable = isEnable
    }

    private fun setOnClickListener(
        view: View?,
        model: TerminalModel,
        position: Int
    ) {
        view!!.setOnClickListener(View.OnClickListener { v ->
            if (!isEnable) return@OnClickListener
            if (radioButtonIsChecked(v)) return@OnClickListener
            if (itemViewSelected != null) hideUIComponentsPreviousItemTerminalSelected()
            positionSelected = position
            itemViewSelected = v
            listener.terminalModelItemOnClick(model, position)
        })
    }

    private fun findViewById(resource: Int): View {
        return itemView!!.findViewById(resource)
    }

    private fun findViewByIdFromItemViewSelected(resource: Int): View {
        return itemViewSelected!!.findViewById(resource)
    }

    private val terminalModelSelected: TerminalModel
        get() = terminals[positionSelected]

    private val textViewTerminalName: TextView
        get() = findViewById(R.id.textViewTerminalName) as TextView

    private val textViewDateTimeDetected: TextView
        get() = findViewById(R.id.textViewDateTimeDetected) as TextView

    private val textViewCompatible: TextView
        get() = findViewById(R.id.textViewCompatible) as TextView

    private val textViewCompatibleSelected: TextView
        get() = findViewByIdFromItemViewSelected(R.id.textViewCompatible) as TextView

    private val textViewIncompatible: TextView
        get() = findViewById(R.id.textViewIncompatible) as TextView

    private val textViewIncompatibleSelected: TextView
        get() = findViewByIdFromItemViewSelected(R.id.textViewIncompatible) as TextView

    private val radioButton: RadioButton
        get() = findViewById(R.id.radioButton) as RadioButton

    private val progressBarFromView: ProgressBar
        get() = findViewById(R.id.progress_horizontal) as ProgressBar

    private val progressBarFromViewSelected: ProgressBar
        get() = findViewByIdFromItemViewSelected(R.id.progress_horizontal) as ProgressBar

    private fun getRadioButton(view: View): RadioButton {
        return view.findViewById<View>(R.id.radioButton) as RadioButton
    }

    private fun radioButtonIsChecked(view: View): Boolean {
        val radioButton = getRadioButton(view)
        return radioButton.isChecked
    }

    private fun showProgressbarContainer() {
        progressBarFromView.visibility = View.VISIBLE
    }

    private fun hideProgressbarContainer() {
        progressBarFromView.visibility = View.GONE
    }

    private fun hideProgressbarContainerSelected() {
        progressBarFromViewSelected.visibility = View.GONE
    }

    private val contextFromViewSelected: Context
        get() = itemView!!.context

    private fun hideUIComponentsPreviousItemTerminalSelected() {
        hideIsZoopTerminalTextViewInfoSelected()
        hideErrorTextViewInfoSelected()
        hideProgressbarContainerSelected()
        terminalModelSelected.showLoading = false
    }

    private fun hideIsZoopTerminalTextViewInfoSelected() {
        textViewCompatibleSelected.visibility = View.GONE
    }

    fun hideErrorTextViewInfoSelected() {
        textViewIncompatibleSelected.visibility = View.GONE
    }

    private fun deselectedAllRadiosButton() {
        for (model in terminals) {
            model.selected = false
        }
    }

    private fun checkRadioButtonSelected() {
        val modelSelected: TerminalModel = terminalModelSelected
        modelSelected.selected = true
    }

    fun uncheckRadioButtonSelected() {
        val modelSelected: TerminalModel = terminalModelSelected
        modelSelected.selected = false
    }

    private fun resetLoadingToModels() {
        for (terminalModel in terminals) {
            terminalModel.showLoading = false
        }
    }

}