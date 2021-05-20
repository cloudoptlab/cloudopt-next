package net.cloudopt.next.waf

import net.cloudopt.next.waf.injection.MongoInjection
import net.cloudopt.next.waf.injection.SQLInjection
import net.cloudopt.next.waf.injection.XSSInjection
import org.junit.Test

/*
 * @author: Cloudopt
 * @Time: 2018/1/9
 * @Description: Test Case
 */
class TestCase {

    @Test
    fun testMongoInjection() {
        var mongo = MongoInjection()
        println(mongo.filter("test'});return {username:1,password:2}//"))
    }

    @Test
    fun testSQLInjection() {
        var sql = SQLInjection()
        println(sql.filter("'  2、and 1=1  3、 and 1=2 "))
    }

    @Test
    fun testXSSInjection() {
        var xss = XSSInjection()
        println(xss.filter("hello<script>alert('Vulnerable')</script>"))
    }

}