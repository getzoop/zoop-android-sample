package com.example.zoopclientsample

import android.app.Activity
import android.app.Instrumentation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.zoopclientsample.view.LoginActivity
import com.example.zoopclientsample.view.MainActivity
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class LoginActivityTest {

    @get:Rule
    val loginActivityTest = IntentsTestRule(LoginActivity::class.java)

    @Before
    fun setUp() {
        val prefs = Preferences(loginActivityTest.activity.applicationContext)
        prefs.clean()
    }

    @After
    fun tearDown() {
        val prefs = Preferences(loginActivityTest.activity.applicationContext)
        prefs.clean()
    }

    @Test
    fun givenInitialState_shouldShowEmailAndPasswordEmpty() {
        // arrange
        // act
        // assert
        onView(withId(R.id.editTextUsername))
            .check(matches(withText("")))
        onView(withId(R.id.editTextPassword))
            .check(matches(withText("")))
        onView(withId(R.id.buttonLogin))
            .check(matches(withText(R.string.charge_button_login)))
    }

    @Test
    fun givenEmailIsEmpty_whenLogin_shouldShowEmptyEmailError() {
        // arrange
        // act
        onView(withId(R.id.editTextPassword))
            .perform(typeText("123456"), closeSoftKeyboard())
        onView(withId(R.id.buttonLogin))
            .perform(click())
        // assert
        onView(withText(R.string.username_error))
            .inRoot(withDecorView(not(loginActivityTest.activity.window.decorView)))
            .check(matches(isDisplayed()))
    }

    @Test
    fun givenPasswordIsEmpty_whenLogin_shouldShowEmptyPasswordError() {
        // arrange
        // act
        onView(withId(R.id.editTextUsername))
            .perform(typeText("test@zoop.com.br"), closeSoftKeyboard())
        onView(withId(R.id.buttonLogin))
            .perform(click())
        // assert
        onView(withText(R.string.password_error))
            .inRoot(withDecorView(not(loginActivityTest.activity.window.decorView)))
            .check(matches(isDisplayed()))
    }

    @Test
    fun givenInvalidEmailAndPassword_whenLogin_shouldShowUnathorizedError() {
        // arrange
        // act
        onView(withId(R.id.editTextUsername))
            .perform(typeText("test@zoop.com.br"))
        onView(withId(R.id.editTextPassword))
            .perform(typeText("123456"), closeSoftKeyboard())
        onView(withId(R.id.buttonLogin))
            .perform(click())
        // assert
        onView(withText(R.string.label_loading))
            .check(matches(isDisplayed()))
        onView(withText("Unauthorized"))
            .inRoot(withDecorView(not(loginActivityTest.activity.window.decorView)))
            .check(matches(isDisplayed()))
    }

    @Test
    fun givenValidEmailAndPassword_whenLogin_shouldGoToMainActivity() {
        // arrange
        intending(hasComponent(MainActivity::class.java.name))
            .respondWith(Instrumentation.ActivityResult(Activity.RESULT_CANCELED, null))
        // act
        onView(withId(R.id.editTextUsername))
            .perform(typeText("test@zoop.com.br"))  //replace with a valid email when testing
        onView(withId(R.id.editTextPassword))
            .perform(typeText("123456"), closeSoftKeyboard()) //replace with a valid password when testing
        onView(withId(R.id.buttonLogin))
            .perform(click())
        // assert
        onView(withText(R.string.label_loading))
            .check(matches(isDisplayed()))
        Thread.sleep(2000)
        intended(hasComponent(MainActivity::class.java.name))
    }

}

