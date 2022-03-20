package at.hannesmoser.gleam.flow.other

import at.hannesmoser.gleam.transforms.Generator
import org.apache.beam.sdk.Pipeline
import org.apache.beam.sdk.values.PCollection
import org.joda.time.Duration

fun Pipeline.sequence(
  from: Long = 1,
  numElements: Long = 1,
  periodLength: Duration = Duration.standardSeconds(1)
): PCollection<Long> = apply(Generator.sequence(from, numElements, periodLength))
