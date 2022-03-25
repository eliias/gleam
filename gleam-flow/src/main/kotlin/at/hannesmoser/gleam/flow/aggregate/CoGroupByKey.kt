package at.hannesmoser.gleam.flow.aggregate

import org.apache.beam.sdk.transforms.join.CoGroupByKey
import org.apache.beam.sdk.transforms.join.KeyedPCollectionTuple

fun <T> KeyedPCollectionTuple<T>.coGroupByKey() = apply(CoGroupByKey.create())
