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
package net.cloudopt.next.waf

import java.util.regex.Pattern

/*
 * @author: Cloudopt
 * @Time: 2018/1/5
 * @Description: XXS anti-injection module
 */
class XSSInjection : Filter {

    /**
     * @param value Pending content
     * @return
     * @Description Peel dangerous content
     */
    override fun filter(value: String): String {
        var s = value

        // Avoid null characters
        s = value.replace("".toRegex(), "")

        // Avoid anything between script tags
        var scriptPattern = Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE)
        s = scriptPattern.matcher(s).replaceAll("")

        // Avoid anything in a src='...' type of expression
        scriptPattern = Pattern.compile(
            "src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE
                    or Pattern.MULTILINE or Pattern.DOTALL
        )
        s = scriptPattern.matcher(s).replaceAll("")

        scriptPattern = Pattern.compile(
            "src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", Pattern.CASE_INSENSITIVE
                    or Pattern.MULTILINE or Pattern.DOTALL
        )
        s = scriptPattern.matcher(s).replaceAll("")

        // Remove any lonesome </script> tag
        scriptPattern = Pattern.compile("</script>", Pattern.CASE_INSENSITIVE)
        s = scriptPattern.matcher(s).replaceAll("")

        // Remove any lonesome <script ...> tag
        scriptPattern = Pattern.compile(
            "<script(.*?)>", Pattern.CASE_INSENSITIVE
                    or Pattern.MULTILINE or Pattern.DOTALL
        )
        s = scriptPattern.matcher(s).replaceAll("")

        // Avoid eval(...) expressions
        scriptPattern = Pattern.compile(
            "eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE
                    or Pattern.MULTILINE or Pattern.DOTALL
        )
        s = scriptPattern.matcher(s).replaceAll("")

        // Avoid expression(...) expressions
        scriptPattern = Pattern.compile(
            "expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE
                    or Pattern.MULTILINE or Pattern.DOTALL
        )
        s = scriptPattern.matcher(s).replaceAll("")

        // Avoid javascript:... expressions
        scriptPattern = Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE)
        s = scriptPattern.matcher(s).replaceAll("")

        // Avoid vbscript:... expressions
        scriptPattern = Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE)
        s = scriptPattern.matcher(s).replaceAll("")

        // Avoid onload= expressions
        scriptPattern = Pattern.compile(
            "onload(.*?)=", Pattern.CASE_INSENSITIVE
                    or Pattern.MULTILINE or Pattern.DOTALL
        )
        s = scriptPattern.matcher(s).replaceAll("")

        return s

    }

}
