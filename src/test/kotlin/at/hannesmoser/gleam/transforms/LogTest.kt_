package at.hannesmoser.gleam.transforms

import org.apache.beam.sdk.testing.PAssert
import org.apache.beam.sdk.testing.TestPipeline
import org.apache.beam.sdk.transforms.Create
import org.junit.Rule
import org.junit.Test

internal class LogTest {
  @Rule
  @JvmField
  val pipeline: TestPipeline = TestPipeline.create()

  @Test
  fun testPassthrough() {
    val data = listOf("a", "b", "c")
    val result = pipeline
      .apply(Create.of(data))
      .apply(Log.info<String>("test message"))

    PAssert
      .that(result)
      .containsInAnyOrder("a", "b", "c")

    pipeline.run()
  }
}
