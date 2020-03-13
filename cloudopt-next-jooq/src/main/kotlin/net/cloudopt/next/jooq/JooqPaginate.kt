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

import org.jooq.SelectConditionStep

/*
 * @author: Cloudopt
 * @Time: 2018/4/5
 * @Description: Pagination
 */
class JooqPaginate(query: SelectConditionStep<*>, private var count: Int, private val page: Int) {

    private var totalPage: Int = 0
    private val totalRow: Long
    private var firstPage = false
    private var lastPage = false
    private lateinit var query: SelectConditionStep<*>

    init {
        this.query = query
        this.totalRow = count().toLong()
        this.totalPage = (totalRow / count.toLong()).toInt()
        if (totalRow % count.toLong() != 0L) {
            ++totalPage
        }

        if (count > totalRow) {
            this.count = totalRow.toInt()
        }

        if (totalRow != 0L && this.count <= 0 || this.page <= 0) {
            throw RuntimeException("JooqPage tips: (づ￣ 3￣)づ count or page is error !")
        }

        this.firstPage = this.page == 1
        this.lastPage = this.page == this.totalPage

    }

    fun <T> find(clazz: Class<T>): JooqPage {
        return JooqPage(count, page, totalPage, totalRow, firstPage, lastPage, query.limit(this.count)?.offset(skip())?.fetchInto(clazz)?.toMutableList()
                ?: mutableListOf<Any>())
    }


    private fun skip(): Int {
        return (page - 1) * count
    }

    fun count(): Int {
        return this.query.count()
    }


}