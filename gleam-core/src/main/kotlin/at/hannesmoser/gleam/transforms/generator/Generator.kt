package at.hannesmoser.gleam.transforms.generator

import org.apache.beam.sdk.io.GenerateSequence
import org.apache.beam.sdk.schemas.Schema
import org.apache.beam.sdk.transforms.PTransform
import org.apache.beam.sdk.values.PBegin
import org.apache.beam.sdk.values.PCollection
import org.apache.beam.sdk.values.Row
import org.joda.time.Duration

object Generator {
  fun schema(
    numFields: Int = 3,
    allowedTypes: Array<Schema.TypeName> = ALL_TYPENAMES,
    maxDepth: Int = 2
  ) = schema(numFields, allowedTypes, 1, maxDepth)

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
    Schema.FieldType.STRING
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
    Schema.FieldType.INT64
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
    Schema.FieldType.row(schema)
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
        GenerateSequence.from(from)
          .withRate(numElements, periodLength)
      )
  }
}
