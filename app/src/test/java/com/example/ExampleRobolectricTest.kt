package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("FlightClaim", appName)
  }

  @Test
  fun `verify compensation calculation`() {
    val result = com.example.legal.CompensationCalculator.calculate(
      delayHours = 2.0f,
      distanceCategory = com.example.data.model.FlightDistance.LONG,
      isCancelled = false,
      terminalExpensesNis = 85
    )
    assertEquals(3620, result.statutoryCompensationNis)
    assertEquals(3620 + 85, result.totalEntitlementNis)
  }
}
