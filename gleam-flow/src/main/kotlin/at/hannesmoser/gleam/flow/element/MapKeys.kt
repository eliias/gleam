package at.hannesmoser.gleam.flow.element

import org.apache.beam.sdk.transforms.MapKeys
import org.apache.beam.sdk.transforms.SerializableFunction
import org.apache.beam.sdk.values.KV
import org.apache.beam.sdk.values.PCollection
import org.apache.beam.sdk.values.TypeDescriptor

inline fun <K1, reified K2, V> PCollection<KV<K1, V>>.mapKeys(fn: SerializableFunction<K1, K2>) =
  apply(
    MapKeys
      .into(TypeDescriptor.of(K2::class.java))
      .via(fn)
  )
