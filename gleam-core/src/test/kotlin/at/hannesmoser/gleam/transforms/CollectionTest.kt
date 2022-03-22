package at.hannesmoser.gleam.transforms

import io.github.serpro69.kfaker.Faker
import org.apache.beam.sdk.testing.PAssert
import org.apache.beam.sdk.testing.TestPipeline
import org.apache.beam.sdk.transforms.Create
import org.junit.Rule
import org.junit.Test


internal class CollectionTest {
  @Rule
  @JvmField
  val pipeline: TestPipeline = TestPipeline.create()

  @Test
  fun testReduce() {
    val faker = Faker()
    val data = (1..100).map { faker.color.name() }

    val result = pipeline
      .apply(Create.of(data))
      .apply(Collection.reduce { s, batch -> batch.add(s); batch })

    PAssert
      .that(result)
      .satisfies {
        assert(it.toList().isNotEmpty())
        return@satisfies null
      }

    pipeline.run()
  }
}
