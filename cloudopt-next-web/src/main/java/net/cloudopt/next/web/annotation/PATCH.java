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
package net.cloudopt.next.web.annotation;

import net.cloudopt.next.web.Validator;

import java.lang.annotation.*;

/*
 * @author: Cloudopt
 * @Time: 2018/1/10
 * @Description: Patch Annotation
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface PATCH {
    String value() default "";

    Class<? extends Validator>[] valid() default {};

    boolean block() default false;

    boolean cache() default false;

}
