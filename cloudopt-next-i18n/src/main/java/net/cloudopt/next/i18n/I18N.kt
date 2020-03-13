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
package net.cloudopt.next.i18n

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import net.cloudopt.next.utils.Resourcer
import java.util.*


/*
 * @author: Cloudopt
 * @Time: 2018/1/4
 * @Description: Used to read yaml
 */
object I18N {

    private val i18nCache = mutableMapOf<String, JSONObject>()

    var baseName: String = ""
        get() = field
        set(value) {
            if (value.isBlank()) {
                throw IllegalArgumentException("baseName can not be blank.")
            }
            field = "$value"
        }

    var defaultLocale: String = Locale.getDefault().getLanguage() + "_" + Locale.getDefault().getCountry()
        get() = field
        set(value) {
            if (value.isBlank()) {
                throw IllegalArgumentException("defaultLocale can not be blank.")
            }
            field = "$value"
        }

    var defaultFolder: String = "_locales"
        get() = field
        set(value) {
            if (value.isBlank()) {
                throw IllegalArgumentException("defaultFolder can not be blank.")
            }
            field = "$value"
        }

    @JvmStatic
    @JvmOverloads
    fun getI18nJsonObject(locale: String = defaultLocale): JSONObject {
        if (i18nCache.get(locale) == null) {
            val fileName = if (baseName.isNotBlank()) {
                defaultFolder + "/" + baseName + "_" + locale + ".json"
            } else {
                defaultFolder + "/" + locale + ".json"
            }
            if (Resourcer.exist(fileName)) {
                val json = JSON.parseObject(Resourcer.getFileString(fileName, true))
                i18nCache.set(locale, json)
                return json
            } else {
                throw IllegalArgumentException("$fileName is not found!")
            }
        } else {
            return i18nCache.get(locale) ?: JSONObject()
        }
    }

    @JvmStatic
    @JvmOverloads
    fun i18n(key: String, locale: String = defaultLocale): String? {
        val arr = key.split(".")
        var jsonObject = getI18nJsonObject(locale)
        val maxSize = arr.size - 1
        for (i in arr.indices) {
            if (i != maxSize) {
                jsonObject = jsonObject.getJSONObject(arr[i])
            } else {
                return jsonObject.getString(arr[i])
            }
        }
        return null
    }

}
