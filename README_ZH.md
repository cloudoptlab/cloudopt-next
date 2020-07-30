![](https://cdn.nlark.com/yuque/0/2020/svg/85774/1595148637950-b36f1541-1dd2-4732-bf5b-55a051782f21.svg#align=left&display=inline&height=50&margin=%5Bobject%20Object%5D&originHeight=132&originWidth=469&size=0&status=done&style=none&width=176)


![image.png](https://cdn.nlark.com/yuque/0/2020/png/85774/1596081097483-df48c14a-e8c6-4e49-98b7-88ec5334308c.png#align=left&display=inline&height=20&margin=%5Bobject%20Object%5D&name=image.png&originHeight=20&originWidth=98&size=2011&status=done&style=none&width=98) [![image.png](https://cdn.nlark.com/yuque/0/2020/png/85774/1596081503933-8696c94f-517e-4913-88f9-94cfb199f2f8.png#align=left&display=inline&height=20&margin=%5Bobject%20Object%5D&name=image.png&originHeight=20&originWidth=110&size=1557&status=done&style=none&width=110)](https://github.com/vert-x3/vertx-awesome) [![](https://cdn.nlark.com/yuque/0/2020/svg/85774/1596081628915-65fb4c2a-8aa6-432b-b13a-cf0f0193333e.svg#align=left&display=inline&height=20&margin=%5Bobject%20Object%5D&originHeight=20&originWidth=102&size=0&status=done&style=none&width=102)](https://github.com/KotlinBy/awesome-kotlin)


[Cloudopt Next](https://next.cloudopt.net/) 是一个非常轻量级且现代的、基于 Kotlin 编写的全栈开发框架，同时支持 Java 和 Kotlin，您可以处理Url的解析，数据的封装,Json的输出等等，从根本上减少开发时间、提升开发体验。

**Cloudopt Next 主要拥有以下特点：**


> **简单** 极简设计，几乎不要任何配置，不依赖 Tomcat、Jetty 等 Web 容器。



> **异步** 基于 vertx 轻松实现高性能的异步服务。



> **扩展** 支持 vertx 体系的各种组件，同时支持通过插件扩展功能，官方也提供了大量好用的插件。



> **中文** 全中文文档、中文社区，帮助中文开发者快速上手。



**GitHub:**


[![](https://cdn.nlark.com/yuque/0/2020/svg/85774/1596080492080-3dd8a1af-e65f-400b-a85a-23b70f5009a9.svg#align=left&display=inline&height=193&margin=%5Bobject%20Object%5D&originHeight=193&originWidth=442&size=0&status=done&style=none&width=442)](https://github.com/cloudoptlab/cloudopt-next)


**开源中国：**


![](https://cdn.nlark.com/yuque/0/2020/svg/85774/1596079972214-d0c5ce4d-3031-4b8f-9b96-0083f085e73e.svg#align=left&display=inline&height=257&margin=%5Bobject%20Object%5D&originHeight=324&originWidth=600&size=0&status=done&style=none&width=475)
[
](https://gitee.com/cloudopt/cloudopt-next)
## 性能


测试电脑的 CPU 是 2.2 GHz 六核 Intel Core i7，内存为 16 GB 2400 MHz DDR4。


吞吐量参数为用户数 10，循环 10000 次。

| 框架名称 | 吞吐量 |
| --- | :---: |
| Cloudopt Next | 20267.5/sec |
| Spring Boot | 10698.2/sec |
| Flask | 440.8/sec |



根据性能测试结果：Cloudopt Next 的性能是 Flask 的 50 倍，是 Spring Boot 的两倍。


## 示例


您可以通过访问[Cloudopt Next的官网](https://next.cloudopt.net)来查看文档，也可以前往[Example](https://github.com/cloudoptlab/cloudopt-next-example)查看简单的示例。


### 路由


让我们来看看一个简单的基于Cloudopt Next的路由：


```kotlin
@API("/")
class IndexController : Resource() {
    @GET
    fun get(){
        renderHtml(view = "index")
    }
}
```


```java
@API(value = "/")
public class IndexController extends Resource {

    @GET
    public void get(){
        View v = new View();
        v.setView("index");
        renderHtml(v);
    }
}
```


### 启动


```kotlin
fun main(args: Array<String>) {
    NextServer.run()
}
```


```java
public static void main(String args[]) { 
    NextServer.run();
}
```


### WebSocket
```kotlin
@WebSocket("/websocket")
class WebSocketController : WebSocketResource {
    override fun handler(userWebSocketConnection: ServerWebSocket) {
        println("Connected!")
        userWebSocketConnection.writeTextMessage("Hello World")
        userWebSocketConnection.frameHandler { frame ->
            println(frame.textData())
        }
    }
}
```
### SockJS


```kotlin
@SocketJS("/socket/api/*")
class SocketController : SocketJSResource {
    override fun handler(userSocketConnection: SockJSSocket) {
        println(userSocketConnection)
        userSocketConnection.handler {message->
            println(message)
            userSocketConnection.write("Hello world!")
        }
    }
}
```


### 插件


```kotlin
fun main(args: Array<String>) {
    NextServer.addPlugin(TestPlugin())
    NextServer.addPlugin(EventPlugin())
    NextServer.run()
}
```


## 寻求帮助


在使用Cloudopt Next的过程中遇到了问题？您可以通过下面途径寻求帮助：


- 请关注我们的[推特](https://twitter.com/)，以便获得最新的信息。
- 请仔细检查[参考文档](https://next.cloudopt.net)，查看具体的代码案例或者是常见问题。
- 如果您在升级版本以后遇到问题，可以查看 [Wiki](https://github.com/cloudoptlab/cloudopt-next/wiki) 中的升级说明。
- 请发送邮件到 support[@cloudopt.net ](/cloudopt.net )
- 请在 GitHub 发送 [Issue](https://github.com/cloudoptlab/cloudopt-next/issues) 提交您的问题，我们将尽快为您解答。
- 如果您在中国，还可以加入交流QQ群：557692142。



## 报告问题


Cloudopt Next使用GitHub的问题跟踪系统，以记录bug和特性请求。如果您想提出一个问题，可以参考下面的建议：


- 请您先尝试搜索一下是否有相关的问题。
- 请尽可能的提供详细的错误信息或者报告，包括正在使用的Cloudopt Next的版本、Java版本或者Kotlin版本等等。



## 许可协议


Cloudopt Next 是一个开源项目，遵循 [Apache 2.0许可协议](http://www.apache.org/licenses/LICENSE-2.0.html)。


## 寻找赞助商


如果您或者您所在的公司希望赞助 Cloudopt Next 的开发，可以发送邮件到 support@cloudopt.net。
