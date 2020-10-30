package com.zoop.zoopandroidsample

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.zoop.zoopandroidsample.view.ConfigPinpadActivity
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ConfigPinpadActivityTest {

    @get:Rule
    val configPinpadActivityTest = IntentsTestRule(ConfigPinpadActivity::class.java)

    @Before
    fun setUp() {
        //TODO: Error in bluetooth receiver because startTerminalDicovery method
    }

    @Test
    fun givenInitialState_shouldShowSearching() {
        // arrange
        // act
        // assert
        onView(withText(R.string.label_select_available_terminal))
            .check(matches(isDisplayed()))
        onView(withId(R.id.progressBarTerminalList))
            .check(matches(isDisplayed()))
        onView(withText(R.string.bluetooth_discovering_compatible_devices))
            .check(matches(isDisplayed()))
        onView(withId(R.id.listViewAvailableTerminals))
            .check(matches(isEnabled()))
        onView(withId(R.id.buttonFinishConfiguration))
            .check(matches(not(isDisplayed())))
    }

}