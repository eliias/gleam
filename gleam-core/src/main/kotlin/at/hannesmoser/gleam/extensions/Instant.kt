package at.hannesmoser.gleam.extensions

import java.time.Instant

@Suppress("MagicNumber")
fun Instant.toEpochMicros() = this.toEpochMilli() * 1000
