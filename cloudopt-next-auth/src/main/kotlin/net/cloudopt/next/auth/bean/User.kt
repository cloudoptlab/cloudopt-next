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
 * Used to record verification users.
 * @property id user id
 * @property unique_tag unique ID of the user in the business system
 * @property rolesIdList list of role ids to which the user belongs
 * @property groupsIdList list of group ids to which the user belongs
 * @property rules LinkedList<Rule>
 * @see Rule
 * @constructor
 */
data class User(
    var id:Int = 0,
    var uniqueTag:String = "",
    var rolesIdList:List<Int> = mutableListOf(),
    var groupsIdList:List<Int> = mutableListOf(),
    var rules: LinkedList<Rule> = LinkedList()
)