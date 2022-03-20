package at.hannesmoser.gleam.flow.element

import org.apache.beam.sdk.transforms.Filter
import org.apache.beam.sdk.transforms.SerializableFunction
import org.apache.beam.sdk.values.PCollection

fun <T> PCollection<T>.filter(fn: (value: T) -> Boolean): PCollection<T> =
  apply(Filter.by(SerializableFunction { fn(it) }))
