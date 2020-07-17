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
package net.cloudopt.next.utils

import java.io.File
import java.io.IOException
import java.io.InputStream


/*
 * @author: Cloudopt
 * @Time: 2018/5/22
 * @Description: Help get resource files
 */
object Resourcer {

    private var rootPath: String = ""


    /**
     * Get the file input stream
     * @return FileInputStream
     */
    fun getFileInputStream(fileName: String): InputStream? {
        return try {
            File(getRootClassPath() + "/" + fileName).inputStream()
        } catch (e: Exception) {
            Resourcer::class.java.getResourceAsStream("/$fileName")
        }
    }

    fun getFileString(fileName: String, isJson: Boolean = false): String {
        return inputStreamToString(getFileInputStream(fileName)!!, isJson)
    }

    fun exist(fileName: String): Boolean {
        return getFileInputStream(fileName) != null
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

}