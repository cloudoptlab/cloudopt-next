/*
 * Copyright 2017-2021 Cloudopt
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