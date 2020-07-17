/*
 * Copyright 2017-2020 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
            return (ub === Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub === Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                    || ub === Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || ub === Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                    || ub === Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || ub === Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                    || ub === Character.UnicodeBlock.GENERAL_PUNCTUATION)
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
