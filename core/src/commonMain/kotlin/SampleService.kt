package com.example

import kotlinx.rpc.annotations.Rpc

@Rpc
interface SampleService {
  suspend fun doUncancellableWork()
}

