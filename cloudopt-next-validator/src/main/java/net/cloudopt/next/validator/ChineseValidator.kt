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
package net.cloudopt.next.validator

import net.cloudopt.next.validator.annotation.Chinese

import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

/*
 * @author: Cloudopt
 * @Time: 2018/6/14
 * @Description: It is used to verify whether it is Chinese
 */
class ChineseValidator : ConstraintValidator<Chinese, String> {

    private var value = false

    override fun initialize(chinese: Chinese) {
        value = chinese.value
    }

    override fun isValid(s: String, constraintValidatorContext: ConstraintValidatorContext): Boolean {
        return if (value) {
            isChinese(s)
        } else {
            !isChinese(s)
        }
    }

    companion object {

        private fun isChinese(c: Char): Boolean {
            val ub = Character.UnicodeBlock.of(c)
            return if (ub === Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub === Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                    || ub === Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || ub === Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                    || ub === Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || ub === Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                    || ub === Character.UnicodeBlock.GENERAL_PUNCTUATION) {
                true
            } else false
        }

        fun isChinese(strName: String): Boolean {
            val ch = strName.toCharArray()
            for (i in ch.indices) {
                val c = ch[i]
                if (isChinese(c)) {
                    return true
                }
            }
            return false
        }
    }
}
