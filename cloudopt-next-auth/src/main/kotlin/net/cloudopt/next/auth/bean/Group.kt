/*
 * Copyright 2017-2020 Cloudopt.
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
package net.cloudopt.next.auth.bean

import java.util.*

/**
 * Used to record verification groups.
 * @property id group id
 * @property name group name
 * @property rules LinkedList<Rule>
 * @see Rule
 * @constructor
 */
data class Group(
    var id:Int = 0,
    var name:String = "",
    var rules: LinkedList<Rule> = LinkedList()
)