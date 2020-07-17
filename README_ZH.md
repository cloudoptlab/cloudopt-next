<img src="https://www.cloudopt.net/static/images/logo.svg" alt="Cloudopt Next" style="width:250px;font-family:'Times New Roman'" />



[Cloudopt Next](https://next.cloudopt.net/) 是一个非常轻量级且现代的、基于 Kotlin 编写的全栈开发框架，同时支持 Java 和 Kotlin，您可以处理Url的解析，数据的封装,Json的输出等等，从根本上减少开发时间、提升开发体验。

**Cloudopt Next主要拥有以下特点：**

> **简单** 极简设计，几乎不要任何配置，不依赖 Tomcat、Jetty 等 Web 容器。

> **异步** 基于 vertx，，轻松实现高性能的异步服务。

> **扩展** 支持 vertx 体系的各种组件，同时支持通过插件扩展功能，官方也提供了大量好用的插件。

> **中文** 全中文文档、中文社区，帮助中文开发者快速上手

## 示例

您可以通过访问 [Cloudopt Next](https://next.cloudopt.net) 的官网来查看文档，也可以前往 [Example ](https://github.com/cloudoptlab/cloudopt-next-example)查看简单的示例。

### 路由

让我们来看看一个简单的基于Cloudopt Next的路由：

````kotlin
@API("/")
class IndexController : Resource() {
    @GET
    fun get(){
        renderHtml(view = "index")
    }
}
````

````java
@API(value = "/")
public class IndexController extends Resource {

    @GET
    public void get(){
        View v = new View();
        v.setView("index");
        renderHtml(v);
    }
}
````

### 启动
````kotlin
fun main(args: Array<String>) {
    CloudoptServer.run()
}
````

````java
public static void main(String args[]) {
    CloudoptServer.run();
}
````

### SockJS
````kotlin
@SocketJS("/socket/api/*")
class SocketController : SocketJSResource {
    override fun handler(socket: SockJSSocket) {
        println(socket)
        socket.handler {message->
            println(message)
            socket.write("Hello world!")
        }
    }
}
````

### 插件
````kotlin
fun main(args: Array<String>) {
    CloudoptServer.addPlugin(TestPlugin())
    CloudoptServer.addPlugin(EventPlugin())
    CloudoptServer.run()
}

````

## 寻求帮助

在使用Cloudopt Next的过程中遇到了问题？您可以通过下面途径寻求帮助：

- 请关注我们的 [推特](https://twitter.com/)，以便获得最新的信息。
- 请仔细检查 [参考文档](https://next.cloudopt.net)，查看具体的代码案例或者是常见问题。
- 如果您在升级版本以后遇到问题，可以查看 [Wiki](https://github.com/cloudoptlab/cloudopt-next/wiki)中的升级说明。
- 请发送邮件到 support@cloudopt.net
- 请在GitHub发送 [Issue](https://github.com/cloudoptlab/cloudopt-next/issues) 提交您的问题，我们将尽快为您解答。
- 如果您在中国，还可以加入交流QQ群：557692142。

## 报告问题
Cloudopt Next使用GitHub的问题跟踪系统，以记录bug和特性请求。如果您想提出一个问题，可以参考下面的建议：

- 请您先尝试搜索一下是否有相关的问题。
- 请尽可能的提供详细的错误信息或者报告，包括正在使用的Cloudopt Next的版本、Java版本或者Kotlin版本等等。

## 许可协议
Cloudopt Next是一个开源项目，遵循 [Apache 2.0许可协议](http://www.apache.org/licenses/LICENSE-2.0.html)。

## 寻找赞助商
如果您或者您所在的公司希望赞助Cloudopt Next的开发，可以发送邮件到 support@cloudopt.net。