package at.hannesmoser.gleam.schemas

import org.apache.beam.sdk.schemas.Schema

object Name {
  val schema: Schema = Schema.builder()
    .addInt64Field("id")
    .addStringField("name")
    .build()
}
