/**
 * Copyright (c) 2020 EmeraldPay, Inc
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
package io.emeraldpay.dshackle.upstream

import io.emeraldpay.dshackle.startup.UpstreamChangeEvent
import io.emeraldpay.dshackle.test.EthereumPosRpcUpstreamMock
import io.emeraldpay.dshackle.test.EthereumRpcUpstreamMock
import io.emeraldpay.dshackle.test.TestingCommons
import io.emeraldpay.dshackle.Chain
import spock.lang.Specification

class CurrentMultistreamHolderSpec extends Specification {

    def "add upstream"() {
        setup:
        def current = new CurrentMultistreamHolder(TestingCommons.defaultMultistreams())
        def up = new EthereumPosRpcUpstreamMock("test", Chain.ETHEREUM, TestingCommons.api())
        when:
        current.getUpstream(Chain.ETHEREUM).onUpstreamChange(new UpstreamChangeEvent(Chain.ETHEREUM, up, UpstreamChangeEvent.ChangeType.ADDED))
        then:
        current.getAvailable() == [Chain.ETHEREUM]
        current.getUpstream(Chain.ETHEREUM).getAll()[0] == up
    }

    def "add multiple upstreams"() {
        setup:
        def current = new CurrentMultistreamHolder(TestingCommons.defaultMultistreams())
        def up1 = new EthereumPosRpcUpstreamMock("test1", Chain.ETHEREUM, TestingCommons.api())
        def up2 = new EthereumRpcUpstreamMock("test2", Chain.ETHEREUM_CLASSIC, TestingCommons.api())
        def up3 = new EthereumPosRpcUpstreamMock("test3", Chain.ETHEREUM, TestingCommons.api())
        when:
        current.getUpstream(Chain.ETHEREUM).onUpstreamChange(new UpstreamChangeEvent(Chain.ETHEREUM, up1, UpstreamChangeEvent.ChangeType.ADDED))
        current.getUpstream(Chain.ETHEREUM_CLASSIC).onUpstreamChange(new UpstreamChangeEvent(Chain.ETHEREUM_CLASSIC, up2, UpstreamChangeEvent.ChangeType.ADDED))
        current.getUpstream(Chain.ETHEREUM).onUpstreamChange(new UpstreamChangeEvent(Chain.ETHEREUM, up3, UpstreamChangeEvent.ChangeType.ADDED))
        current.getUpstream(Chain.ETHEREUM_CLASSIC).onUpstreamChange(new UpstreamChangeEvent(Chain.ETHEREUM, up3, UpstreamChangeEvent.ChangeType.ADDED))
        then:
        current.getAvailable().toSet() == [Chain.ETHEREUM, Chain.ETHEREUM_CLASSIC].toSet()
        current.getUpstream(Chain.ETHEREUM).getAll().toSet() == [up1, up3].toSet()
        current.getUpstream(Chain.ETHEREUM_CLASSIC).getAll().toSet() == [up2].toSet()
    }

    def "remove upstream"() {
        setup:
        def current = new CurrentMultistreamHolder(TestingCommons.defaultMultistreams())
        def up1 = new EthereumPosRpcUpstreamMock("test1", Chain.ETHEREUM, TestingCommons.api())
        def up2 = new EthereumRpcUpstreamMock("test2", Chain.ETHEREUM_CLASSIC, TestingCommons.api())
        def up3 = new EthereumPosRpcUpstreamMock("test3", Chain.ETHEREUM, TestingCommons.api())
        def up1_del = new EthereumPosRpcUpstreamMock("test1", Chain.ETHEREUM, TestingCommons.api())
        when:
        current.getUpstream(Chain.ETHEREUM).onUpstreamChange(new UpstreamChangeEvent(Chain.ETHEREUM, up1, UpstreamChangeEvent.ChangeType.ADDED))
        current.getUpstream(Chain.ETHEREUM_CLASSIC).onUpstreamChange(new UpstreamChangeEvent(Chain.ETHEREUM_CLASSIC, up2, UpstreamChangeEvent.ChangeType.ADDED))
        current.getUpstream(Chain.ETHEREUM).onUpstreamChange(new UpstreamChangeEvent(Chain.ETHEREUM, up3, UpstreamChangeEvent.ChangeType.ADDED))
        current.getUpstream(Chain.ETHEREUM).onUpstreamChange(new UpstreamChangeEvent(Chain.ETHEREUM, up1_del, UpstreamChangeEvent.ChangeType.REMOVED))
        then:
        current.getAvailable().toSet() == [Chain.ETHEREUM, Chain.ETHEREUM_CLASSIC].toSet()
        current.getUpstream(Chain.ETHEREUM).getAll().toSet() == [up3].toSet()
        current.getUpstream(Chain.ETHEREUM_CLASSIC).getAll().toSet() == [up2].toSet()
    }

    def "available after adding"() {
        setup:
        def current = new CurrentMultistreamHolder(TestingCommons.defaultMultistreams())
        def up1 = new EthereumPosRpcUpstreamMock("test1", Chain.ETHEREUM, TestingCommons.api())

        when:
        def act = current.isAvailable(Chain.ETHEREUM)
        then:
        !act

        when:
        current.getUpstream(Chain.ETHEREUM).onUpstreamChange(new UpstreamChangeEvent(Chain.ETHEREUM, up1, UpstreamChangeEvent.ChangeType.ADDED))
        act = current.isAvailable(Chain.ETHEREUM)

        then:
        act
    }
}
