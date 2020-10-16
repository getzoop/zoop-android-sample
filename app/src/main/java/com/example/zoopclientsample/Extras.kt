package com.example.zoopclientsample

import android.text.format.DateUtils
import org.json.JSONObject
import java.math.BigDecimal
import java.math.MathContext
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class Extras {

    companion object {

        private fun getDateFromTimestampStringAtTimezone(sTimestamp: String): Date? {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sszzzzz", Locale.US)
            dateFormat.timeZone = TimeZone.getTimeZone("UTC")
            return dateFormat.parse(sTimestamp)
        }

        fun checkIfTransactionCanBeCancelled(joTransactionResponse: JSONObject?): Boolean {
            if (joTransactionResponse != null) {
                if (joTransactionResponse.getString("status").compareTo("succeeded") == 0) {
                    val sTransactionDateTime = joTransactionResponse.getString("created_at")
                    val transactionDate = getDateFromTimestampStringAtTimezone(sTransactionDateTime)
                    if (transactionDate != null) {
                        return DateUtils.isToday(transactionDate.time)
                    }
                }
            }
            return false
        }

        fun parseCurrencyFormatToBigDecimal(amount: String): BigDecimal {
            val cleanString = amount.replace(Regex("[R$.,]"), "").trim()
            val parsed = cleanString.toDouble()
            val result = BigDecimal(parsed / 100,  MathContext.DECIMAL64)
            return result.setScale(2, BigDecimal.ROUND_HALF_EVEN)
        }

        fun parseDoubleToCurrenyFormat(amount: String): String {
            val cleanString = amount.replace(Regex("[R$.,]"), "").trim()
            val parsed = cleanString.toDouble()
            return NumberFormat.getCurrencyInstance().format(parsed / 100)
        }

        fun translatePaymentType(paymentType: String): String {
            return when (paymentType.toUpperCase(Locale.ROOT)) {
                Constants.CREDIT -> "CRÉDITO"
                Constants.DEBIT -> "DÉBITO"
                else -> "DESCONHECIDO"
            }
        }

        fun formatNumberOfInstallments(numberOfInstallments: String): String {
            return "$numberOfInstallments x"
        }

    }

}
