package at.hannesmoser.gleam.schema

import org.apache.beam.sdk.schemas.Schema
import org.apache.beam.sdk.schemas.Schema.Field
import org.apache.beam.sdk.schemas.Schema.FieldType
import org.apache.beam.sdk.schemas.Schema.LogicalType

@SchemaMarker
@Suppress("TooManyFunctions")
class Root : ExtendedSchema(Schema.of()) {
  // basic field methods
  fun field(field: Field): Field {
    addField(field)
    return field
  }

  fun field(
    name: String,
    type: FieldType,
    init: ExtendedField.() -> Unit
  ): Field {
    val extendedField = ExtendedField(Field.of(name, type))
    extendedField.init()
    return field(extendedField.field)
  }

  fun field(name: String, type: FieldType) = field(name, type) {}

  fun fields(fields: List<Field>) = addFields(fields)
  fun fields(fields: List<Field>, init: ExtendedField.() -> Unit) = fields(
    fields.map {
      val extendedField = ExtendedField(it)
      extendedField.init()
      extendedField.field
    }
  )

  fun fields(vararg field: Field, init: ExtendedField.() -> Unit) =
    fields(field.toList(), init)

  fun fields(vararg field: Field) = fields(field.toList()) {}

  // nullable fields
  fun nullableField(field: Field) = field(field.withNullable(true))
  fun nullableField(name: String, type: FieldType) =
    nullableField(Field.of(name, type))

  // collection fields
  fun arrayField(name: String, elementType: FieldType) = field(
    Field.of(name, FieldType.array(elementType))
  )

  fun mapField(name: String, keyType: FieldType, valueType: FieldType) = field(
    Field.of(name, FieldType.map(keyType, valueType))
  )

  // composite fields
  fun rowField(name: String, schema: Schema) = field(
    Field.of(name, FieldType.row(schema))
  )

  fun rowField(name: String, init: Root.() -> Unit): Field {
    val schema = schema(init)
    return field(
      Field.of(name, FieldType.row(schema))
    )
  }

  // logical fields
  fun <InputT, BaseT> logicalField(
    name: String,
    logicalType: LogicalType<InputT, BaseT>
  ) = field(
    Field.of(name, FieldType.logicalType(logicalType))
  )

  private fun addField(field: Field) {
    schema = Schema.builder()
      .addFields(schema.fields)
      .addField(field)
      .build()
  }

  private fun addFields(fields: List<Field>) {
    schema = Schema.builder()
      .addFields(schema.fields)
      .addFields(fields)
      .build()
  }
}
