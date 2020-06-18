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

import org.hibernate.validator.HibernateValidator
import java.util.*
import javax.validation.ConstraintViolation
import javax.validation.Validation

/*
 * @author: Cloudopt
 * @Time: 2018/6/14
 * @Description: Simplified check
 */

data class ValidatorResult(var result: Boolean = true, var message: String = "")

object ValidatorTool {

    private val validator = Validation.byProvider(HibernateValidator::class.java).configure().buildValidatorFactory()
        .validator

    private val validForExecutables =
        Validation.byProvider(HibernateValidator::class.java).configure().buildValidatorFactory()
            .validator.forExecutables()

    /**
     * Check parameters with comments on fields
     * @param obj Any object
     * @param args Checking only some fields
     * @return ValidatorResult
     */
    @JvmOverloads
    fun validate(obj: Any, vararg args: String): ValidatorResult {
        if (args.isEmpty()) {
            val constraintViolations = validator.validate(obj)
            val iter = constraintViolations.iterator()
            if (iter.hasNext()) {
                val c = iter.next() as ConstraintViolation<*>
                return ValidatorResult(false, c.message)
            }
        } else {
            val constraintViolations = HashSet<ConstraintViolation<Any>>()
            for (key in args) {
                val it = validator.validateProperty(obj, key).iterator()
                if (it.hasNext()) {
                    constraintViolations.add(it.next())
                }

            }
            val iter = constraintViolations.iterator()
            if (iter.hasNext()) {
                val c = iter.next() as ConstraintViolation<*>
                return ValidatorResult(false, c.message)
            }
        }
        return ValidatorResult(true, "")
    }


}