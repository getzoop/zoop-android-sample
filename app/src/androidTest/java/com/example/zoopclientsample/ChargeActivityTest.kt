package com.example.zoopclientsample

import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.zoopclientsample.view.ChargeActivity
import org.hamcrest.Matchers.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class ChargeActivityTest {

    @get:Rule
    val chargeActivityTest = IntentsTestRule(ChargeActivity::class.java)

    @Test
    fun givenInitialState_shouldShowDefaultValues() {
        // arrange
        // act
        // assert
        onView(withId(R.id.editTextValueToCharge))
            .check(matches(withText("")))
        onView(withId(R.id.buttonCreditOnly))
            .check(matches(isDisplayed()))
        onView(withId(R.id.buttonCreditWithInstallments))
            .check(matches(isDisplayed()))
        onView(withId(R.id.buttonDebit))
            .check(matches(isDisplayed()))
        onView(withId(R.id.spinnerNumberOfInstallments))
            .check(matches(isDisplayed()))
        onView(withId(R.id.buttonAction))
            .check(matches(withText(R.string.charge_button_pay_label)))
    }

    @Test
    fun givenValueIsEmpty_whenCharge_shouldShowEmptyValueError() {
        // arrange
        // act
        onView(withId(R.id.buttonAction))
            .perform(click())
        // assert
        onView(withText(R.string.value_error))
            .inRoot(withDecorView(not(chargeActivityTest.activity.window.decorView)))
            .check(matches(isDisplayed()))
    }

    @Test
    fun givenCredit_whenCharge_shouldDisableNumberOfInstallments() {
        // arrange
        // act
        onView(withId(R.id.editTextValueToCharge))
            .perform(typeText("1050"), closeSoftKeyboard())
        onView(withId(R.id.buttonCreditOnly))
            .perform(click())
        // assert
        onView(withId(R.id.spinnerNumberOfInstallments))
            .check(matches(not(isEnabled())))
    }


    @Test
    fun givenDebit_whenCharge_shouldDisableNumberOfInstallments() {
        // arrange
        // act
        onView(withId(R.id.editTextValueToCharge))
            .perform(typeText("23000"), closeSoftKeyboard())
        onView(withId(R.id.buttonDebit))
            .perform(click())
        // assert
        onView(withId(R.id.spinnerNumberOfInstallments))
            .check(matches(not(isEnabled())))
    }

    @Test
    fun givenCreditWithInstallments_whenCharge_shouldEnableNumberOfInstallments() {
        // arrange
        // act
        onView(withId(R.id.editTextValueToCharge))
            .perform(typeText("120000"), closeSoftKeyboard())
        onView(withId(R.id.buttonCreditWithInstallments))
            .perform(click())
        // assert
        onView(withId(R.id.spinnerNumberOfInstallments))
            .check(matches(isEnabled()))
    }

    @Test
    fun givenCredit_whenCharge_shouldShowCancelButton() {
        // arrange
        // act
        onView(withId(R.id.editTextValueToCharge))
            .perform(typeText("123456"), closeSoftKeyboard())
        onView(withId(R.id.buttonCreditOnly))
            .perform(click())
        onView(withId(R.id.buttonAction))
            .perform(click())
        // assert
        onView(withId(R.id.buttonAction))
            .check(matches(withText(R.string.label_cancel)))
    }

    @Test
    fun givenDebit_whenCharge_shouldShowCancelButton() {
        // arrange
        // act
        onView(withId(R.id.editTextValueToCharge))
            .perform(typeText("123"), closeSoftKeyboard())
        onView(withId(R.id.buttonDebit))
            .perform(click())
        onView(withId(R.id.buttonAction))
            .perform(click())
        // assert
        onView(withId(R.id.buttonAction))
            .check(matches(withText(R.string.label_cancel)))
    }

    @Test
    fun givenCreditWithInstallments_whenCharge_shouldShowCancelButton() {
        // arrange
        // act
        onView(withId(R.id.editTextValueToCharge))
            .perform(typeText("1234567890"), closeSoftKeyboard())
        onView(withId(R.id.buttonCreditWithInstallments))
            .perform(click())
        onView(withId(R.id.spinnerNumberOfInstallments))
            .perform(click())
        onData(allOf(`is`(instanceOf(String::class.java))))
            .atPosition(4)
            .perform(click())
        onView(withId(R.id.buttonAction))
            .perform(click())
        // assert
        onView(withId(R.id.buttonAction))
            .check(matches(withText(R.string.label_cancel)))
    }
}
