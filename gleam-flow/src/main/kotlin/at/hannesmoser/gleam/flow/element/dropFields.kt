package at.hannesmoser.gleam.flow.element

import org.apache.beam.sdk.schemas.transforms.DropFields
import org.apache.beam.sdk.values.PCollection
import org.apache.beam.sdk.values.Row

fun PCollection<Row>.dropFields(vararg fields: String) = apply(
  DropFields.fields(*fields)
)
