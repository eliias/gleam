package at.hannesmoser.gleam.transforms.generator

import org.apache.beam.sdk.schemas.Schema

internal fun getFieldTypeForTypeName(typename: Schema.TypeName): Schema.FieldType {
  return when (typename) {
    Schema.TypeName.BOOLEAN -> Schema.FieldType.BOOLEAN
    Schema.TypeName.BYTE -> Schema.FieldType.BYTE
    Schema.TypeName.BYTES -> Schema.FieldType.BYTES
    Schema.TypeName.DATETIME -> Schema.FieldType.DATETIME
    Schema.TypeName.DECIMAL -> Schema.FieldType.DECIMAL
    Schema.TypeName.DOUBLE -> Schema.FieldType.DOUBLE
    Schema.TypeName.FLOAT -> Schema.FieldType.FLOAT
    Schema.TypeName.INT16 -> Schema.FieldType.INT16
    Schema.TypeName.INT32 -> Schema.FieldType.INT32
    Schema.TypeName.INT64 -> Schema.FieldType.INT64
    Schema.TypeName.STRING -> Schema.FieldType.STRING
    else -> throw IllegalArgumentException("`${typename.name}` not supported")
  }
}
