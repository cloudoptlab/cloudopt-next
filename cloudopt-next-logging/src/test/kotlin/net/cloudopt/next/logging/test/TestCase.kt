/*
 * Copyright 2017-2021 Cloudopt
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
package net.cloudopt.next.logging.test

import net.cloudopt.next.logging.test.provider.JdkLoggerProvider
import net.cloudopt.next.logging.test.provider.Slf4jLoggerProvider
import org.junit.Test
import org.junit.jupiter.api.assertDoesNotThrow

class TestCase {

    private val e = RuntimeException()

    @Test
    fun testJdkProvider() {
        Logger.configuration.loggerProvider = JdkLoggerProvider()
        val logger = Logger.getLogger(TestCase::class)
        assertDoesNotThrow {

            logger.isDebugEnabled()
            logger.isInfoEnabled()
            logger.isWarnEnabled()
            logger.isErrorEnabled()

            logger.debug("debug")
            logger.info("info")
            logger.warn("warn")
            logger.error("error")

            logger.debug("Hello {}", "World")
            logger.info("Hello {}", "World")
            logger.warn("Hello {}", "World")
            logger.error("Hello {}", "World")

            logger.debug("", e)
            logger.info("", e)
            logger.warn("", e)
            logger.error("", e)

            logger.debug("", e, 1)
            logger.info("", e, 1)
            logger.warn("", e, 1)
            logger.error("", e, 1)
        }
    }

    @Test
    fun testSlf4jProvider() {
        Logger.configuration.loggerProvider = Slf4jLoggerProvider()
        val logger = Logger.getLogger("net.cloudopt.next.logging.test.TestCase")
        assertDoesNotThrow {

            logger.isDebugEnabled()
            logger.isInfoEnabled()
            logger.isWarnEnabled()
            logger.isErrorEnabled()

            logger.debug("debug")
            logger.info("info")
            logger.warn("warn")
            logger.error("error")

            logger.debug("Hello {}", "World")
            logger.info("Hello {}", "World")
            logger.warn("Hello {}", "World")
            logger.error("Hello {}", "World")

            logger.debug("Loading {}", "100%")
            logger.info("Loading {}", "100%")
            logger.warn("Loading {}", "100%")
            logger.error("Loading {}", "100%")

            logger.debug("", e)
            logger.info("", e)
            logger.warn("", e)
            logger.error("", e)

            logger.debug("", e, 1)
            logger.info("", e, 1)
            logger.warn("", e, 1)
            logger.error("", e, 1)
        }
    }

}
