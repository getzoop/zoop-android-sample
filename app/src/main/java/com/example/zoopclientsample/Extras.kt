package com.example.zoopclientsample

import android.text.format.DateUtils
import org.json.JSONObject
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

        fun formatAmount(amount: String?): String {
            if (amount != null) {
                val cleanString = amount.toString().replace(Regex("[R$.,]"), "").trim()
                val parsed = cleanString.toDouble()
                return NumberFormat.getCurrencyInstance().format(parsed / 100)
            }
            return "-"
        }

        fun translatePaymentTypeInPortuguese(paymentType: String?): String {
            if (paymentType != null) {
                return when (paymentType.toUpperCase(Locale.ROOT)) {
                    Constants.CREDIT -> "CRÉDITO"
                    Constants.DEBIT -> "DÉBITO"
                    else -> "DESCONHECIDO"
                }
            }
            return "-"
        }

        fun formatNumberOfInstallments(numberOfInstallments: String?): String {
            if (numberOfInstallments != "null") {
                return "$numberOfInstallments X"
            }
            return "-"
        }

    }

}
