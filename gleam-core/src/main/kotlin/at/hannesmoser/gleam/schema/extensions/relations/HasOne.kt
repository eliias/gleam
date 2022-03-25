package at.hannesmoser.gleam.schema.extensions.relations

import at.hannesmoser.gleam.schema.ExtendedSchema
import at.hannesmoser.gleam.schema.extensions.ID
import at.hannesmoser.gleam.schema.extensions.KeyFn
import org.apache.beam.sdk.schemas.Schema

@Suppress("UnusedPrivateMember")
private const val EXTENSION_ID = "hasOne"

@Suppress("EmptyFunctionBlock", "UnusedPrivateMember")
fun ExtendedSchema.hasOne(
  name: String,
  schema: Schema,
  key: KeyFn = ID
) {
}
