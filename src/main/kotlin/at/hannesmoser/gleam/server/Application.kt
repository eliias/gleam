package at.hannesmoser.gleam.server

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.websocket.*
import org.slf4j.event.Level

fun Application.main() {
  install(CallLogging) {
    level = Level.INFO
  }
  install(WebSockets)

  routing {
    get("/") {
      call.respondText("Gleam", ContentType.Text.Html)
    }

    webSocket("/") {
      for (frame in incoming) {
        when (frame) {
          is Frame.Text -> {
            val text = frame.readText()
            outgoing.send(Frame.Text("YOU SAID: $text"))
            if (text.equals("bye", ignoreCase = true)) {
              close(CloseReason(CloseReason.Codes.NORMAL, "Client said BYE"))
            }
          }
        }
      }
    }
  }
}
