/*
 * Copyright 2017-2020 Cloudopt.
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
package net.cloudopt.next.auth.utils

import java.util.regex.Pattern

/**
 * Implementation of reference caspin project.
 * https://github.com/casbin/jcasbin
 */
object BuiltinOperators {
    private val keyMatch2Pattern: Pattern = Pattern.compile("(.*):[^/]+(.*)")
    private val keyMatch3Pattern: Pattern = Pattern.compile("(.*)\\{[^/]+\\}(.*)")

    /**
     * keyMatch determines whether key1 matches the pattern of key2 (similar to RESTful path), key2 can contain a *.
     * For example, "/foo/bar" matches "/foo/ *"
     *
     * @param key1 the first argument.
     * @param key2 the second argument.
     * @return whether key1 matches key2.
     */
    fun keyMatch(key1: String, key2: String): Boolean {
        val i = key2.indexOf('*')
        if (i == -1) {
            return key1 == key2
        }
        return if (key1.length > i) {
            key1.substring(0, i) == key2.substring(0, i)
        } else key1 == key2.substring(0, i)
    }

    /**
     * keyMatch2 determines whether key1 matches the pattern of key2 (similar to RESTful path), key2 can contain a *.
     * For example, "/foo/bar" matches "/foo/ *", "/resource1" matches "/:resource"
     *
     * @param key1 the first argument.
     * @param key2 the second argument.
     * @return whether key1 matches key2.
     */
    fun keyMatch2(key1: String?, key2: String): Boolean {
        var key2 = key2
        key2 = key2.replace("/*", "/.*")
        while (true) {
            if (!key2.contains("/:")) {
                break
            }
            key2 = "^" + keyMatch2Pattern.matcher(key2).replaceAll("$1[^/]+$2").toString() + "$"
        }
        return regexMatch(key1, key2)
    }

    /**
     * keyMatch3 determines whether key1 matches the pattern of key2 (similar to RESTful path), key2 can contain a *.
     * For example, "/foo/bar" matches "/foo/ *", "/resource1" matches "/{resource}"
     *
     * @param key1 the first argument.
     * @param key2 the second argument.
     * @return whether key1 matches key2.
     */
    fun keyMatch3(key1: String?, key2: String): Boolean {
        var key2 = key2
        key2 = key2.replace("/*", "/.*")
        while (true) {
            if (!key2.contains("/{")) {
                break
            }
            key2 = keyMatch3Pattern.matcher(key2).replaceAll("$1[^/]+$2")
        }
        return regexMatch(key1, key2)
    }

    /**
     * regexMatch determines whether key1 matches the pattern of key2 in regular expression.
     *
     * @param key1 the first argument.
     * @param key2 the second argument.
     * @return whether key1 matches key2.
     */
    private fun regexMatch(key1: String?, key2: String?): Boolean {
        return Pattern.matches(key2, key1)
    }

}