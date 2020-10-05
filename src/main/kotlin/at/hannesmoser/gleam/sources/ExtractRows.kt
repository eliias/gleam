package at.hannesmoser.gleam.sources

import org.apache.beam.sdk.extensions.sql.meta.provider.text.TextTableProvider
import org.apache.beam.sdk.io.TextIO
import org.apache.beam.sdk.schemas.Schema
import org.apache.beam.sdk.transforms.PTransform
import org.apache.beam.sdk.values.PBegin
import org.apache.beam.sdk.values.PCollection
import org.apache.beam.sdk.values.Row
import org.apache.commons.csv.CSVFormat

class ExtractRows(
  private val filepath: String,
  private val schema: Schema
) : PTransform<PBegin, PCollection<Row>>() {
  override fun expand(input: PBegin): PCollection<Row> {
    return input.apply(
      "read text file",
      TextIO
        .read()
        .from(filepath)
    )
      .apply(
        "parse csv",
        TextTableProvider.CsvToRow(schema, CSVFormat.DEFAULT)
      )
  }
}
