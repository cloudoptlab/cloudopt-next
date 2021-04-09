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
package net.cloudopt.next.utils

import net.cloudopt.next.json.Jsoner.jsonToObject
import net.cloudopt.next.json.Jsoner.jsontoMutableMap
import net.cloudopt.next.json.Jsoner.toJsonObject
import net.cloudopt.next.json.Jsoner.toJsonString
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.net.URLDecoder
import kotlin.reflect.KClass

object Resourcer {

    private var rootPath: String = ""

    /**
     * Get the file input stream
     * @param fileName Specify the file name
     * @return File
     */
    fun getFile(fileName: String): File {
        return if (File(getRootClassPath() + "/" + fileName).exists()) {
            File(URLDecoder.decode(getRootClassPath() + "/" + fileName, "UTF-8"))
        } else {
            File(URLDecoder.decode(Resourcer::class.java.getResource("/$fileName").file, "UTF-8"))
        }
    }

    /**
     * Get the file input stream
     * @param fileName Specify the file name
     * @return FileInputStream
     */
    fun getFileInputStream(fileName: String): InputStream {
        return getFile(fileName).inputStream()
    }

    fun getFileString(fileName: String, isJson: Boolean = false): String {
        return inputStreamToString(getFileInputStream(fileName), isJson)
    }

    /**
     * Get the project runtime path
     * @return Path
     */
    fun getRootClassPath(): String {
        val loader = ClassLoader.getSystemClassLoader()
        return if (rootPath.isBlank()) {
            val path = loader.getResource("")?.path
            rootPath = File(path).absolutePath
            rootPath
        } else {
            rootPath
        }
    }

    /**
     * Turn the input stream into a string
     * @param inputStream The input stream to be processed
     * @return String
     */
    fun inputStreamToString(inputStream: InputStream?, isJson: Boolean = false): String {
        val reader = inputStream?.bufferedReader()
        val sb = StringBuilder()
        try {
            while (reader?.ready() == true) {
                if (isJson) {
                    sb.append(reader.readLine())
                } else {
                    sb.append(reader.readLine() + "/n")
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                inputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        return sb.toString()
    }

    fun read(filePath: String, prefix: String, clazz: KClass<*>): Any {
        val map = read(filePath, prefix)
        return map.toJsonString().jsonToObject(clazz)
    }

    fun read(filePath: String): MutableMap<String, Any> {
        var jsonString = inputStreamToString(getFileInputStream(filePath))
        jsonString = cleanText(jsonString)
        return jsonString.jsontoMutableMap()
    }

    fun read(filePath: String, prefix: String): MutableMap<String, Any> {
        var jsonString = inputStreamToString(getFileInputStream(filePath))
        jsonString = cleanText(jsonString)
        var jsonObj = jsonString.toJsonObject()
        var list = prefix.split(".")
        for (key in list) {
            if (jsonObj.getJsonObject(key) != null) {
                jsonObj = jsonObj.getJsonObject(key)
            }
        }
        return jsonObj.map.toMutableMap()
    }

    private fun cleanText(jsonString: String): String {
        return jsonString.replace("/n", "")
    }

}