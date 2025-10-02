package com.example

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.routing
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.measureTime
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import kotlinx.rpc.krpc.ktor.server.Krpc
import kotlinx.rpc.krpc.ktor.server.rpc
import kotlinx.rpc.krpc.serialization.json.json

fun main() {
  embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
    .start(wait = true)
}

fun Application.module() {
  install(Krpc)
  routing {
    rpc("/api") {
      rpcConfig { serialization { json() } }
      registerService<SampleService> { SampleServiceImpl() }
    }
  }
}

class SampleServiceImpl : SampleService {
  override suspend fun doUncancellableWork() {
    val startTime = System.currentTimeMillis()
    try {
      withContext(NonCancellable) {
        delay(5_000)
      }
    } finally {
      val finishTime = System.currentTimeMillis()
      println("Work ran for ${(finishTime - startTime).milliseconds} before being finishing")
    }
  }
}