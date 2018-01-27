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
package net.cloudopt.next.web.test.controller

import net.cloudopt.next.web.Resource
import net.cloudopt.next.web.route.API
import net.cloudopt.next.web.route.GET


/*
 * @author: Cloudopt
 * @Time: 2018/1/26
 * @Description: Test Controller
 */
@API("/")
class IndexController: Resource() {

    @GET
    fun index(){
        renderText(getParam<String>("name") as String)
    }

}