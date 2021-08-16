package at.hannesmoser.gleam.server

import io.ktor.server.engine.*
import io.ktor.server.netty.*

class HttpServer {
  fun run(args: Array<String>) {
    embeddedServer(Netty, commandLineEnvironment(args)).start()
  }
}
