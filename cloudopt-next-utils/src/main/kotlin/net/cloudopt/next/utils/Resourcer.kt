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