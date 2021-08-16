package at.hannesmoser.gleam

import at.hannesmoser.gleam.pipelines.SimplePipeline

object App {
  @JvmStatic
  fun main(args: Array<String>) {
    val pipeline = SimplePipeline()
    pipeline.run(args)  }
}
