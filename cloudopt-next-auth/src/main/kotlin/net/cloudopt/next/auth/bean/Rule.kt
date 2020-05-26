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

/**
 * Used to record verification rules.
 * @property name rule's name
 * @property url path of network request access
 * @property method metho of network request access, maybe GET or POST...
 * @property allow Whether to allow network requests to pass
 * @constructor
 */
data class Rule(
    var name:String="",
    var url:String="",
    var method:String="",
    var allow:Boolean = true
)