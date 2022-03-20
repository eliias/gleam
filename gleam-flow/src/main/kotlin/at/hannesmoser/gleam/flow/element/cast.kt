package at.hannesmoser.gleam.flow.element

import org.apache.beam.sdk.schemas.Schema
import org.apache.beam.sdk.schemas.transforms.Cast
import org.apache.beam.sdk.values.PCollection
import org.apache.beam.sdk.values.Row

fun PCollection<Row>.cast(schema: Schema, validator: Cast.Validator) = apply(
  Cast.of(schema, validator)
)
