package at.hannesmoser.gleam

import at.hannesmoser.gleam.fixtures.UserFixture
import kotlin.test.Test

class AppTest {
  @Test
  fun testAppHasAGreeting() {
    val users = UserFixture.create(10)
  }
}
