package at.hannesmoser.gleam.transforms.generator

import io.github.serpro69.kfaker.Faker
import org.apache.beam.sdk.schemas.Schema
import org.apache.beam.sdk.schemas.logicaltypes.EnumerationType

internal fun schema(
  numFields: Int,
  allowedTypes: Array<Schema.TypeName>,
  depth: Int,
  maxDepth: Int
): Schema {
  val faker = Faker()
  faker.unique.configuration {
    enable(faker::ancient)
  }

  return Schema.builder()
    .addFields(
      (0 until numFields)
        .map {
          when (allowedTypes.random()) {
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
            Schema.TypeName.ARRAY -> Schema.FieldType.array(
              getFieldTypeForTypeName(
                allowedTypes
                  .filter { it.isPrimitiveType }
                  .random()
              )
            )
            Schema.TypeName.ITERABLE -> Schema.FieldType.array(
              getFieldTypeForTypeName(
                allowedTypes
                  .filter { it.isPrimitiveType }
                  .random()
              )
            )
            Schema.TypeName.MAP -> Schema.FieldType.map(
              getFieldTypeForTypeName(
                allowedTypes
                  .filter { it.isPrimitiveType }
                  .random()
              ),
              getFieldTypeForTypeName(
                allowedTypes
                  .filter { it.isPrimitiveType }
                  .random()
              )
            )
            Schema.TypeName.ROW -> {
              if (depth > maxDepth) {
                return@map null
              }
              Schema.FieldType.row(
                schema(
                  numFields,
                  allowedTypes,
                  depth + 1,
                  maxDepth
                )
              )
            }
            Schema.TypeName.LOGICAL_TYPE -> Schema.FieldType.logicalType(
              EnumerationType.create(
                (0..3).map { faker.witcher.characters() }
              )
            )
          }
        }
        .mapNotNull { Schema.Field.of(faker.ancient.god(), it) }
    )
    .build()
}
