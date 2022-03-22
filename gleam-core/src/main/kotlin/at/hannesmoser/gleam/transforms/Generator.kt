package at.hannesmoser.gleam.transforms

import io.github.serpro69.kfaker.Faker
import org.apache.beam.sdk.coders.*
import org.apache.beam.sdk.io.GenerateSequence
import org.apache.beam.sdk.schemas.Schema
import org.apache.beam.sdk.schemas.Schema.*
import org.apache.beam.sdk.schemas.logicaltypes.EnumerationType
import org.apache.beam.sdk.transforms.DoFn
import org.apache.beam.sdk.transforms.PTransform
import org.apache.beam.sdk.transforms.ParDo
import org.apache.beam.sdk.values.PBegin
import org.apache.beam.sdk.values.PCollection
import org.apache.beam.sdk.values.Row
import org.joda.time.Duration
import org.joda.time.Instant

object Generator {
  /**
   * Generates a value for a given field type
   */
  private fun <T> generateValue(
    faker: Faker,
    type: FieldType
  ) = when (type.typeName) {
    // scalar types
    TypeName.BOOLEAN -> faker.random.nextBoolean()
    TypeName.BYTE -> faker.random.nextInt(
      Byte.MIN_VALUE.toInt(),
      Byte.MAX_VALUE.toInt()
    ).toByte()
    TypeName.BYTES -> faker.artist.names().toByteArray()
    TypeName.DATETIME -> Instant.ofEpochSecond(
      faker.random.nextInt(
        (Instant.EPOCH.millis / 1000).toInt(),
        (Instant.now().millis / 1000).toInt()
      ).toLong()
    ).toDateTime()
    TypeName.DECIMAL -> faker.random.nextDouble().toBigDecimal()
    TypeName.DOUBLE -> faker.random.nextDouble()
    TypeName.FLOAT -> faker.random.nextFloat()
    TypeName.INT16 -> faker.random.nextInt(
      Short.MIN_VALUE.toInt(),
      Short.MAX_VALUE.toInt()
    ).toShort()
    TypeName.INT32 -> faker.random.nextInt()
    TypeName.INT64 -> faker.random.nextLong()
    TypeName.STRING -> faker.commerce.productName()

    // aggregate types
    TypeName.ARRAY -> {
      val len = faker.random.nextInt(0, 10 - 1)
      (0..len).map { faker.movie.title() }
    }
    TypeName.ITERABLE -> {
      val len = faker.random.nextInt(0, 10 - 1)
      (0..len).map { faker.animal.name() }
    }
    TypeName.MAP -> {
      val len = faker.random.nextInt(0, 10 - 1)
      (0..len).associate {
        Pair(
          faker.programmingLanguage.name(),
          faker.programmingLanguage.creator()
        )
      }
    }

    // complex types
    TypeName.ROW -> Row.withSchema(type.rowSchema)
      .withFieldValues(
        generateFieldValues(faker, type.rowSchema!!)
      )
      .build()

    TypeName.LOGICAL_TYPE -> EnumerationType.create(
      "RED",
      "GREEN",
      "BLUE"
    )

    // catch the case of Java theoretically allowing null values for typeName
    null -> throw IllegalArgumentException("type.typeName is null")
  } as T

  /**
   * Recursively creates fake data for the given schema
   */
  private fun <T> generateFieldValues(
    faker: Faker,
    schema: Schema
  ): Map<String, T> {
    return schema.fields.associate {
      it.name to generateValue(faker, it.type)
    }
  }

  private val ALL_TYPENAMES = arrayOf(
    // scalar types
    TypeName.BOOLEAN,
    TypeName.BYTE,
    TypeName.BYTES,
    TypeName.DATETIME,
    TypeName.DECIMAL,
    TypeName.DOUBLE,
    TypeName.FLOAT,
    TypeName.INT16,
    TypeName.INT32,
    TypeName.INT64,
    TypeName.STRING,

    // aggregate types
    TypeName.ARRAY,
    TypeName.ITERABLE,
    TypeName.MAP,

    // complex types
    TypeName.ROW,
    TypeName.LOGICAL_TYPE
  )

  private fun getFieldTypeForTypeName(typename: TypeName): FieldType {
    return when (typename) {
      TypeName.BOOLEAN -> FieldType.BOOLEAN
      TypeName.BYTE -> FieldType.BYTE
      TypeName.BYTES -> FieldType.BYTES
      TypeName.DATETIME -> FieldType.DATETIME
      TypeName.DECIMAL -> FieldType.DECIMAL
      TypeName.DOUBLE -> FieldType.DOUBLE
      TypeName.FLOAT -> FieldType.FLOAT
      TypeName.INT16 -> FieldType.INT16
      TypeName.INT32 -> FieldType.INT32
      TypeName.INT64 -> FieldType.INT64
      TypeName.STRING -> FieldType.STRING
      else -> throw IllegalArgumentException("`${typename.name}` not supported")
    }
  }

