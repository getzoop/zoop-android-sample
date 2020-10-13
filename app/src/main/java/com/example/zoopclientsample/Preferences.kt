package com.example.zoopclientsample

import android.content.Context
import android.content.SharedPreferences

class Preferences(context: Context) {

    private val mSharedPreferences: SharedPreferences =
        context.getSharedPreferences("userData", Context.MODE_PRIVATE)

    fun storeString(key: String?, value: String?) {
        mSharedPreferences.edit().putString(key, value).apply()
    }

    fun getStoredString(key: String?): String? {
        return mSharedPreferences.getString(key, "")
    }

    fun clean() {
        mSharedPreferences.edit().clear().apply()
    }

}