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

import net.cloudopt.next.validator.annotation.Type
import java.util.regex.Pattern
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

/*
 * @author: Cloudopt
 * @Time: 2018/6/14
 * @Description: Used to verify whether it is this type
 */
class TypeValidator : ConstraintValidator<Type, String> {

    private var value = "string"

    override fun initialize(type: Type) {
        value = type.value
    }

    override fun isValid(s: String, constraintValidatorContext: ConstraintValidatorContext): Boolean {
        return when (value) {
            "int" -> {
                isInt(s)
            }
            "double" -> {
                isDouble(s)
            }
            "boolean" -> {
                isDouble(s)
            }
            else -> {
                true
            }
        }
    }

    private fun isInt(s: String): Boolean {
        val mer = Pattern.compile("^[+-]?[0-9]+$").matcher(s)
        return mer.find()
    }

    private fun isDouble(s: String): Boolean {
        val mer = Pattern.compile("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$").matcher(s)
        return mer.find()
    }

    private fun isBoolean(s: String): Boolean {
        return s == "true" || s == "false"
    }


}
