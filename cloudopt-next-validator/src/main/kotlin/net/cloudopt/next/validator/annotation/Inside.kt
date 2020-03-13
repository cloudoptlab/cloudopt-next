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
package net.cloudopt.next.validator.annotation

import net.cloudopt.next.validator.InsideValidator
import java.lang.annotation.Documented
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy.RUNTIME
import javax.validation.Constraint
import javax.validation.Payload
import kotlin.reflect.KClass

/*
 * @author: Cloudopt
 * @Time: 2018/6/14
 * @Description: Is used to verify whether a text is in an array
 */
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.FIELD,
    AnnotationTarget.ANNOTATION_CLASS
)
@Retention(RUNTIME)
@Constraint(validatedBy = arrayOf(InsideValidator::class))
@Documented
annotation class Inside(
    vararg val value: String,
    val message: String = "{constraints.inside.message}",
    val groups: Array<KClass<*>> = arrayOf(),
    val payload: Array<KClass<out Payload>> = arrayOf()
)

