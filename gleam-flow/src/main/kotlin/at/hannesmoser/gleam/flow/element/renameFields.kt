package at.hannesmoser.gleam.flow.element

import org.apache.beam.sdk.schemas.transforms.RenameFields
import org.apache.beam.sdk.transforms.PTransform
import org.apache.beam.sdk.values.PCollection
import org.apache.beam.sdk.values.Row

fun PCollection<Row>.renameFields(
  configure: (
    renameFields: RenameFields.Inner<Row>
  ) -> RenameFields.Inner<Row> = { it }
) =
  apply(
    object : PTransform<PCollection<Row>, PCollection<Row>>() {
      override fun expand(input: PCollection<Row>) = input
        .apply(configure(RenameFields.create()))
    }
  )
