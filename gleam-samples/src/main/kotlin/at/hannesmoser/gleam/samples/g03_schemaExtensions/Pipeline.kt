@file:Suppress("PackageNaming")

package at.hannesmoser.gleam.samples.g03_schemaExtensions

import at.hannesmoser.gleam.schema.extensions.createInt64KeyFn
import at.hannesmoser.gleam.schema.extensions.field.compress
import at.hannesmoser.gleam.schema.extensions.field.version
import at.hannesmoser.gleam.schema.extensions.relations.hasMany
import at.hannesmoser.gleam.schema.extensions.relations.hasOne
import at.hannesmoser.gleam.schema.extensions.version
import at.hannesmoser.gleam.schema.schema
import org.apache.beam.sdk.schemas.Schema
import org.apache.beam.sdk.schemas.Schema.Field
import org.apache.beam.sdk.schemas.Schema.FieldType.INT64
import org.apache.beam.sdk.schemas.Schema.FieldType.STRING
import org.apache.beam.sdk.schemas.logicaltypes.EnumerationType
import org.apache.beam.sdk.values.Row

object Pipeline {
  @JvmStatic
  fun main(args: Array<String>) {
    val stateEnumType = EnumerationType.create(
      "draft",
      "published"
    )

    val detailSchema = Schema.of()
    val variantSchema = Schema.of()

    val schema = schema {
      version("1.0")

      // base
      field("id", INT64.withNullable(true)) {
        // adds the "version" extension
        version("1.0")
      }
      fields(
        Field.of("title", STRING),
        Field.of("description", STRING)
      ) {
        // adds the "compress" extension to all fields!
        compress()
      }

      // nullable
      nullableField("shop_id", INT64)

      // collection
      arrayField("country_codes", STRING)
      mapField(
        "user_permissions",
        INT64,
        STRING
      )

      // composite
      rowField("user_profile") {
        field("id", INT64)
        field("username", STRING)
      }

      // logical
      logicalField("state", stateEnumType)

      // relations
      hasOne("detail", detailSchema, createInt64KeyFn("user_id"))
      hasMany("variants", variantSchema, createInt64KeyFn("user_id"))
    }

    println(schema)

    val row = Row.withSchema(schema)
      .withFieldValue("id", 1L)
      .withFieldValue("title", "Blue Jean")
      .withFieldValue("description", "Lorem ipsum something…")
      .withFieldValue("shop_id", null)
      .withFieldValue("country_codes", listOf("de_AT", "en_EN"))
      .withFieldValue(
        "user_permissions",
        mapOf(
          1L to "read,write",
        )
      )
      .withFieldValue(
        "user_profile",
        Row.withSchema(schema.getField("user_profile").type.rowSchema)
          .withFieldValue("id", 1L)
          .withFieldValue("username", "eliias")
          .build()
      )
      .withFieldValue("state", stateEnumType.valueOf("published"))
      .build()

    println(row)
  }
}
