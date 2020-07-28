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
package net.cloudopt.next.example.ymal

import net.cloudopt.next.i18n.I18N
import net.cloudopt.next.logging.Logger
import org.junit.Test

/*
 * @author: Cloudopt
 * @Time: 2018/5/22
 * @Description: Test Case
 */
class TestCase {

    val logger = Logger.getLogger(TestCase::class.java)

    @Test
    fun getDefaultLocale() {
        logger.info(I18N.getI18nJsonObject().toString())
    }

    @Test
    fun i18n1Level() {
        logger.info(I18N.i18n("title") ?: "Not Found")
    }

    @Test
    fun i18n1LevelByEnglish() {
        logger.info(I18N.i18n("title", "en") ?: "Not Found")
    }

    @Test
    fun i18n3Level() {
        logger.info(I18N.i18n("meta.author.name") ?: "Not Found")
    }

}
