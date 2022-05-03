package com.zoop.zoopandroidsample.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.zoop.zoopandroidsample.*
import com.zoop.zoopandroidsample.api.*
import com.zoop.zoopandroidsdk.commons.ZLog
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : AppCompatActivity() {

    private var loginService: LoginService? = null
    private var toastHelper: ToastHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        toastHelper = ToastHelper(this)
        setupLoginButton()
    }

    private fun setupLoginButton() {
        val buttonLogin = findViewById<Button>(R.id.buttonLogin)
        buttonLogin?.let { button ->
            button.setOnClickListener {
                val editTextUsername = findViewById<EditText>(R.id.editTextUsername)
                val editTextPassword = findViewById<EditText>(R.id.editTextPassword)

                val sUsername = editTextUsername.text.toString()
                val sPassword = editTextPassword.text.toString()

                if (isUsernameValid(sUsername) && isPasswordValid(sPassword)) {
                    findViewById<LinearLayout>(R.id.llProgressBar).visibility = View.VISIBLE
                    executeLogin(sUsername, sPassword)
                }
            }
        }
    }

    private fun isUsernameValid(sUsername: String): Boolean {
        if (sUsername.isEmpty()) {
            toastHelper?.showToast(resources.getString(R.string.username_error))
            return false
        }
        return true
    }

    private fun isPasswordValid(sPassword: String): Boolean {
        if (sPassword.isEmpty()) {
            toastHelper?.showToast(resources.getString(R.string.password_error))
            return false
        }
        return true
    }

    private fun getLoginService(): LoginService? {
        if (loginService == null) {
            loginService = RetrofitInstance.retrofitInstance?.create(LoginService::class.java)
        }
        return loginService
    }

    private fun executeLogin(sUsername: String, sPassword: String) {
        val loginCall =
            getLoginService()!!.login(BuildConfig.marketplace_id, LoginRequest(sUsername, sPassword, true))
        loginCall!!.enqueue(object : Callback<LoginResponse?> {
            override fun onResponse(
                call: Call<LoginResponse?>,
                response: Response<LoginResponse?>
            ) {
                findViewById<LinearLayout>(R.id.llProgressBar).visibility = View.GONE
                try {
                    if (response.isSuccessful) {
                        val loginResponse: LoginResponse? = response.body()
                        val firstName = loginResponse?.firstName
                        val lastName = loginResponse?.lastName
                        val token = loginResponse?.token
                        val sellerId = getSellerId(loginResponse?.permissions)
                        if (sellerId.isEmpty()) {
                            toastHelper?.showToast(resources.getString(R.string.login_connection_error))
                        } else {
                            val prefs = Preferences(this@LoginActivity)
                            prefs
                                .storeString(Constants.USERNAME, sUsername)
                            prefs
                                .storeString(Constants.PASSWORD, sPassword)
                            prefs
                                .storeString(Constants.FIRST_NAME, firstName)
                            prefs
                                .storeString(Constants.LAST_NAME, lastName)
                            prefs
                                .storeString(Constants.USER_TOKEN, token)
                            prefs
                                .storeString(Constants.SELLER_ID, sellerId)
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        }

                    } else {
                        val error = response.errorBody().string()
                        val joError = JSONObject(error)
                        toastHelper?.showToast(joError.getString("errorMessage"))
                    }
                } catch (e: JSONException) {
                    ZLog.exception(300100, e)
                }
            }

            override fun onFailure(call: Call<LoginResponse?>?, t: Throwable?) {
                findViewById<LinearLayout>(R.id.llProgressBar).visibility = View.GONE
                ZLog.exception(300101, Exception(t))
                toastHelper?.showToast(resources.getString(R.string.login_connection_error))
            }
        })
    }

    private fun getSellerId(permissions: List<Permission>?): String {
        if (!permissions.isNullOrEmpty()) {
            for (permission in permissions) {
                if (permission.type == "model" &&
                    permission.modelName == "customers" &&
                    !permission.sellerId.startsWith("*")) {
                    return permission.sellerId
                }
            }
        }
        return ""
    }

    private fun callSeller(token: String, sellerId: String) {
        TODO("Not yet implemented")
    }

    private fun callSubscriptions(token: String, sellerId: String) {
        TODO("Not yet implemented")
    }

    private fun callPlan(token: String, sellerId: String) {
        TODO("Not yet implemented")
    }
}