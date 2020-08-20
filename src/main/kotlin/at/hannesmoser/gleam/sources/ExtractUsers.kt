package at.hannesmoser.gleam.sources

import at.hannesmoser.gleam.entities.User
import at.hannesmoser.gleam.schemas.Name
import org.apache.beam.sdk.extensions.sql.meta.provider.text.TextTableProvider
import org.apache.beam.sdk.io.TextIO
import org.apache.beam.sdk.transforms.MapElements
import org.apache.beam.sdk.transforms.PTransform
import org.apache.beam.sdk.transforms.SerializableFunction
import org.apache.beam.sdk.values.PBegin
import org.apache.beam.sdk.values.PCollection
import org.apache.beam.sdk.values.TypeDescriptor
import org.apache.commons.csv.CSVFormat

class ExtractUsers(private val filepath: String) : PTransform<PBegin, PCollection<User>>() {
  override fun expand(input: PBegin): PCollection<User> {
    return input.apply(
      "read text file",
      TextIO
        .read()
        .from(filepath)
    )
      .apply(
        "parse csv",
        TextTableProvider.CsvToRow(Name.schema, CSVFormat.DEFAULT)
      )
      .apply(
        "map rows to users",
        MapElements
          .into(TypeDescriptor.of(User::class.java))
          .via(SerializableFunction {
            User(
              it.getInt64("id")!!,
              it.getString("name")!!
            )
          })
      )
  }
}
