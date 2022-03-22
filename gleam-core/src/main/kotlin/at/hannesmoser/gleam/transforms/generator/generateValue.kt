package at.hannesmoser.gleam.transforms.generator

import io.github.serpro69.kfaker.Faker
import org.apache.beam.sdk.schemas.Schema
import org.apache.beam.sdk.schemas.Schema.FieldType
import org.apache.beam.sdk.schemas.logicaltypes.EnumerationType
import org.apache.beam.sdk.values.Row
import org.joda.time.Instant

internal const val AGGREGATE_LEN = 10

/**
 * Generates a value for a given field type
 */
@Suppress("UNCHECKED_CAST", "ReturnCount")
internal fun <T> generateValue(
  faker: Faker,
  type: FieldType
): T {
  if (type.typeName.isPrimitiveType) {
    return generatePrimitiveValue(faker, type) as T
  }

  if (type.typeName.isCollectionType) {
    return generateCollectionValue(faker, type) as T
  }

  if (type.typeName.isCompositeType) {
    return generateCompositeValue(faker, type) as T
  }

  if (type.typeName.isLogicalType) {
    return generateLogicalType(faker, type) as T
  }

  throw IllegalArgumentException("${type.typeName} is not supported")
}

private fun generatePrimitiveValue(faker: Faker, type: Schema.FieldType) =
  when (type.typeName) {
    Schema.TypeName.BOOLEAN -> faker.random.nextBoolean()
    Schema.TypeName.BYTE -> faker.random.nextInt(
      Byte.MIN_VALUE.toInt(),
      Byte.MAX_VALUE.toInt()
    ).toByte()
    Schema.TypeName.BYTES -> faker.artist.names().toByteArray()
    Schema.TypeName.DATETIME -> Instant.ofEpochMilli(
      faker.random.nextInt(
        Instant.EPOCH.millis.toInt(),
        Instant.now().millis.toInt()
      ).toLong()
    ).toDateTime()
    Schema.TypeName.DECIMAL -> faker.random.nextDouble().toBigDecimal()
    Schema.TypeName.DOUBLE -> faker.random.nextDouble()
    Schema.TypeName.FLOAT -> faker.random.nextFloat()
    Schema.TypeName.INT16 -> faker.random.nextInt(
      Short.MIN_VALUE.toInt(),
      Short.MAX_VALUE.toInt()
    ).toShort()
    Schema.TypeName.INT32 -> faker.random.nextInt()
    Schema.TypeName.INT64 -> faker.random.nextLong()
    Schema.TypeName.STRING -> faker.commerce.productName()
    else -> throw IllegalArgumentException("${type.typeName} is not a primitive type")
  }

@Suppress("IMPLICIT_CAST_TO_ANY")
private fun generateCollectionValue(faker: Faker, type: Schema.FieldType) =
  when (type.typeName) {
    Schema.TypeName.ARRAY -> {
      val len = faker.random.nextInt(0, AGGREGATE_LEN - 1)
      (0..len).map { faker.movie.title() }
    }
    Schema.TypeName.ITERABLE -> {
      val len = faker.random.nextInt(0, AGGREGATE_LEN - 1)
      (0..len).map { faker.animal.name() }
    }
    Schema.TypeName.MAP -> {
      val len = faker.random.nextInt(0, AGGREGATE_LEN - 1)
      (0..len).associate {
        Pair(
          faker.programmingLanguage.name(),
          faker.programmingLanguage.creator()
        )
      }
    }
    else -> throw IllegalArgumentException("${type.typeName} is not a collection type")
  }

private fun generateCompositeValue(faker: Faker, type: Schema.FieldType) =
  when (type.typeName) {
    Schema.TypeName.ROW -> Row.withSchema(type.rowSchema)
      .withFieldValues(generateFieldValues(faker, type.rowSchema!!))
      .build()
    else -> throw IllegalArgumentException("${type.typeName} is not a composite type")
  }

@Suppress("UnusedPrivateMember")
private fun generateLogicalType(faker: Faker, type: FieldType) =
  when (type.typeName) {
    Schema.TypeName.LOGICAL_TYPE -> EnumerationType.create(
      "RED",
      "GREEN",
      "BLUE"
    )
    else -> throw IllegalArgumentException("${type.typeName} is not a logical type")
  }
