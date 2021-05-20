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
package net.cloudopt.next.spring

import net.cloudopt.next.logging.Logger
import org.springframework.beans.factory.support.BeanDefinitionBuilder
import org.springframework.beans.factory.support.DefaultListableBeanFactory
import org.springframework.context.ConfigurableApplicationContext

/*
 * @author: Cloudopt
 * @Time: 2018/2/7
 * @Description: Spring Builder
 */

object SpringBuilder {

    private val logger = Logger.getLogger(SpringBuilder::class)

    @JvmStatic
    private var context: ConfigurableApplicationContext? = null

    fun getContext(): ConfigurableApplicationContext? {
        return SpringBuilder.context
    }

    fun setContext(context: ConfigurableApplicationContext?) {
        if (context == null) {
            logger.error("[SPRING PLUGIN] Could not found context for spring.")
            return
        }
        SpringBuilder.context = context
        SpringHolder.alive = true
    }

    fun refreshContext() {
        if (SpringHolder.alive) {
            SpringBuilder.context!!.refresh()
        }
    }

    fun removeContext() {
        if (SpringHolder.alive) {
            SpringBuilder.context!!.close()
            SpringBuilder.context = null
            SpringHolder.alive = false
        }
    }

    fun register(clazz: Class<*>) {
        val context = getContext()
        if (context != null) {
            val beanFactory = context.beanFactory as DefaultListableBeanFactory
            val beanName = firstLowerCase(clazz.simpleName)
            beanFactory.registerBeanDefinition(beanName, BeanDefinitionBuilder.rootBeanDefinition(clazz).beanDefinition)
        }
    }

    fun registerSingleton(clazz: Class<*>) {
        try {
            registerSingleton(clazz, clazz.newInstance())
        } catch (e: InstantiationException) {
            logger.error(e.message ?: "", e)
        } catch (e: IllegalAccessException) {
            logger.error(e.message ?: "", e)
        }

    }

    fun registerSingleton(clazz: Class<*>, bean: Any) {
        val context = getContext()
        if (context != null) {
            val beanFactory = context.beanFactory as DefaultListableBeanFactory
            val beanName = firstLowerCase(clazz.simpleName)
            beanFactory.registerSingleton(beanName, bean)
        }
    }

    fun <T> getBean(clazz: Class<T>): T? {
        val context = getContext()
        return context?.getBean(clazz)
    }

    fun firstLowerCase(name: String): String {
        val items = name.toByteArray()
        items[0] = (items[0].toInt().toChar().code + ('a' - 'A')).toByte()
        return String(items)
    }
}
