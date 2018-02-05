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
package net.cloudopt.next.web.test.event

import io.vertx.core.eventbus.Message
import net.cloudopt.next.web.event.AutoEvent
import net.cloudopt.next.web.event.EventListener


/*
 * @author: Cloudopt
 * @Time: 2018/2/5
 * @Description: Test Case
 */
@AutoEvent("net.cloudopt.web.test")
class TestEventListener:EventListener {
    override fun listener(message: Message<Any>) {
        print(message.body())
    }
}