  fun schema(
    numFields: Int = 3,
    allowedTypes: Array<TypeName> = ALL_TYPENAMES,
    depth: Int = 1,
    maxDepth: Int = 2
  ): Schema {
    val faker = Faker()
    faker.unique.configuration {
      enable(faker::ancient)
    }

    return builder()
      .addFields(
        (0 until numFields)
          .map {
            when (allowedTypes.random()) {
              TypeName.BOOLEAN -> FieldType.BOOLEAN
              TypeName.BYTE -> FieldType.BYTE
              TypeName.BYTES -> FieldType.BYTES
              TypeName.DATETIME -> FieldType.DATETIME
              TypeName.DECIMAL -> FieldType.DECIMAL
              TypeName.DOUBLE -> FieldType.DOUBLE
              TypeName.FLOAT -> FieldType.FLOAT
              TypeName.INT16 -> FieldType.INT16
              TypeName.INT32 -> FieldType.INT32
              TypeName.INT64 -> FieldType.INT64
              TypeName.STRING -> FieldType.STRING
              TypeName.ARRAY -> FieldType.array(
                getFieldTypeForTypeName(
                  allowedTypes
                    .filter { it.isPrimitiveType }
                    .random()
                )
              )
              TypeName.ITERABLE -> FieldType.array(
                getFieldTypeForTypeName(
                  allowedTypes
                    .filter { it.isPrimitiveType }
                    .random()
                )
              )
              TypeName.MAP -> FieldType.map(
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
              TypeName.ROW -> {
                if (depth > maxDepth) {
                  return@map null
                }
                FieldType.row(
                  schema(
                    numFields,
                    allowedTypes,
                    depth + 1,
                    maxDepth
                  )
                )
              }
              TypeName.LOGICAL_TYPE -> FieldType.logicalType(
                EnumerationType.create(
                  (0..3).map { faker.witcher.characters() }
                )
              )
            }
          }
          .mapNotNull { Field.of(faker.ancient.god(), it) }
      )
      .build()
  }

  class GenerateFn<T>(val type: FieldType) : DoFn<Long, T>() {
    private lateinit var faker: Faker

    @Setup
    fun setup() {
      faker = Faker()
    }

    @ProcessElement
    fun process(context: ProcessContext) {
      context.output(generateValue(faker, type))
    }
  }

  class Generate<T>(
    private val from: Long = 0,
    private val numElements: Long = 1,
    private val periodLength: Duration = Duration.standardSeconds(1),
    private val generateFn: GenerateFn<T>
  ) : PTransform<PBegin, PCollection<T>>() {
    override fun expand(input: PBegin): PCollection<T> {
      val coder = determineCoder(generateFn.type)

      return input
        .apply(
          GenerateSequence
            .from(from)
            .withRate(numElements, periodLength)
        )
        .apply(ParDo.of(generateFn))
        .setCoder(coder)
    }

    private fun determineCoder(type: FieldType): Coder<T> {
      return when (type.typeName) {
        // scalar types
        TypeName.BOOLEAN -> BooleanCoder.of()
        TypeName.BYTE -> ByteCoder.of()
        TypeName.BYTES -> ByteArrayCoder.of()
        TypeName.DATETIME -> InstantCoder.of()
        TypeName.DECIMAL -> BigDecimalCoder.of()
        TypeName.DOUBLE -> DoubleCoder.of()
        TypeName.FLOAT -> FloatCoder.of()
        TypeName.INT16 -> BigEndianShortCoder.of()
        TypeName.INT32 -> BigEndianIntegerCoder.of()
        TypeName.INT64 -> BigEndianLongCoder.of()
        TypeName.STRING -> StringUtf8Coder.of()

        // aggregate types
        TypeName.ARRAY -> IterableCoder.of(
          determineCoder(type.collectionElementType!!)
        )
        TypeName.ITERABLE -> IterableCoder.of(
          determineCoder(type.collectionElementType!!)
        )
        TypeName.MAP -> MapCoder.of(
          determineCoder(type.mapKeyType!!),
          determineCoder(type.mapValueType!!)
        )

        // complex types
        TypeName.ROW -> RowCoder.of(type.rowSchema)
        TypeName.LOGICAL_TYPE -> determineCoder(
          type.logicalType!!.baseType
        )

        // catch the case of Java theoretically allowing null values for typeName
        null -> throw IllegalArgumentException("type.typeName is null")
      } as Coder<T>
    }
  }

  /**
   * Generates a stream of string elements
   */
  fun strings(
    from: Long = 0,
    numElements: Long = 1,
    periodLength: Duration = Duration.standardSeconds(1),
  ) = Generate<String>(
    from,
    numElements,
    periodLength,
    GenerateFn(FieldType.STRING)
  )

  /**
   * Generates a stream of long elements
   */
  fun longs(
    from: Long = 0,
    numElements: Long = 1,
    periodLength: Duration = Duration.standardSeconds(1),
  ) = Generate<Long>(
    from,
    numElements,
    periodLength,
    GenerateFn(FieldType.INT64)
  )

  /**
   * Generates a stream of row elements
   */
  fun rows(
    from: Long = 0,
    numElements: Long = 1,
    periodLength: Duration = Duration.standardSeconds(1),
    schema: Schema
  ) = Generate<Row>(
    from,
    numElements,
    periodLength,
    GenerateFn(FieldType.row(schema))
  )

  /**
   * Generates a sequence of long elements
   */
  fun sequence(
    from: Long = 0,
    numElements: Long = 1,
    periodLength: Duration = Duration.standardSeconds(1),
  ) = object : PTransform<PBegin, PCollection<Long>>() {
    override fun expand(input: PBegin) = input
      .apply(
        GenerateSequence
          .from(from)
          .withRate(numElements, periodLength)
      )
  }
}
