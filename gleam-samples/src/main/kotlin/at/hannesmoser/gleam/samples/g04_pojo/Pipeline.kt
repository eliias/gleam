@file:Suppress("PackageNaming")

package at.hannesmoser.gleam.samples.g04_pojo

import at.hannesmoser.gleam.transforms.Log
import at.hannesmoser.gleam.transforms.generator.Generator
import org.apache.beam.sdk.Pipeline
import org.apache.beam.sdk.schemas.JavaBeanSchema
import org.apache.beam.sdk.schemas.JavaFieldSchema
import org.apache.beam.sdk.schemas.Schema
import org.apache.beam.sdk.schemas.annotations.DefaultSchema
import org.apache.beam.sdk.schemas.transforms.Convert

@DefaultSchema(JavaBeanSchema::class)
data class Product(
  var id: Long = -1,
  var title: String = "",
  var price: Float = 0.0f
)

@DefaultSchema(JavaFieldSchema::class)
data class Customer(
  @JvmField var id: Long = -1,
  @JvmField var title: String = "",
  @JvmField var price: Float = 0.0f
)

object Pipeline {
  @JvmStatic
  fun main(args: Array<String>) {
    val pipeline = Pipeline.create()

    pipeline
      .apply(
        Generator.rows(
          schema = Schema.builder()
            .addInt64Field("id")
            .addStringField("title")
            .addFloatField("price")
            .build()
        )
      )
      .apply(Convert.fromRows(Product::class.java))
      .apply(Log.info())

    pipeline
      .apply(
        Generator.rows(
          schema = Schema.builder()
            .addInt64Field("id")
            .addStringField("title")
            .addFloatField("price")
            .build()
        )
      )
      .apply(Convert.fromRows(Customer::class.java))
      .apply(Log.info())

    pipeline.run()
  }
}
