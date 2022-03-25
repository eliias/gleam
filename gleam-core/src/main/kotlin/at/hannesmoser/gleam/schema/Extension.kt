package at.hannesmoser.gleam.schema

import org.apache.beam.sdk.schemas.Schema

interface Extension {
  fun deserialize(value: Schema.Options): Extension
  fun serialize(): Schema.Options
}
