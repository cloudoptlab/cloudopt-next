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
package net.cloudopt.next.jooq

import java.io.Serializable

/*
 * @author: Cloudopt
 * @Time: 2018/4/5
 * @Description: Pagination
 */
data class JooqPage(
    val count: Int = 0,
    val page: Int = 0,
    val totalPage: Int = 0,
    val totalRow: Long = 0,
    val firstPage: Boolean = true,
    val lastPage: Boolean = true,
    var list: MutableList<*> = mutableListOf<Any>()
) : Serializable
