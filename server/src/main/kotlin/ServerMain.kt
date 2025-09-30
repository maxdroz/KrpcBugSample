package com.example

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.routing
import kotlin.time.measureTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
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
  private val sharedFlow = MutableSharedFlow<Int>(extraBufferCapacity = Int.MAX_VALUE)

  override fun receiveInt(): Flow<Int> {
    return sharedFlow
  }

  override suspend fun sendInt(data: Int) {
    val duration = measureTime {
      sharedFlow.emit(data)
    }
    println("Emission of item $data took $duration")
  }
}