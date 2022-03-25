package at.hannesmoser.gleam.flow.aggregate

import org.apache.beam.sdk.transforms.Count
import org.apache.beam.sdk.values.KV
import org.apache.beam.sdk.values.PCollection

fun <T> PCollection<T>.countPerElement(): PCollection<KV<T, Long>> =
  apply(Count.perElement())
