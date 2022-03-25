package at.hannesmoser.gleam.schema.extensions.field

import at.hannesmoser.gleam.schema.ExtendedField
import at.hannesmoser.gleam.schema.schema
import org.apache.beam.sdk.schemas.Schema
import org.apache.beam.sdk.schemas.logicaltypes.EnumerationType
import org.apache.beam.sdk.values.Row

private const val EXTENSION_ID = "compress"

private val compressionAlgorithms = EnumerationType.create(
  "deflate",
  "gzip"
)

private val extensionSchema = schema {
  logicalField("algorithm", compressionAlgorithms)
  nullableField("level", Schema.FieldType.INT32)
}

fun ExtendedField.compress(
  algorithm: EnumerationType.Value = compressionAlgorithms.valueOf("deflate"),
  level: Int? = null
) {
  val options = Schema.Options.builder()
    .setOption(
      EXTENSION_ID,
      Row.withSchema(extensionSchema)
        .withFieldValue("algorithm", algorithm)
        .withFieldValue("level", level)
        .build()
    )
    .build()

  field = field.withOptions(options)
}
