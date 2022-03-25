package at.hannesmoser.gleam.schema.extensions

import at.hannesmoser.gleam.schema.ExtendedSchema
import org.apache.beam.sdk.schemas.Schema

private const val EXTENSION_ID = "version"

fun ExtendedSchema.version(version: String) {
  val options = Schema.Options.builder()
    .setOption(EXTENSION_ID, Schema.FieldType.STRING, version)
    .build()

  schema = schema.withOptions(options)
}
