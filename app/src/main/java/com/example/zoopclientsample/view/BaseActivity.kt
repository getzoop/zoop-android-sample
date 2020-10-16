package com.example.zoopclientsample.view

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import com.example.zoopclientsample.Constants
import com.example.zoopclientsample.Preferences
import com.example.zoopclientsample.R

abstract class BaseActivity : AppCompatActivity() {

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.options, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.home -> {
                startActivity(Intent(this, MainActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun getUserToken(): String? {
        return Preferences(this)
            .getStoredString(Constants.USER_TOKEN)
    }

    fun getSellerId(): String? {
        return Preferences(this)
            .getStoredString(Constants.SELLER_ID)
    }

    fun isLogged(): Boolean {
        val username = Preferences(this)
            .getStoredString(Constants.USERNAME)
        val password = Preferences(this)
            .getStoredString(Constants.PASSWORD)
        val token = Preferences(this)
            .getStoredString(Constants.USER_TOKEN)
        val sellerId = Preferences(this)
            .getStoredString(Constants.SELLER_ID)
        if (username.isNullOrEmpty() ||
            password.isNullOrEmpty() ||
            token.isNullOrEmpty() ||
            sellerId.isNullOrEmpty()) {
            return false
        }
        return true
    }

    fun logout() {
        Preferences(this).clean()
    }
}