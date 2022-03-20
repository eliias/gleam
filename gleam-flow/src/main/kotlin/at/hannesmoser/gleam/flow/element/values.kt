package at.hannesmoser.gleam.flow.element

import org.apache.beam.sdk.transforms.Values
import org.apache.beam.sdk.values.KV
import org.apache.beam.sdk.values.PCollection

fun <K, V> PCollection<KV<K, V>>.values() = apply(Values.create())
