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
