package at.hannesmoser.gleam.flow.other

import at.hannesmoser.gleam.dsl.transform
import org.apache.beam.sdk.transforms.PTransform
import org.apache.beam.sdk.values.PCollection
import org.apache.beam.sdk.values.PDone
import org.apache.beam.sdk.values.POutput

fun <T, OutputT : POutput?> PCollection<T>.sink(
  transform: PTransform<in PCollection<T>, OutputT>
): POutput = apply("sink", transform { input ->
  input.apply(transform)
  PDone.`in`(input.pipeline)
})
