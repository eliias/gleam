package at.hannesmoser.gleam.flow.element

import org.apache.beam.sdk.transforms.MapElements
import org.apache.beam.sdk.transforms.SimpleFunction
import org.apache.beam.sdk.values.PCollection
import org.apache.beam.sdk.values.TypeDescriptor

inline fun <InputT, reified OutputT> PCollection<InputT>.map(
  crossinline fn: (value: InputT) -> OutputT
): PCollection<OutputT> =
  apply(
    MapElements
      .into(TypeDescriptor.of(OutputT::class.java))
      .via(object : SimpleFunction<InputT, OutputT>() {
        override fun apply(value: InputT) = fn(value)
      })
  )
