package at.hannesmoser.gleam.flow.element

import org.apache.beam.sdk.schemas.transforms.Convert
import org.apache.beam.sdk.values.PCollection
import org.apache.beam.sdk.values.Row

fun PCollection<Row>.convertToPojo() = apply(Convert.toRows())
