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
package net.cloudopt.next.web.health

import net.cloudopt.next.json.Jsoner.json
import net.cloudopt.next.web.Resource
import net.cloudopt.next.web.Validator

/**
 * If a password is set, the authenticator will be automatically registered, and after the authenticator takes effect,
 * the password must be passed to access the api.
 */
class HealthChecksPasswordValidator : Validator {

    override suspend fun validate(resource: Resource): Boolean {
        return !(HealthChecksManager.config.password.isNotEmpty()
                && HealthChecksManager.config.password != resource.getParam("password"))
    }

    override suspend fun error(resource: Resource) {
        resource.response.statusCode = 401
        resource.renderJson(
            json(
                "error" to 401,
                "errorMessage" to "No access rights"
            )
        )
    }


}