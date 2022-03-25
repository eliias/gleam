package at.hannesmoser.gleam.flow.element

import org.apache.beam.sdk.coders.KvCoder
import org.apache.beam.sdk.transforms.PTransform
import org.apache.beam.sdk.transforms.WithKeys
import org.apache.beam.sdk.values.KV
import org.apache.beam.sdk.values.PCollection
import org.apache.beam.sdk.values.TypeDescriptor

fun <K, V> PCollection<V>.key(key: K): PCollection<KV<K, V>> = apply(WithKeys.of(key))

inline fun <reified K, V> PCollection<V>.key(noinline fn: (value: V) -> K): PCollection<KV<K, V>> = apply(
  "withKeys",
  object : PTransform<PCollection<V>, PCollection<KV<K, V>>>() {
    override fun expand(input: PCollection<V>): PCollection<KV<K, V>> = input
      .apply(WithKeys.of(fn))
      .setCoder(
        KvCoder.of(
          pipeline.coderRegistry.getCoder(TypeDescriptor.of(K::class.java)),
          input.coder
        )
      )
  }
)
