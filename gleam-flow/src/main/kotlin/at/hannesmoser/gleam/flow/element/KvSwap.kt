package at.hannesmoser.gleam.flow.element

import org.apache.beam.sdk.transforms.KvSwap
import org.apache.beam.sdk.values.KV
import org.apache.beam.sdk.values.PCollection

fun <K, V> PCollection<KV<K, V>>.kvSwap() = apply(KvSwap.create())
