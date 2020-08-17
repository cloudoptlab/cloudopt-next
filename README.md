<img src="https://www.cloudopt.net/static/images/logo.svg" alt="Cloudopt Next" style="width:250px;font-family:'Times New Roman'" />

![image.png](https://cdn.nlark.com/yuque/0/2020/png/85774/1596081097483-df48c14a-e8c6-4e49-98b7-88ec5334308c.png#align=left&display=inline&height=20&margin=%5Bobject%20Object%5D&name=image.png&originHeight=20&originWidth=98&size=2011&status=done&style=none&width=98) [![image.png](https://cdn.nlark.com/yuque/0/2020/png/85774/1596081503933-8696c94f-517e-4913-88f9-94cfb199f2f8.png#align=left&display=inline&height=20&margin=%5Bobject%20Object%5D&name=image.png&originHeight=20&originWidth=110&size=1557&status=done&style=none&width=110)](https://github.com/vert-x3/vertx-awesome) [![](https://cdn.nlark.com/yuque/0/2020/svg/85774/1596081628915-65fb4c2a-8aa6-432b-b13a-cf0f0193333e.svg#align=left&display=inline&height=20&margin=%5Bobject%20Object%5D&originHeight=20&originWidth=102&size=0&status=done&style=none&width=102)](https://github.com/KotlinBy/awesome-kotlin)

[Cloudopt Next](https://next.cloudopt.net/) is a very lightweight and modern, JVM-based, full stack kotlin framework designed for building modular, easily testable JVM applications with support for Java, Kotlin language, crafted from the best of breed Java libraries and standards.

**Cloudopt Next has the following features:**

>**Simple** Minimalist design, almost no configuration, no dependence on tomcat, jetty and other web containers.

>**Asyn** Based on vertx, it is easy to achieve high-performance asynchronous services.

>**Plugin** Supports various components of the vertx system, and also supports the extension of functions through plug-ins. The official also provides a large number of useful plug-ins.

>**Chinese** All Chinese documents, Chinese community to help Chinese developers get started quickly



## Started

You can view the [document](https://next.cloudopt.net) by visiting the official website of Cloudopt Next, or you can go to [example](https://github.com/cloudoptlab/cloudopt-next-example) to see a simple example.

### Route

Let's take a look at a simple route based on Cloudopt Next:

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
        renderHtml(view);
    }
}
````

### Run

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

### Plugin
````kotlin
fun main(args: Array<String>) {
    CloudoptServer.addPlugin(TestPlugin())
    CloudoptServer.addPlugin(EventPlugin())
    CloudoptServer.run()
}

````


## Getting help

Having trouble with Cloudopt Next? We’d like to help!

- Follow our [twitter](https://twitter.com/) to get the latest information.
- Check the reference [documentation](https://next.cloudopt.net) carefully for specific code cases or common problems.
- If you have a problem with the upgrade, check the upgrade instructions in the [Wiki](https://github.com/cloudoptlab/cloudopt-next/wiki).
- Send mail to support@cloudopt.net.
- Send [issue](https://github.com/cloudoptlab/cloudopt-next/issues) to your question on GitHub, we will answer it as soon as possible.
- If you are in China, you can also join the QQ group: 557692142.

## Reporting Issues

Cloudopt Next uses GitHub’s integrated issue tracking system to record bugs and feature requests. If you want to raise an issue, please follow the recommendations below:

- Before you log a bug, please search the issue tracker to see if someone has already reported the problem.
- Please provide as much information as possible with the issue report, we like to know the version of Cloudopt Next that you are using, as well as your Operating System and JVM version.

## License

Cloudopt Next is Open Source software released under the [Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0.html).
