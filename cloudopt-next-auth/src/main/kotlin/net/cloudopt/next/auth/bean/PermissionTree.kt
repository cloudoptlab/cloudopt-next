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
 * Permission tree for caching
 * @property roles LinkedList<Role>
 * @see Role
 * @property groups LinkedList<Group>
 * @see Group
 * @property users LinkedList<User>
 * @see User
 * @constructor
 */
data class PermissionTree(
    var roles: LinkedList<Role> = LinkedList(),
    var groups: LinkedList<Group> = LinkedList(),
    var users: LinkedList<User> = LinkedList()
)