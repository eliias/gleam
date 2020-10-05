package at.hannesmoser.gleam.sources

import at.hannesmoser.gleam.entities.Project
import at.hannesmoser.gleam.schemas.CSVProject
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.beam.sdk.extensions.sql.meta.provider.text.TextTableProvider
import org.apache.beam.sdk.io.TextIO
import org.apache.beam.sdk.transforms.MapElements
import org.apache.beam.sdk.transforms.PTransform
import org.apache.beam.sdk.transforms.SerializableFunction
import org.apache.beam.sdk.values.PBegin
import org.apache.beam.sdk.values.PCollection
import org.apache.beam.sdk.values.TypeDescriptor
import org.apache.commons.csv.CSVFormat

class ExtractProjects(private val filepath: String) : PTransform<PBegin, PCollection<Project>>() {
  override fun expand(input: PBegin): PCollection<Project> {
    return input.apply(
      "read text file",
      TextIO
        .read()
        .from(filepath)
    )
      .apply(
        "parse csv",
        TextTableProvider.CsvToRow(CSVProject.schema, CSVFormat.DEFAULT)
      )
      .apply(
        "map rows to projects",
        MapElements
          .into(TypeDescriptor.of(Project::class.java))
          .via(SerializableFunction {
            Project(
              it.getInt64("id")!!,
              it.getString("name")!!,
              it.getInt64("owner")!!
            )
          })
      )
  }
}
