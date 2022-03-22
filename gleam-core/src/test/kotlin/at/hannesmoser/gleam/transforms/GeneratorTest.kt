package at.hannesmoser.gleam.transforms

import at.hannesmoser.gleam.transforms.generator.Generator
import org.apache.beam.sdk.testing.TestPipeline
import org.junit.Rule
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class GeneratorTest {
  @Rule
  var pipeline: TestPipeline = TestPipeline.create()

  @Test
  fun schemaFieldCount() {
    val newSchema = Generator.schema(3)
    val expected = 3
    val actual = newSchema.fields.size
    Assertions.assertEquals(expected, actual)
  }
}
