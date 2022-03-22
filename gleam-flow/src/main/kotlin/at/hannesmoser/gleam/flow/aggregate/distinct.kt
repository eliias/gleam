package at.hannesmoser.gleam.flow.aggregate

import org.apache.beam.sdk.transforms.Distinct
import org.apache.beam.sdk.values.PCollection

fun <T> PCollection<T>.distinct(): PCollection<T> = apply(Distinct.create())
