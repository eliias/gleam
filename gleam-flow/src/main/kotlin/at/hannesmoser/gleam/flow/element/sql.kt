package at.hannesmoser.gleam.flow.element

import org.apache.beam.sdk.extensions.sql.SqlTransform
import org.apache.beam.sdk.values.PCollection
import org.apache.beam.sdk.values.Row

fun <T> PCollection<T>.sql(query: String): PCollection<Row> = apply(SqlTransform.query(query))
