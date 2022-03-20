package at.hannesmoser.gleam.samples.g02_dsl

import at.hannesmoser.gleam.flow.aggregate.groupByKey
import at.hannesmoser.gleam.flow.element.filter
import at.hannesmoser.gleam.flow.element.key
import at.hannesmoser.gleam.flow.element.map
import at.hannesmoser.gleam.flow.other.sequence
import at.hannesmoser.gleam.flow.other.sink
import at.hannesmoser.gleam.flow.other.window
import at.hannesmoser.gleam.transforms.Log
import org.apache.beam.sdk.Pipeline
import org.apache.beam.sdk.transforms.windowing.FixedWindows
import org.joda.time.Duration

object Pipeline {
  @JvmStatic
  fun main(args: Array<String>) {
    val pipeline = Pipeline.create()

    val window = FixedWindows.of(Duration.standardSeconds(1))

    pipeline
      .sequence()
      .window(window)
      .map { it * 2 }
      .filter { it % 2 == 0L }
      .key { it / 10 }
      .groupByKey()
      .sink(Log.info())

    pipeline.run()
  }
}
