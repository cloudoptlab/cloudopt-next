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
package net.cloudopt.next.core

import net.cloudopt.next.json.Jsoner.jsonToMutableMap
import net.cloudopt.next.json.Jsoner.jsonToObject
import net.cloudopt.next.json.Jsoner.toJsonObject
import net.cloudopt.next.json.Jsoner.toJsonString
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.net.URLDecoder
import java.nio.file.Files
import kotlin.io.path.Path
import kotlin.reflect.KClass

object Resourcer {

    private var rootPath: String = ""

    /**
     * Get the file input stream
     * @param fileName String specify the file name
     * @return File
     */
    fun getFile(fileName: String): File {
        return if (File(URLDecoder.decode(getRootClassPath() + "/" + fileName, "UTF-8")).exists()) {
            File(URLDecoder.decode(getRootClassPath() + "/" + fileName, "UTF-8"))
        } else {
            File(URLDecoder.decode(Resourcer::class.java.getResource("/$fileName")?.file, "UTF-8"))
        }
    }

    /**
     * Get the file url
     * @param fileName String specify the file name
     * @return URL?
     */
    fun getUrl(fileName: String): URL? {
        return Resourcer::class.java.classLoader.getResource(fileName)
    }

    /**
     * Get the file input stream
     * @param fileName String specify the file name
     * @return FileInputStream
     */
    fun getFileInputStream(fileName: String): InputStream? {
        return Resourcer::class.java.classLoader.getResourceAsStream(fileName)
    }

    /**
     * Read external file and automatically remove carriage returns.
     * @param fileName String
     * @param isJson Boolean If true, the carriage return is automatically removed.
     * @return String
     */
    fun getExternalFileString(fileName: String, isJson: Boolean = false): String {
        return Files.readString(Path(fileName))
    }

    /**
     * Read file in project resources and automatically remove carriage returns.
     * @param fileName String
     * @param isJson Boolean If true, the carriage return is automatically removed.
     * @return String
     */
    fun getFileString(fileName: String, isJson: Boolean = false): String {
        return inputStreamToString(getFileInputStream(fileName), isJson)
    }

    /**
     * Get the project runtime path
     * @return Path
     */
    fun getRootClassPath(): String {
        val loader = ClassLoader.getSystemClassLoader()
        return rootPath.ifBlank {
            val path = loader.getResource("")?.path
            rootPath = File(path ?: "").absolutePath
            rootPath
        }
    }

    /**
     * Turn the input stream into a string
     * @param inputStream InputStream the input stream to be processed
     * @param isJson Boolean
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

    /**
     * Get the configuration in the json file by the specified filename and prefix, and deserialize it to the specified
     * object.
     * @param fileName String specify the file name
     * @param prefix String specify the prefix
     * @param clazz KClass<*> specify the class
     * @param external Boolean if it is true, it will be read the file outside the project
     * @return Any
     */
    fun read(fileName: String, prefix: String, clazz: KClass<*>, external: Boolean = false): Any {
        val map = read(fileName, prefix, external)
        return map.toJsonString().jsonToObject(clazz)
    }

    /**
     * Get the configuration in the json file.
     * @param fileName String specify the file name
     * @param external Boolean if it is true, it will be read the file outside the project
     * @return MutableMap<String, Any>
     */
    fun read(fileName: String, external: Boolean = false): MutableMap<String, Any> {
        var jsonString = if (external) {
            getExternalFileString(fileName)
        } else {
            inputStreamToString(getFileInputStream(fileName))
        }
        jsonString = cleanText(jsonString)
        return jsonString.jsonToMutableMap()
    }

    /**
     * Get the configuration in the json file by the specified filename and prefix, and deserialize it to map.
     * @param fileName String specify the file name
     * @param prefix String specify the prefix
     * @param external Boolean if it is true, it will be read the file outside the project
     * @return MutableMap<String, Any>
     */
    fun read(fileName: String, prefix: String, external: Boolean = false): MutableMap<String, Any> {
        var jsonString = if (external) {
            getExternalFileString(fileName)
        } else {
            inputStreamToString(getFileInputStream(fileName))
        }
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

    /**
     * Clear out the extra carriage return characters.
     * @param jsonString String
     * @return String
     */
    private fun cleanText(jsonString: String): String {
        return jsonString.replace("/n", "")
    }

}