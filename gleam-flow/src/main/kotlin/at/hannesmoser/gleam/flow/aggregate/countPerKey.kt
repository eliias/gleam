package at.hannesmoser.gleam.flow.aggregate

import org.apache.beam.sdk.transforms.Count
import org.apache.beam.sdk.values.KV
import org.apache.beam.sdk.values.PCollection

fun <K, V> PCollection<KV<K, V>>.countPerKey(): PCollection<KV<K, Long>> =
  apply(Count.perKey())
