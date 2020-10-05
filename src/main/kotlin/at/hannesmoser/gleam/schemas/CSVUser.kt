package at.hannesmoser.gleam.schemas

import org.apache.beam.sdk.schemas.Schema

object CSVUser {
  val schema: Schema = Schema.builder()
    .addInt64Field("id")
    .addStringField("name")
    .addInt64Field("project_id")
    .build()
}
