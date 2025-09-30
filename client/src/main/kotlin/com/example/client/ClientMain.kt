package com.example.client

import com.example.SampleService
import io.ktor.client.HttpClient
import io.ktor.http.encodedPath
import kotlin.time.measureTime
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.rpc.krpc.ktor.client.KtorRpcClient
import kotlinx.rpc.krpc.ktor.client.installKrpc
import kotlinx.rpc.krpc.ktor.client.rpc
import kotlinx.rpc.krpc.ktor.client.rpcConfig
import kotlinx.rpc.krpc.serialization.json.json
import kotlinx.rpc.withService

private val httpClient = HttpClient {
  installKrpc()
}

private val rpcClient: KtorRpcClient = httpClient.rpc {
  url {
    host = "localhost"
    port = 8080
    encodedPath = "/api"
  }

  rpcConfig {
    serialization {
      json()
    }
  }
}

private val sampleRpc = rpcClient.withService<SampleService>()

suspend fun main(): Unit = coroutineScope {
  launch { collectIntEvery10Seconds() }
  launch {
    delay(1000) //give collector a head start
    emitIntEvery1Second()
  }
}

private suspend fun collectIntEvery10Seconds() {
  sampleRpc.receiveInt().collect { int ->
    delay(10_000)
  }
}

private suspend fun emitIntEvery1Second() {
  var i = 0
  while (true) {
    val payload = i++
    val duration = measureTime {
      sampleRpc.sendInt(payload)
    }
    println("Emission of item $payload took $duration")
    delay(1000)
  }
}
