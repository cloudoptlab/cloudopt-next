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

package net.cloudopt.next.redis.serializer

import net.cloudopt.next.logging.Logger
import org.nustaq.serialization.FSTObjectInput
import org.nustaq.serialization.FSTObjectOutput
import redis.clients.jedis.util.SafeEncoder

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException

/*
* @author: Cloudopt
* @Time: 2018/2/8
* @Description: FstSerializer.
*/
class FstSerializer : ISerializer {

    private val logger = Logger.getLogger(FstSerializer::class.java)

    override fun keyToBytes(key: String): ByteArray {
        return SafeEncoder.encode(key)
    }

    override fun keyFromBytes(bytes: ByteArray): String {
        return SafeEncoder.encode(bytes)
    }

    override fun fieldToBytes(field: Any): ByteArray {
        return valueToBytes(field)
    }

    override fun fieldFromBytes(bytes: ByteArray): Any {
        return valueFromBytes(bytes)
    }

    override fun valueToBytes(value: Any): ByteArray {
        var fstOut: FSTObjectOutput? = null
        try {
            val bytesOut = ByteArrayOutputStream()
            fstOut = FSTObjectOutput(bytesOut)
            fstOut.writeObject(value)
            fstOut.flush()
            return bytesOut.toByteArray()
        } catch (e: Exception) {
            throw RuntimeException(e)
        } finally {
            if (fstOut != null)
                try {
                    fstOut.close()
                } catch (e: IOException) {
                    logger.error(e.message ?: "", e)
                }

        }
    }

    override fun valueFromBytes(bytes: ByteArray): Any {
        if (bytes.isEmpty())
            return ""

        var fstInput: FSTObjectInput? = null
        try {
            fstInput = FSTObjectInput(ByteArrayInputStream(bytes))
            return fstInput.readObject()
        } catch (e: Exception) {
            throw RuntimeException(e)
        } finally {
            if (fstInput != null)
                try {
                    fstInput.close()
                } catch (e: IOException) {
                    logger.error(e.message ?: "", e)
                }

        }
    }

    companion object {
        val me: ISerializer = FstSerializer()
    }
}



