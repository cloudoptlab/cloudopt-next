/*
 * Copyright 2017 Cloudopt.
 *
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Apache License v2.0 which accompanies this distribution.
 *
 *  The Apache License v2.0 is available at
 *  http://www.opensource.org/licenses/apache2.0.php
 *
 *  You may elect to redistribute this code under either of these licenses.
 */
package net.cloudopt.next.logging

import org.fusesource.jansi.Ansi

/*
 * @author: Cloudopt
 * @Time: 2018/10/18
 * @Description: Used to help the console output colored text
 */

object Colorer {

    /**
     * Output black text
     * @param value Hope to output the text
     * @return This is the processed text
     */
    fun black(value: String): String {
        return diy("black", value)
    }

    /**
     * Output white text
     * @param value Hope to output the text
     * @return Handled text
     */
    fun white(value: String): String {
        return diy("white", value)
    }

    /**
     * Output green text
     * @param value Hope to output the text
     * @return Handled text
     */
    fun green(value: String): String {
        return diy("green", value)
    }

    /**
     * Output yellow text
     * @param value Hope to output the text
     * @return Handled text
     */
    fun yellow(value: String): String {
        return diy("yellow", value)
    }

    /**
     * Output pink text
     * @param value Hope to output the text
     * @return Handled text
     */
    fun magenta(value: String): String {
        return diy("magenta", value)
    }

    /**
     * Output red text
     * @param value Hope to output the text
     * @return Handled text
     */
    fun red(value: String): String {
        return diy("red", value)
    }

    /**
     * Output cyan text
     * @param value Hope to output the text
     * @return Handled text
     */
    fun cyan(value: String): String {
        return diy("cyan", value)
    }

    /**
     * Output blue text
     * @param value Hope to output the text
     * @return Handled text
     */
    fun blue(value: String): String {
        return diy("blue", value)
    }

    /**
     * Preprocess the text
     * @param color The name of the color
     * @param value Hope to output the text
     * @return Handled text
     */
    private fun diy(color: String, value: String): String {
        return if (Logger.configuration.color) {
            Ansi.ansi().eraseScreen().render("@|$color $value|@").toString()
        } else {
            value
        }
    }


}
