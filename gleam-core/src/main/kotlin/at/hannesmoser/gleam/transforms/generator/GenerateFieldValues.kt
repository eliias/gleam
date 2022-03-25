package at.hannesmoser.gleam.transforms.generator

import io.github.serpro69.kfaker.Faker
import org.apache.beam.sdk.schemas.Schema

/**
 * Recursively creates fake data for the given schema
 */
internal fun <T> generateFieldValues(
  faker: Faker,
  schema: Schema
): Map<String, T> {
  return schema.fields.associate {
    it.name to generateValue(faker, it.type)
  }
}
