package at.hannesmoser.gleam.schema

import org.apache.beam.sdk.schemas.Schema

fun schema(init: Root.() -> Unit): Schema {
  val root = Root()
  root.init()

  return root.schema
}
