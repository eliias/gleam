package at.hannesmoser.gleam.server

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import org.slf4j.event.Level

fun Application.main() {
  install(CallLogging) {
    level = Level.INFO
  }

  routing {
    get("/") {
      call.respondText("Gleam", ContentType.Text.Html)
    }
  }
}
