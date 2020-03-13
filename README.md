![Cloudopt Next](https://github.com/cloudoptlab/cloudopt-next/raw/master/logo.svg)

[![Awesome Kotlin Badge](https://kotlin.link/awesome-kotlin.svg)](https://github.com/KotlinBy/awesome-kotlin) [![Apache Licenses Badge](https://img.shields.io/hexpm/l/plug.svg)](http://www.apache.org/licenses/LICENSE-2.0.html) [![Twitter Badge](https://img.shields.io/twitter/url/http/shields.io.svg?style=social&logo=twitter)](https://twitter.com/CloudoptLab)

Cloudopt Next is a very lightweight micro-service framework based on kotlin and vertx for the Next generation. You can handle url parsing, data encapsulation, and Json output. Radically reducing development time and improving development experience. Cloudopt Next absorbed the ideas of [Spring Boot](https://github.com/spring-projects/spring-boot)，[JFinal](https://github.com/jfinal/jfinal)，[Resty](https://github.com/Dreampie/Resty)，[Vertx](https://github.com/vert-x3/vertx-web) and other excellent projects, and not only had a very good development experience, but also had a very low learning curve.


**Cloudopt Next has the following features:**

- minimalist design, almost 0 configuration, uses yml like Spring Boot.
- get out of the traditional MVC and do professional things professionally.
- support plugin system with strong expansibility.
- independent of Tomcat and Jetty.
- support FreeMarker, Beetl...
- support both Kotlin and Java development.
- support [Vertx](http://vertx.io/).
- a series of easy-to-use toolsets, such as cloudopt-next-logging, cloudopt-next-kafka, cloudopt-next-encrypt, cloudopt-next-waf, etc.



## Installation and Getting Started

You can view the [document](https://next.cloudopt.net) by visiting the official website of Cloudopt Next, or you can go to [example](https://github.com/cloudoptlab/cloudopt-next-example) to see a simple example.

Let's take a look at a simple route based on Cloudopt Next:

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

## Getting help

Having trouble with Cloudopt Next? We’d like to help!

- Follow our [twitter](https://twitter.com/) to get the latest information.
- Check the reference [documentation](https://next.cloudopt.net) carefully for specific code cases or common problems.
- If you have a problem after the upgrade, check the upgrade instructions in the [Wiki](https://github.com/cloudoptlab/cloudopt-next/wiki).
- Send mail to support@cloudopt.net.
- Send [issue](https://github.com/cloudoptlab/cloudopt-next/issues) to your question on GitHub, we will answer it as soon as possible.
- If you are in China, you can also join the QQ group: 557692142.

## Reporting Issues

Cloudopt Next uses GitHub’s integrated issue tracking system to record bugs and feature requests. If you want to raise an issue, please follow the recommendations below:

- Before you log a bug, please search the issue tracker to see if someone has already reported the problem.
- Please provide as much information as possible with the issue report, we like to know the version of Cloudopt Next that you are using, as well as your Operating System and JVM version.

## License

Cloudopt Next is Open Source software released under the [Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0.html).

## Documents

[中文版介绍](https://github.com/cloudoptlab/cloudopt-next/wiki/Chinese-Document)