package com.example

import kotlinx.coroutines.flow.Flow
import kotlinx.rpc.annotations.Rpc

@Rpc
interface SampleService {
    fun receiveInt(): Flow<Int>

    suspend fun sendInt(data: Int)
}

