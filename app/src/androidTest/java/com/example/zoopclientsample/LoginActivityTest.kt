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
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginActivityTest {

    @get:Rule
    val loginActivityTest = IntentsTestRule(LoginActivity::class.java)

    @Test
    fun givenInitialState_shouldShowEmailAndPasswordEmpty() {
        // arrange
        // act
        // assert
        onView(withId(R.id.editTextUsername))
            .check(matches(withText("")))
        onView(withId(R.id.editTextPassword))
            .check(matches(withText("")))
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
        onView(withText("O campo e-mail é obrigatório"))
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
        onView(withText("O campo senha é obrigatório"))
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
        onView(withText("Unauthorized"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun givenValidEmailAndPassword_whenLogin_shouldGoToMainActivity() {
        // arrange
        intending(hasComponent(MainActivity::class.java.name))
            .respondWith(Instrumentation.ActivityResult(Activity.RESULT_CANCELED, null))
        // act
        onView(withId(R.id.editTextUsername))
            .perform(typeText("test@zoop.com.br"))
        onView(withId(R.id.editTextPassword))
            .perform(typeText("123456"), closeSoftKeyboard())
        onView(withId(R.id.buttonLogin))
            .perform(click())
        // assert
        intended(hasComponent(MainActivity::class.java.name))
    }

}

