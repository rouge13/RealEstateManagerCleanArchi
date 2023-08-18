package com.openclassrooms.realestatemanager

import com.openclassrooms.realestatemanager.ui.utils.Utils
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*
/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */

class UtilsTest {
    @Test
    fun convertDollarsToEuros_isCorrect() {
        val dollars = DOLLAR_VALUE
        val expected = EXPECTED_EURO_VALUE
        val result = Utils.convertDollarsToEuros(dollars)
        assertEquals(expected, result)
    }

    @Test
    fun convertEurosToDollars_isCorrect() {
        val euros = EURO_VALUE
        val expected = EXPECTED_DOLLAR_VALUE
        val result = Utils.convertEurosToDollars(euros)
        assertEquals(expected, result)
    }

    @Test
    fun todayDateFranceFormat_isCorrect() {
        val calendar = Calendar.getInstance(Locale.FRANCE)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val monthValue = calendar.get(Calendar.MONTH) + 1 // In java Calendar.MONTH is zero-based.
        val month = if (monthValue < 10) "0$monthValue" else monthValue.toString()
        val year = calendar.get(Calendar.YEAR)
        val expected = "${String.format("%02d", day)}/$month/${year}"
        val result = Utils.todayDateFranceFormat.format(Date())
        assertEquals(expected, result)
    }

    @Test
    fun todayDateUsaFormat_isCorrect() {
        val calendar = Calendar.getInstance(Locale.US)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val monthValue = calendar.get(Calendar.MONTH) + 1 // In java Calendar.MONTH is zero-based.
        val month = if (monthValue < 10) "0$monthValue" else monthValue.toString()
        val year = calendar.get(Calendar.YEAR)
        val expected = "${year}/$month/${String.format("%02d", day)}"
        val result = Utils.todayDateUsaFormat.format(Date())
        assertEquals(expected, result)
    }

    companion object {
        private const val DOLLAR_VALUE = 100
        private const val EXPECTED_EURO_VALUE = 93
        private const val EURO_VALUE = 100
        private const val EXPECTED_DOLLAR_VALUE = 107
    }
}