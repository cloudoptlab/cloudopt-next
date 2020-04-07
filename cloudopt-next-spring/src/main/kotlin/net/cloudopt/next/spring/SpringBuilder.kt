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

    private val logger = Logger.getLogger(SpringBuilder::class.java)

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
        items[0] = (items[0].toChar().toInt() + ('a' - 'A')).toByte()
        return String(items)
    }
}
