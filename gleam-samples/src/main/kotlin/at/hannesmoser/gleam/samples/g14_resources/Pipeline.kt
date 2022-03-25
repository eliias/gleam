@file:Suppress("PackageNaming")

package at.hannesmoser.gleam.samples.g14_resources

import at.hannesmoser.gleam.resource.Component
import at.hannesmoser.gleam.resource.inject
import at.hannesmoser.gleam.resource.resources.Bigtable
import at.hannesmoser.gleam.resource.resources.GCP

object Pipeline : Component {
  private val gcp by inject<GCP>()
  private val bigtable by inject<Bigtable>()

  @JvmStatic
  fun main(args: Array<String>) {
    val instanceId = gcp.instanceId
    val projectId = gcp.projectId

    println("GCP Config: instanceId=$instanceId, projectId=$projectId")

    val writer = bigtable.write()
      .withTableId("products")
      .withWriteResults()

    println("Bigtable Writer: writer=$writer")
  }
}
