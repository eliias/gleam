package at.hannesmoser.gleam

import at.hannesmoser.gleam.pipelines.GleamPipeline
import at.hannesmoser.gleam.server.HttpServer

object App {
  @JvmStatic
  fun main(args: Array<String>) {
    val pipeline = GleamPipeline()
    pipeline.run(args)

    val server = HttpServer()
    server.run(args)
  }
}
