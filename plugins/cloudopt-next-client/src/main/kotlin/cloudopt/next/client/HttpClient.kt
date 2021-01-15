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
package cloudopt.next.client

import io.vertx.core.buffer.Buffer
import io.vertx.ext.web.client.HttpRequest
import io.vertx.ext.web.client.WebClient
import io.vertx.ext.web.client.WebClientOptions
import net.cloudopt.next.web.NextServer
import net.cloudopt.next.web.Worker


/*
 * @author: Cloudopt
 * @Time: 2018/1/31
 * @Description: HTTP request client, a simple encapsulation of vertx client.
 */
class HttpClient() {

    private var client = WebClient.create(Worker.vertx)

    private var host = ""

    private var port = 80

    private var request: HttpRequest<Buffer>? = null

    private var timeout: Long = 5000

    private var followRedirects: Boolean = true


    constructor(host: String, options: WebClientOptions = WebClientOptions()) : this() {
        if (host.startsWith("https://")) {
            options.isSsl = true
            this.port = 443
        }
        this.host = host
        this.host = this.host.replace("http://", "")
        this.host = this.host.replace("https://", "")
        options.userAgent =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.119 Safari/537.36"
        options.isKeepAlive = false
        options.isFollowRedirects = followRedirects
        client = WebClient.create(Worker.vertx, options)
    }

    /**
     * Configure the request to use a new port.
     * @param port New port
     * @return HttpClient
     */
    fun setPort(port: Int): HttpClient {
        this.port = port
        return this
    }

    /**
     * Configures the amount of time in milliseconds after which if the request does not return any data within the timeout
     * period an {@see java.util.concurrent.TimeoutException} fails the request.
     * @param timeout the quantity of time in milliseconds
     * @return HttpClient
     */
    fun setTimeout(timeout: Long): HttpClient {
        this.timeout = timeout
        return this
    }

    /**
     * Configure the request to add multiple HTTP headers.
     * @param key the HTTP header's name
     * @param value the HTTP header's value
     * @return HttpClient
     */
    fun addHeader(key: String, value: String): HttpClient {
        this.request?.putHeader(key, value)
        return this
    }

    /**
     * Add a query parameter to the request.
     * @param key the param name
     * @param value the param value
     * @return HttpClient
     */
    fun addParam(key: String, value: String): HttpClient {
        this.request?.addQueryParam(key, value)
        return this
    }

    /**
     * Create an HTTP GET request to send to the server at the specified host and port.
     * @param url the relative URI
     * @return HttpRequest<Buffer>
     */
    @JvmOverloads
    fun get(url: String = ""): HttpRequest<Buffer> {
        this.request = client.get(port, host, url)
        return this.request!!
    }

    /**
     * Create an HTTP POST request to send to the server at the specified host and port.
     * @param url the relative URI
     * @return HttpRequest<Buffer>
     */
    @JvmOverloads
    fun post(url: String = ""): HttpRequest<Buffer> {
        this.request = client.post(port, host, url)
        return this.request!!
    }

    /**
     * Create an HTTP PUT request to send to the server at the specified host and port.
     * @param url the relative URI
     * @return HttpRequest<Buffer>
     */
    @JvmOverloads
    fun put(url: String = ""): HttpRequest<Buffer> {
        this.request = client.put(port, host, url)
        return this.request!!
    }

    /**
     * Create an HTTP DELETE request to send to the server at the specified host and port.
     * @param url the relative URI
     * @return HttpRequest<Buffer>
     */
    @JvmOverloads
    fun delete(url: String = ""): HttpRequest<Buffer> {
        this.request = client.delete(port, host, url)
        return this.request!!
    }

    /**
     * Create an HTTP PATCH request to send to the server at the specified host and port.
     * @param url the relative URI
     * @return HttpRequest<Buffer>
     */
    @JvmOverloads
    fun patch(url: String = ""): HttpClient {
        this.request = client.patch(port, host, url)
        return this
    }


}