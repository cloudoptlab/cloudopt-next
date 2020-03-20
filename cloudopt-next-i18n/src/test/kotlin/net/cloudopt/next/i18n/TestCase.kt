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
