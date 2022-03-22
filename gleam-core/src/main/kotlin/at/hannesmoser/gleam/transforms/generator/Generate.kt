package at.hannesmoser.gleam.transforms.generator

import io.github.serpro69.kfaker.Faker
import org.apache.beam.sdk.io.GenerateSequence
import org.apache.beam.sdk.schemas.Schema
import org.apache.beam.sdk.schemas.Schema.FieldType
import org.apache.beam.sdk.transforms.DoFn
import org.apache.beam.sdk.transforms.PTransform
import org.apache.beam.sdk.transforms.ParDo
import org.apache.beam.sdk.values.PBegin
import org.apache.beam.sdk.values.PCollection
import org.joda.time.Duration

class Generate<T>(
  private val from: Long = 0,
  private val numElements: Long = 1,
  private val periodLength: Duration = Duration.standardSeconds(1),
  private val fieldType: FieldType
) : PTransform<PBegin, PCollection<T>>() {
  override fun expand(input: PBegin): PCollection<T> {
    val generateFn = GenerateFn<T>(fieldType)
    val coder = findCoderForFieldType<T>(generateFn.type)

    return input
      .apply(
        GenerateSequence.from(from)
          .withRate(numElements, periodLength)
      )
      .apply(ParDo.of(generateFn))
      .setCoder(coder)
  }

  private class GenerateFn<T>(val type: Schema.FieldType) : DoFn<Long, T>() {
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
}
