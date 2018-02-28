package net.cloudopt.next.utils

/*
 * @author: t-baby
 * @Time: 2018/2/28
 * @Description: net.cloudopt.next.utils
 */
class Test {

    @Throws(IllegalAccessException::class, InstantiationException::class)
    fun a() {
        Test::class.java.newInstance()
    }

}
