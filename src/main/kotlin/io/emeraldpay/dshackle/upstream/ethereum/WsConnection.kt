/**
 * Copyright (c) 2022 EmeraldPay, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.emeraldpay.dshackle.upstream.ethereum

import io.emeraldpay.dshackle.upstream.rpcclient.JsonRpcRequest
import io.emeraldpay.dshackle.upstream.rpcclient.JsonRpcResponse
import io.emeraldpay.dshackle.upstream.rpcclient.JsonRpcWsMessage
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface WsConnection : AutoCloseable {

    val isConnected: Boolean

    fun connectionId(): String
    fun getSubscribeResponses(): Flux<JsonRpcWsMessage>
    fun callRpc(originalRequest: JsonRpcRequest): Mono<JsonRpcResponse>
    fun connect()
    fun connectionInfoFlux(): Flux<ConnectionInfo>

    data class ConnectionInfo(
        val connectionId: String,
        val connectionState: ConnectionState,
    )

    enum class ConnectionState {
        CONNECTED, DISCONNECTED
    }
}
