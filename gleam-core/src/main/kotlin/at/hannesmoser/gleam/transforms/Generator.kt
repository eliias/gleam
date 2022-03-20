package at.hannesmoser.gleam.transforms

import io.github.serpro69.kfaker.Faker
import org.apache.beam.sdk.io.GenerateSequence
import org.apache.beam.sdk.schemas.Schema
import org.apache.beam.sdk.transforms.DoFn
import org.apache.beam.sdk.transforms.PTransform
import org.apache.beam.sdk.transforms.ParDo
import org.apache.beam.sdk.values.PBegin
import org.apache.beam.sdk.values.PCollection
import org.apache.beam.sdk.values.Row
import org.joda.time.Duration

object Generator {
  private val STRINGS = object : DoFn<Long, String>() {
    private lateinit var faker: Faker

    @Setup
    fun setup() {
      faker = Faker()
    }

    @ProcessElement
    fun process(context: ProcessContext) {
      context.output(faker.name.name())
    }
  }

  private val LONGS = object : DoFn<Long, Long>() {
    private lateinit var faker: Faker

    @Setup
    fun setup() {
      faker = Faker()
    }

    @ProcessElement
    fun process(context: ProcessContext) {
      context.output(faker.random.nextLong())
    }
  }

  private class ROWS(private val schema: Schema) : DoFn<Long, Row>() {
    private lateinit var faker: Faker

    @Setup
    fun setup() {
      faker = Faker()
    }

    @ProcessElement
    fun process(context: ProcessContext) {
      val row = Row.withSchema(schema)
        .withFieldValues(generateFieldValues())
        .build()

      context.output(row)
    }

    private fun generateFieldValues() = schema.fields.associate {
      it.name to
        when (it.type) {
          Schema.FieldType.BOOLEAN -> faker.random.nextBoolean()
          Schema.FieldType.BYTE -> faker.random.nextInt().toByte()
          Schema.FieldType.BYTES -> faker.artist.names().toByteArray()
          Schema.FieldType.DOUBLE -> faker.random.nextDouble()
          Schema.FieldType.FLOAT -> faker.random.nextFloat()
          Schema.FieldType.INT32 -> faker.random.nextInt()
          Schema.FieldType.INT64 -> faker.random.nextLong()
          Schema.FieldType.STRING -> faker.commerce.productName()
          else -> throw NotImplementedError("${it.type.typeName.name} has no generator")
        }
    }
  }

  /**
   * Generates a stream of string elements
   */
  fun strings(
    from: Long = 0,
    numElements: Long = 1,
    periodLength: Duration = Duration.standardSeconds(1),
  ) = object : PTransform<PBegin, PCollection<String>>() {
    override fun expand(input: PBegin) = input
      .apply(
        GenerateSequence
          .from(from)
          .withRate(numElements, periodLength)
      )
      .apply(ParDo.of(STRINGS))
  }

  /**
   * Generates a stream of long elements
   */
  fun longs(
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
      .apply(ParDo.of(LONGS))
  }

  /**
   * Generates a stream of row elements
   */
  fun rows(
    from: Long = 0,
    numElements: Long = 1,
    periodLength: Duration = Duration.standardSeconds(1),
    schema: Schema
  ) = object : PTransform<PBegin, PCollection<Row>>() {
    override fun expand(input: PBegin): PCollection<Row> = input
      .apply(
        GenerateSequence
          .from(from)
          .withRate(numElements, periodLength)
      )
      .apply(ParDo.of(ROWS(schema)))
      .setRowSchema(schema)
  }

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
