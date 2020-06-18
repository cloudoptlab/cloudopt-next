/*
 * Copyright 2017 Cloudopt.
 *
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  and Apache License v2.0 which accompanies this distribution.
 *
 *  The Eclipse Public License is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  The Apache License v2.0 is available at
 *  http://www.opensource.org/licenses/apache2.0.php
 *
 *  You may elect to redistribute this code under either of these licenses.
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



