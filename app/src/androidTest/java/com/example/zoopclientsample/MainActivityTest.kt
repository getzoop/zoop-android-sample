package com.example.zoopclientsample

import android.app.Activity
import android.app.Instrumentation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.zoopclientsample.view.ChargeActivity
import com.example.zoopclientsample.view.ConfigPinpadActivity
import com.example.zoopclientsample.view.LoginActivity
import com.example.zoopclientsample.view.MainActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val mainActivityTest = IntentsTestRule(MainActivity::class.java)

    @Before
    fun setUp() {
        //TODO: sharedPreference store not working
        val prefs = Preferences(mainActivityTest.activity.applicationContext)
        prefs.storeString(Constants.USERNAME, "test@zoop.com.br")
        prefs.storeString(Constants.PASSWORD, "123456")
        prefs.storeString(Constants.USER_TOKEN, "Aa0Bb1Cc2Dd3Ee4Ff5Gg6Hh7Ii8Jj9Kk0Ll1Mm2Nn3Oo4Pp5")
        prefs.storeString(Constants.SELLER_ID, "a0b1c2d3e4f5g6h7i8j9k0l1m2n3o4p5")
    }

    @After
    fun tearDown() {
        val prefs = Preferences(mainActivityTest.activity.applicationContext)
        prefs.clean()
    }

    @Test
    fun givenInitialState_shouldShowWelcomeMessage() {
        // arrange
        // act
        // assert
        onView(withId(R.id.textViewWelcome))
            .check(matches(withText("Olá, test@zoop.com.br")))
    }

    @Test
    fun givenInitialState_whenSalesButtonClicked_shouldGoToChargeActivity()  {
        // arrange
        intending(hasComponent(ChargeActivity::class.java.name))
            .respondWith(Instrumentation.ActivityResult(Activity.RESULT_CANCELED, null))
        // act
        onView(withId(R.id.buttonSales))
            .perform(click())
        // assert
        intended(hasComponent(ChargeActivity::class.java.name))
    }

    @Test
    fun givenInitialState_whenTerminalsButtonClicked_shouldGoToConfigPinpadActivity() {
        // arrange
        intending(hasComponent(ConfigPinpadActivity::class.java.name))
            .respondWith(Instrumentation.ActivityResult(Activity.RESULT_CANCELED, null))
        // act
        onView(withId(R.id.buttonTerminals))
            .perform(click())
        // assert
        intended(hasComponent(ConfigPinpadActivity::class.java.name))
    }

    @Test
    fun givenInitialState_whenLogoutButtonClicked_shouldShowAlertDialog() {
        // arrange
        // act
        onView(withId(R.id.buttonLogout))
            .perform(click())
        // assert
        onView(withText(R.string.dialog_logout_message))
            .check(matches(isDisplayed()))
    }

    @Test
    fun givenPositiveResponse_whenLogout_shouldGoToLoginActivity() {
        // arrange
        intending(hasComponent(LoginActivity::class.java.name))
            .respondWith(Instrumentation.ActivityResult(Activity.RESULT_CANCELED, null))
        // act
        onView(withId(R.id.buttonLogout))
            .perform(click())
        onView(withText(R.string.label_yes))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
            .perform(click())
        // assert
        intended(hasComponent(LoginActivity::class.java.name))
    }
}