package com.joshai.nasajoshaichallenge

import com.joshai.nasajoshaichallenge.utils.DateFormatters
import org.junit.Assert.assertEquals
import org.junit.Test

class DateFormattersTest {

    @Test
    fun `apiFormatter parses date correctly`() {
        val dateString = "2023-10-27"
        val date = DateFormatters.apiFormatter.parse(dateString)
        val formatted = DateFormatters.apiFormatter.format(date!!)
        assertEquals(dateString, formatted)
    }

    @Test
    fun `selectedFormatter formats date correctly`() {
        val dateString = "2023-10-27"
        val date = DateFormatters.apiFormatter.parse(dateString)
        val formatted = DateFormatters.selectedFormatter.format(date!!)
        assertEquals("10/27/2023", formatted)
    }
}
