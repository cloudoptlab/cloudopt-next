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
package net.cloudopt.next.validation

import net.cloudopt.next.validation.annotation.Type

import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import java.util.regex.Matcher
import java.util.regex.Pattern

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
        return if (value == "int") {
            isInt(s)
        } else if (value == "double") {
            isDouble(s)
        } else if (value == "boolean") {
            isDouble(s)
        } else {
            true
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
