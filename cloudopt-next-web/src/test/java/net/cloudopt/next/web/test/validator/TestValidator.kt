package net.cloudopt.next.web.test.validator

import net.cloudopt.next.web.Resource
import net.cloudopt.next.web.Validator


/*
 * @author: Cloudopt
 * @Time: 2018/2/28
 * @Description: Test Case
 */
class TestValidator : Validator {


    override fun validate(resource: Resource): Boolean {
        return true
    }

    override fun error(resource: Resource) {

    }


}