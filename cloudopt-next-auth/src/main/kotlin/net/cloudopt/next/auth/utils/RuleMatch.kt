/*
 * Copyright 2017-2020 Cloudopt.
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

package net.cloudopt.next.auth.utils

import net.cloudopt.next.auth.bean.Rule

object RuleMatch {

    /**
     * Used to match the consistency between url and rules list
     * @param url User access routes
     * @param method GET, POST, PUT, DELETE or ...
     * @param ruleList MutableList<Rule>
     * @see Rule
     * @return As long as there is a rule that matches, it returns True.
     * If there is no rule that matches, it returns False.
     */
    fun ruleMatch(url: String, method: String, ruleList: MutableList<Rule>): Boolean {
        for (rule in ruleList) {
            if (ruleMatch(url, method, rule)) {
                return true
            }
        }
        return false
    }

    /**
     * Used to match the consistency between url and rule
     * @param url User access routes
     * @param method GET, POST, PUT, DELETE or ...
     * @param rule Rule
     * @see Rule
     * @return As long as there is a rule that matches, it returns True.
     * If there is no rule that matches, it returns False.
     */
    fun ruleMatch(url: String, method: String, rule: Rule): Boolean {
        var methods: List<String> = if (rule.method.contains("||")) {
            rule.method.split("||")
        } else if (rule.method.equals("*")) {
            mutableListOf("GET", "POST", "PUT", "DELETE", "PATCH")
        } else {
            mutableListOf(rule.method)
        }

        return BuiltinOperators.keyMatch2(url, rule.url) && method in methods && rule.allow
    }

}