package at.hannesmoser.gleam.flow.other

import org.apache.beam.sdk.transforms.Flatten
import org.apache.beam.sdk.values.PCollectionList

fun <T> PCollectionList<T>.flatten() = apply(Flatten.pCollections())
