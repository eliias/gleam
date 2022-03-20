package at.hannesmoser.gleam.flow.aggregate

import org.apache.beam.sdk.transforms.Count
import org.apache.beam.sdk.values.PCollection

fun <T> PCollection<T>.count(): PCollection<Long> =
  apply(Count.globally())
