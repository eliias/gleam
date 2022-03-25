package at.hannesmoser.gleam.schema

import org.apache.beam.sdk.schemas.Schema

@ExtendedFieldMarker
class ExtendedField(var field: Schema.Field)
