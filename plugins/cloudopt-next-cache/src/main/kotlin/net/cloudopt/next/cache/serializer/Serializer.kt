/*
 * Copyright 2017-2021 Cloudopt
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
package net.cloudopt.next.cache.serializer

import java.io.IOException

/**
 * Object Serialization Interface.
 */
interface Serializer {
    /**
     * Serializing objects to byte arrays
     *
     * @param any Objects to be serialized
     * @return Byte arrays
     * @throws IOException io exception
     */
    @Throws(IOException::class)
    fun serialize(any: Any): ByteArray

    /**
     * Deserialization to objects
     *
     * @param bytes Byte arrays to be deserialized
     * @return Any object
     * @throws IOException io exception
     */
    @Throws(IOException::class)
    fun deserialize(bytes: ByteArray): Any
}