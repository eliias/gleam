package at.hannesmoser.gleam.flow.element

import org.apache.beam.sdk.schemas.transforms.Convert
import org.apache.beam.sdk.values.PCollection
import org.apache.beam.sdk.values.Row

fun <OutputT> PCollection<Row>.convertFromRows(clazz: Class<OutputT>) = apply(Convert.fromRows(clazz))
