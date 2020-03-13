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
package net.cloudopt.next.example.ymal
import net.cloudopt.next.utils.Maper
import net.cloudopt.next.yaml.Yamler
import org.junit.Test
import java.util.jar.Manifest

/*
 * @author: Cloudopt
 * @Time: 2018/1/9
 * @Description: Test Case
 */
class TestCase {

    @Test
    fun test() {
        println(Yamler.read("application.yml","net.cloudopt.next"))
        var yaml:YamlBean= Yamler.read(YamlBean::class) as YamlBean
        //If you want to use the kotlin data class in the JVM,
        // the parameters of the data class must have default values.
        println(yaml.toString())
    }

    @Test
    fun testReadFileList(){

        var map = linkedMapOf<String, Any>()

        if (Yamler.read("application.yml") != null) {
            map.putAll(Yamler.read("application.yml") as Map<out String, Any>)
        }

        if (Yamler.read("application-dev.yml") != null) {
            map.putAll(Yamler.read("application-dev.yml") as Map<out String, Any>)
        }

        if (Yamler.read("application-pro.yml") != null) {
            map.putAll(Yamler.read("application-pro.yml") as Map<out String, Any>)
        }


        println(map.toString())
    }

}
