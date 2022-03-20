package at.hannesmoser.gleam.flow.aggregate

import org.apache.beam.sdk.schemas.transforms.Group
import org.apache.beam.sdk.values.KV
import org.apache.beam.sdk.values.PCollection

fun <K, V> PCollection<KV<K, V>>.groupByKey() = apply(Group.globally())
