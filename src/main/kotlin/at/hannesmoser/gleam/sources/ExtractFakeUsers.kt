package at.hannesmoser.gleam.sources

import at.hannesmoser.gleam.entities.User
import io.github.serpro69.kfaker.Faker
import org.apache.beam.sdk.io.GenerateSequence
import org.apache.beam.sdk.schemas.transforms.Convert
import org.apache.beam.sdk.transforms.DoFn
import org.apache.beam.sdk.transforms.PTransform
import org.apache.beam.sdk.transforms.ParDo
import org.apache.beam.sdk.transforms.windowing.AfterPane
import org.apache.beam.sdk.transforms.windowing.FixedWindows
import org.apache.beam.sdk.transforms.windowing.Repeatedly
import org.apache.beam.sdk.transforms.windowing.Window
import org.apache.beam.sdk.values.PBegin
import org.apache.beam.sdk.values.PCollection
import org.apache.beam.sdk.values.Row
import org.joda.time.Duration

class ExtractFakeUsers : PTransform<PBegin, PCollection<Row>>() {
  override fun expand(input: PBegin): PCollection<Row> {
    return input
      .apply(
        "generate user IDs",
        GenerateSequence
          .from(1)
          .withRate(1, Duration.standardSeconds(1)))
      .apply("create fake user", ParDo.of(object : DoFn<Long, User>() {
        private lateinit var faker: Faker

        @Setup
        fun setup() {
          faker = Faker()
        }

        @ProcessElement
        fun process(context: ProcessContext, out: OutputReceiver<User>) {
          val user = User(context.element(), faker.name.name())

          out.output(user)
        }
      }))
      .apply("convert to rows", Convert.toRows())
      .apply("fixed window", Window
        .into<Row>(
          FixedWindows
            .of(Duration.standardSeconds(5))
        )
        .triggering(
          Repeatedly.forever(
            AfterPane.elementCountAtLeast(1)
          )
        )
        .withOnTimeBehavior(Window.OnTimeBehavior.FIRE_IF_NON_EMPTY)
        .withAllowedLateness(Duration.ZERO)
        .discardingFiredPanes()
      )
  }
}
