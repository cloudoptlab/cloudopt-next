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
package net.cloudopt.next.web.test;

import io.vertx.core.eventbus.EventBus;
import net.cloudopt.next.web.CloudoptServer;
import net.cloudopt.next.web.config.ConfigManager;
import net.cloudopt.next.web.event.EventPlugin;
import net.cloudopt.next.web.render.FreemarkerRender;
import net.cloudopt.next.web.test.handler.TestHandler;
import net.cloudopt.next.web.test.plugin.TestPlugin;

/*
 * @author: t-baby
 * @Time: 2018/1/27
 * @Description: net.cloudopt.next.web.test
 */
public class TestJavaCase {

    public static void main(String[] args){
        CloudoptServer.addHandler(new TestHandler());
        CloudoptServer.addPlugin(new TestPlugin());
        CloudoptServer.addPlugin(new EventPlugin());
        CloudoptServer.run(TestJavaCase.class);
    }

}
