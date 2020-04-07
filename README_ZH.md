<img src="https://www.cloudopt.net/static/images/logo.svg" width = "150"/>

<br />

[![Awesome Kotlin Badge](https://kotlin.link/awesome-kotlin.svg)](https://github.com/KotlinBy/awesome-kotlin) [![Apache Licenses Badge](https://img.shields.io/hexpm/l/plug.svg)](http://www.apache.org/licenses/LICENSE-2.0.html) [![Twitter Badge](https://img.shields.io/twitter/url/http/shields.io.svg?style=social&logo=twitter)](https://twitter.com/CloudoptLab)

Cloudopt Next是基于Kotlin、Vertx的一个面向下一代的极其轻量级的微服务框架，您可以处理Url的解析，数据的封装,Json的输出等等，从根本上减少开发时间、提升开发体验。Cloudopt Next吸收了[Spring Boot](https://github.com/spring-projects/spring-boot)、[JFinal](https://github.com/jfinal/jfinal)、[Resty](https://github.com/Dreampie/Resty)、[Vertx](https://github.com/vert-x3/vertx-web)等优秀项目的思想,不仅拥有非常好的开发体验还拥有着极低的学习曲线。

**Cloudopt Next主要拥有以下特点：**

- 极简设计，几乎零配置，与Spring Boot一样使用Yaml。
- 脱离传统MVC，专业的事由专业的做。
- 支持Plugin体系，扩展性强。
- 不依赖Tomcat、Jetty。
- 多视图支持，支持FreeMarker、Beetl等。
- 同时支持Kotlin和Java开发。
- 支持[Vertx](http://vertx.io/)体系
- 提供了一系列好用的工具集，如cloudopt-next-logging、cloudopt-next-kafka、cloudopt-next-encrypt、cloudopt-next-waf等。

## 安装和开始

您可以通过访问[Cloudopt Next的官网](https://next.cloudopt.net)来查看文档，也可以前往[Example](https://github.com/cloudoptlab/cloudopt-next-example)查看简单的示例。

让我们来看看一个简单的基于Cloudopt Next的路由：

[Kotlin]

````Kotlin
@API("/")
class IndexController : Resource() {

    @GET
    fun get(){
        var view = View()
        view.view = "index"
        renderHtml(view)
    }

}
````

[Java]
````Java
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

## 寻求帮助

在使用Cloudopt Next的过程中遇到了问题？您可以通过下面途径寻求帮助：

- 请关注我们的[推特](https://twitter.com/CloudoptLab)，以便获得最新的信息。
- 请仔细检查[参考文档](https://next.cloudopt.net)，查看具体的代码案例或者是常见问题。
- 如果您在升级版本以后遇到问题，可以查看[Wiki](https://github.com/cloudoptlab/cloudopt-next/wiki)中的升级说明。
- 请发送邮件到support@cloudopt.net
- 请在GitHub发送[Issue](https://github.com/cloudoptlab/cloudopt-next/issues)提交您的问题，我们将尽快为您解答。
- 如果您在中国，还可以加入交流QQ群：557692142。

## 报告问题
Cloudopt Next使用GitHub的问题跟踪系统，以记录bug和特性请求。如果您想提出一个问题，可以参考下面的建议：

- 请您先尝试搜索一下是否有相关的问题。
- 请尽可能的提供详细的错误信息或者报告，包括正在使用的Cloudopt Next的版本、Java版本或者Kotlin版本等等。

## 许可协议
Cloudopt Next是一个开源项目，遵循[Apache 2.0许可协议](http://www.apache.org/licenses/LICENSE-2.0.html)。