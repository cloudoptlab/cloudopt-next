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