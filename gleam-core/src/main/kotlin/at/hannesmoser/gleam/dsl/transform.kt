package at.hannesmoser.gleam.dsl

import org.apache.beam.sdk.transforms.PTransform
import org.apache.beam.sdk.values.PCollection
import org.apache.beam.sdk.values.POutput

fun <T> transform(apply: (input: PCollection<T>) -> POutput) =
  object : PTransform<PCollection<T>, POutput>() {
    override fun expand(input: PCollection<T>) = apply(input)
  }
