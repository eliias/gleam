package at.hannesmoser.gleam.flow.element

import org.apache.beam.sdk.transforms.MapValues
import org.apache.beam.sdk.transforms.SerializableFunction
import org.apache.beam.sdk.values.KV
import org.apache.beam.sdk.values.PCollection
import org.apache.beam.sdk.values.TypeDescriptor

inline fun <K, V1, reified V2> PCollection<KV<K, V1>>.mapValues(fn: SerializableFunction<V1, V2>) =
  apply(
    MapValues
      .into(TypeDescriptor.of(V2::class.java))
      .via(fn)
  )
