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
        var fileInputStream: InputStream? = try {
            File(getRootClassPath() + "/" + fileName).inputStream()
        } catch (e: Exception) {
            Resourcer::class.java.getResourceAsStream("/" + fileName)
        }
        return fileInputStream
    }

    fun getFileString(fileName: String, isJson: Boolean = false): String {
        return inputSreamToString(getFileInputStream(fileName)!!, isJson)
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
        if (rootPath == null || rootPath.equals("")) {
            val path = loader.getResource("")!!.getPath()
            rootPath = File(path).getAbsolutePath()
        }
        return rootPath
    }

    /**
     * Turn the inputstream into a string
     * @param inputStream The inputstream to be processed
     * @return String
     */
    fun inputSreamToString(inputStream: InputStream, isJson: Boolean = false): String {
        val reader = inputStream.bufferedReader()
        val sb = StringBuilder()
        try {
            while (reader.ready()) {
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
                inputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        return sb.toString()
    }

}