package net.cloudopt.next.web.test;

import io.vertx.core.eventbus.EventBus;
import net.cloudopt.next.web.CloudoptServer;
import net.cloudopt.next.web.config.ConfigManager;
import net.cloudopt.next.web.event.EventPlugin;
import net.cloudopt.next.web.render.FreemarkerRender;
import net.cloudopt.next.web.test.plugin.TestPlugin;

/*
 * @author: t-baby
 * @Time: 2018/1/27
 * @Description: net.cloudopt.next.web.test
 */
public class TestJavaCase {

    public static void main(String[] args){
        CloudoptServer.addPlugin(new TestPlugin());
        CloudoptServer.addPlugin(new EventPlugin());
        CloudoptServer.run(TestJavaCase.class);
    }

}
