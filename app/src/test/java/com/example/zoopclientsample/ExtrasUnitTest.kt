package com.example.zoopclientsample

import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal
import java.math.MathContext
import java.text.NumberFormat

class ExtrasUnitTest {

    @Test
    fun givenCurrency_whenExtras_shouldReturnBigDecimalWithDECIMAL64() {
        //arrange
        //act
        val result = Extras.parseCurrencyFormatToBigDecimal("R$ 1.234,56")
        //assert
        assertEquals(BigDecimal(1234.56,  MathContext.DECIMAL64).setScale(2, BigDecimal.ROUND_HALF_EVEN), result)
    }
    
    @Test
    fun givenDouble_whenExtras_shouldReturnCurrency() {
        // arrange
        // act
        val result = Extras.parseDoubleToCurrenyFormat("1234.56")
        // assert
        assertEquals(NumberFormat.getCurrencyInstance().format(1234.56), result)
    }

    @Test
    fun givenCredit_whenTranslatePaymentType_shouldReturnCredito() {
        // arrange
        // act
        val result = Extras.translatePaymentType("CrEdIt")
        // assert
        assertEquals("CRÉDITO", result)
    }

    @Test
    fun givenDebit_whenTranslatePaymentType_shouldReturnDebito() {
        // arrange
        // act
        val result = Extras.translatePaymentType("DeBiT")
        // assert
        assertEquals("DÉBITO", result)
    }

    @Test
    fun givenUnknown_whenTranslatePaymentType_shouldReturnDesconhecido() {
        // arrange
        // act
        val result = Extras.translatePaymentType("CrEdIt WiTh InStAlLmEnTs")
        // assert
        assertEquals("DESCONHECIDO", result)
    }


    @Test
    fun givenNumberOfInstallments_whenExtras_shouldReturnNumberOfInstallmentsX() {
        // arrange
        // act
        val result = Extras.formatNumberOfInstallments("6")
        // assert
        assertEquals("6 x", result)
    }

}