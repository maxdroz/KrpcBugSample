package com.example.client

import com.example.SampleService
import io.ktor.client.HttpClient
import io.ktor.http.encodedPath
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.measureTime
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
  val job = async {
    sampleRpc.doUncancellableWork()
  }
  delay(1000) // let coroutine to connect to server
  val duration = measureTime {
    job.cancelAndJoin()
  }
  println("Cancelling job took $duration")
}