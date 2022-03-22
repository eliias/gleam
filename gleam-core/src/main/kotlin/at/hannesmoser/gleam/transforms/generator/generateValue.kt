package at.hannesmoser.gleam.transforms.generator

import io.github.serpro69.kfaker.Faker
import org.apache.beam.sdk.schemas.Schema
import org.apache.beam.sdk.schemas.logicaltypes.EnumerationType
import org.apache.beam.sdk.values.Row
import org.joda.time.Instant

/**
 * Generates a value for a given field type
 */
@Suppress("IMPLICIT_CAST_TO_ANY", "UNCHECKED_CAST")
internal fun <T> generateValue(
  faker: Faker,
  type: Schema.FieldType
) = when (type.typeName) {
  // scalar types
  Schema.TypeName.BOOLEAN -> faker.random.nextBoolean()
  Schema.TypeName.BYTE -> faker.random.nextInt(
    Byte.MIN_VALUE.toInt(),
    Byte.MAX_VALUE.toInt()
  ).toByte()
  Schema.TypeName.BYTES -> faker.artist.names().toByteArray()
  Schema.TypeName.DATETIME -> Instant.ofEpochSecond(
    faker.random.nextInt(
      (Instant.EPOCH.millis / 1000).toInt(),
      (Instant.now().millis / 1000).toInt()
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

  // aggregate types
  Schema.TypeName.ARRAY -> {
    val len = faker.random.nextInt(0, 10 - 1)
    (0..len).map { faker.movie.title() }
  }
  Schema.TypeName.ITERABLE -> {
    val len = faker.random.nextInt(0, 10 - 1)
    (0..len).map { faker.animal.name() }
  }
  Schema.TypeName.MAP -> {
    val len = faker.random.nextInt(0, 10 - 1)
    (0..len).associate {
      Pair(
        faker.programmingLanguage.name(),
        faker.programmingLanguage.creator()
      )
    }
  }

  // complex types
  Schema.TypeName.ROW -> Row.withSchema(type.rowSchema)
    .withFieldValues(generateFieldValues(faker, type.rowSchema!!))
    .build()

  Schema.TypeName.LOGICAL_TYPE -> EnumerationType.create(
    "RED",
    "GREEN",
    "BLUE"
  )

  // catch the case of Java theoretically allowing null values for typeName
  null -> throw IllegalArgumentException("type.typeName is null")
} as T
