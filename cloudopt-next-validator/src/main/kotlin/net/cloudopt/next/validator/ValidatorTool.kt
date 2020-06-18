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
import java.lang.reflect.Method
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

    @JvmStatic
    private val validator = Validation.byProvider(HibernateValidator::class.java).configure().buildValidatorFactory()
        .validator

    @JvmStatic
    private val executableValidator =
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
            if (constraintViolations.isNotEmpty()) {
                return ValidatorResult(false, constraintViolations.first().message)
            }
        } else {
            val constraintViolations = HashSet<ConstraintViolation<Any>>()
            for (key in args) {
                val it = validator.validateProperty(obj, key).iterator()
                if (it.hasNext()) {
                    constraintViolations.add(it.next())
                }

            }
            if (constraintViolations.isNotEmpty()) {
                return ValidatorResult(false, constraintViolations.first().message)
            }
        }
        return ValidatorResult(true, "")
    }

    /**
     * Validates all constraints placed on the parameters of the given method.
     *
     * @param any the object on which the method to validate is invoked
     * @param method the method for which the parameter constraints is validated
     * @param parameterValues the values provided by the caller for the given method's
     *        parameters
     * @return ValidatorResult with the constraint violations caused by this validation;
     *         the message will be empty if no error occurs, but never {@code null}
     * @throws IllegalArgumentException if {@code null} is passed for any of the parameters
     *         or if parameters don't match with each other
     */
    fun validateParameters(any:Any, method:Method, parameterValues: Array<Any>): ValidatorResult {
        val violations = executableValidator.validateParameters(any,method, parameterValues)
        return if (violations.isEmpty()){
            ValidatorResult(true, "")
        }else{
            ValidatorResult(false, violations.first().message)
        }
    }



}