package at.hannesmoser.gleam.schema

import org.apache.beam.sdk.schemas.Schema

@ExtendedSchemaMarker
open class ExtendedSchema(var schema: Schema)
