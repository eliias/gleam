package at.hannesmoser.gleam.flow.element

import org.apache.beam.sdk.transforms.Keys
import org.apache.beam.sdk.values.KV
import org.apache.beam.sdk.values.PCollection

fun <K, V> PCollection<KV<K, V>>.keys() = apply(Keys.create())
