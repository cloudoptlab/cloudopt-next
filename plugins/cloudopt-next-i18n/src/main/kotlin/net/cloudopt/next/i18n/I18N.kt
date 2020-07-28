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
package net.cloudopt.next.i18n

import net.cloudopt.next.json.Jsoner
import net.cloudopt.next.utils.Resourcer
import java.util.*
import kotlin.collections.HashMap


/*
 * @author: Cloudopt
 * @Time: 2018/1/4
 * @Description: To help improve internationalization.
 */
object I18N {

    private val i18nCache = mutableMapOf<String, MutableMap<String, Any>>()

    /**
     * The default filename prefix.
     */
    var baseName: String = ""
        set(value) {
            if (value.isBlank()) {
                throw IllegalArgumentException("BaseName can not be blank.")
            }
            field = value
        }

    /**
     * The default language name. When the specified language file cannot be obtained,
     * the file with the default language name will be obtained.
     */
    var defaultLocale: String = Locale.getDefault().getLanguage() + "_" + Locale.getDefault().getCountry()
        set(value) {
            if (value.isBlank()) {
                throw IllegalArgumentException("DefaultLocale can not be blank.")
            }
            field = value
        }

    /**
     * The default folder name.
     */
    var defaultFolder: String = "_locales"
        set(value) {
            if (value.isBlank()) {
                throw IllegalArgumentException("DefaultFolder can not be blank.")
            }
            field = value
        }

    /**
     * Get the json object of the specified language file.
     * JSON objects generally inherit the MAP class, so return map objects.
     * @param locale specified language name.
     * @return MutableMap<String, Any>
     */
    @JvmStatic
    @JvmOverloads
    fun getI18nJsonObject(locale: String = defaultLocale): MutableMap<String, Any> {
        if (i18nCache[locale] == null) {
            val fileName = if (baseName.isNotBlank()) {
                defaultFolder + "/" + baseName + "_" + locale + ".json"
            } else {
                defaultFolder + "/" + locale + ".json"
            }
            if (Resourcer.exist(fileName)) {
                val json = Jsoner.read(fileName)
                i18nCache[locale] = json
                return json
            } else {
                throw IllegalArgumentException("$fileName is not found!")
            }
        } else {
            return i18nCache[locale] ?: HashMap<String, Any>()
        }
    }

    /**
     * Get a translation of the specified name from the specified language file.
     * @param key the specified name
     * @param locale the specified language file
     * @return String?
     */
    @JvmStatic
    @JvmOverloads
    fun i18n(key: String, locale: String = defaultLocale): String? {
        val arr = key.split(".")
        var jsonObject: MutableMap<String, Any> = getI18nJsonObject(locale)
        val maxSize = arr.size - 1
        for (i in arr.indices) {
            if (i != maxSize) {
                jsonObject = jsonObject[arr[i]] as MutableMap<String, Any>
            } else {
                return jsonObject.get(arr[i]) as String?
            }
        }
        return null
    }

}
