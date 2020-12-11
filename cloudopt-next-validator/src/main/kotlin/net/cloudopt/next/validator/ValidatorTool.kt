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
package net.cloudopt.next.validator

import org.hibernate.validator.HibernateValidator
import java.util.*
import javax.validation.ConstraintViolation
import javax.validation.Validation
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.jvm.javaMethod

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
     * Validates all constraints on {@code object}.
     *
     * @param obj object to validate
     * @param groups the group or list of groups targeted for validation (defaults to
     *        {@link Default})
     * @param <T> the type of the object to validate
     * @return constraint violations or an empty set if none
     * @throws IllegalArgumentException if object is {@code null}
     *         or if {@code null} is passed to the varargs groups
     * @throws ValidationException if a non recoverable error happens
     *         during the validation process
     */
    fun validateGroup(obj: Any, vararg groups: Class<*>): ValidatorResult {
        val violations = validator.validate(obj, *groups)
        return if (violations.isEmpty()) {
            ValidatorResult(true, "")
        } else {
            ValidatorResult(false, violations.first().message)
        }
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
    fun validateParameters(
        any: Any,
        method: KFunction<*>,
        parameterValues: MutableMap<KParameter, Any?>
    ): ValidatorResult {
        val violations =
            executableValidator.validateParameters(any, method.javaMethod, parameterValues.values.toTypedArray())
        return if (violations.isEmpty()) {
            ValidatorResult(true, "")
        } else {
            ValidatorResult(false, violations.first().message)
        }
    }


}