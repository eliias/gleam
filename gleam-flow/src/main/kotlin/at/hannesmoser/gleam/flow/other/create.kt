package at.hannesmoser.gleam.flow.other

import org.apache.beam.sdk.Pipeline
import org.apache.beam.sdk.transforms.Create
import org.apache.beam.sdk.values.PCollection

inline fun <reified T> Pipeline.create(vararg element: T): PCollection<T> =
  apply(Create.of(element.first(), *element.drop(1).toTypedArray()))
