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
package net.cloudopt.next.encrypt

/*
 * @author: Cloudopt
 * @Time: 2018/1/5
 * @Description: Encryption module common interface
 */
abstract class Encrypt {

    abstract fun encrypt(value: String): String

    abstract fun decrypt(value: String): String

    /**
     * Convert Byte [] to a string
     * @param value The array to be converted
     * @return This is the converted string
     */
    fun toHexString(value: ByteArray): String {
        var sb: StringBuffer = StringBuffer()
        for (b in value) {
            var i: Int = b.toInt() and 0xff
            var hexString = Integer.toHexString(i)
            if (hexString.length < 2) {
                hexString = "0" + hexString
            }
            sb.append(hexString)
        }
        return sb.toString()
    }
}
