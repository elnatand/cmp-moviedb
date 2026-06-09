package com.elna.moviedb.core.ui.utils

import kotlin.test.Test
import kotlin.test.assertEquals

class DateFormatterTest {

    @Test
    fun `formatDate converts ISO date to dots format`() {
        assertEquals("25.12.2023", formatDate("2023-12-25"))
        assertEquals("01.01.2024", formatDate("2024-01-01"))
    }

    @Test
    fun `formatDate returns original string on invalid format`() {
        assertEquals("invalid-date", formatDate("invalid-date"))
        assertEquals("2023/12/25", formatDate("2023/12/25"))
    }

    @Test
    fun `formatDate handles empty string`() {
        assertEquals("", formatDate(""))
    }
}
