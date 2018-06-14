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
package net.cloudopt.next.validator

import java.util.HashSet
import javax.validation.ConstraintViolation
import javax.validation.Validation

/*
 * @author: Cloudopt
 * @Time: 2018/6/14
 * @Description: Simplified check
 */
object Validator {

    @JvmOverloads
    fun validate(obj: Any, keys: Array<String> = arrayOf<String>()): String {
        val buffer = StringBuffer(64)
        val validator = Validation.buildDefaultValidatorFactory()
                .validator

        return if (keys.size <= 0) {
            val constraintViolations = validator.validate(obj)
            val iter = constraintViolations.iterator()
            while (iter.hasNext()) {
                val c = iter.next() as ConstraintViolation<*>
                buffer.append(c.message)
            }
            buffer.toString()
        } else {
            val constraintViolations = HashSet<ConstraintViolation<Any>>()
            for (key in keys) {
                val it = validator.validateProperty(obj, key).iterator()
                if (it.hasNext()) {
                    constraintViolations.add(it.next())
                }

            }
            val iter = constraintViolations.iterator()
            while (iter.hasNext()) {
                val c = iter.next() as ConstraintViolation<*>
                buffer.append(c.message)
            }
            buffer.toString()
        }

    }


}