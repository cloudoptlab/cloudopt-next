/*
 * Copyright 2017-2020 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.cloudopt.next.web.event.codec

import com.alibaba.fastjson.JSON
import io.netty.util.CharsetUtil
import io.vertx.core.buffer.Buffer
import io.vertx.core.eventbus.MessageCodec


class ObjectMessageCodec : MessageCodec<Any, Any> {
    override fun encodeToWire(buffer: Buffer, any: Any) {
        var byteArray = JSON.toJSONString(any).toByteArray(CharsetUtil.UTF_8)
        buffer.appendInt(byteArray.size)
        buffer.appendBytes(byteArray)
    }

    override fun decodeFromWire(pos: Int, buffer: Buffer): Any {
        var pos = pos
        val length = buffer.getInt(pos)
        pos += 4
        val bytes = buffer.getBytes(pos, pos + length)
        return JSON.parse(String(bytes))
    }

    override fun transform(any: Any): Any {
        return any
    }

    override fun name(): String? {
        return "object"
    }

    override fun systemCodecID(): Byte {
        return -1
    }
}
