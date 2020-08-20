package at.hannesmoser.gleam

object App {
  @JvmStatic
  fun main(args: Array<String>) {
    val pipeline = GleamPipeline()
    pipeline.run(args)
  }
}
