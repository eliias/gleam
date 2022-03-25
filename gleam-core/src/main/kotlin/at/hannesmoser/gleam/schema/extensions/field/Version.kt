package at.hannesmoser.gleam.schema.extensions.field

import at.hannesmoser.gleam.schema.ExtendedField
import org.apache.beam.sdk.schemas.Schema

private const val EXTENSION_ID = "version"

fun ExtendedField.version(version: String) {
  val options = Schema.Options.builder()
    .setOption(EXTENSION_ID, Schema.FieldType.STRING, version)
    .build()

  field = field.withOptions(options)
}
