package net.cloudopt.next.web.test.controller

import net.cloudopt.next.json.Jsoner.json
import net.cloudopt.next.web.Resource
import net.cloudopt.next.web.annotation.API
import net.cloudopt.next.web.annotation.GET

@API("/render")
class RenderController:Resource() {

    @GET("/text")
    fun textRender(){
        renderText("text")
    }

    @GET("/json")
    fun jsonRender(){
        renderJson(json("result" to "json"))
    }

    @GET("/html")
    fun htmlRender(){
        renderHtml {
            template {
                name = "index"
            }
        }
    }

    @GET("/free")
    fun freemarkerRender(){
        renderFree {
            template {
                name = "index"
                parameters["name"] = "next"
            }
        }
    }

    @GET("/hbs")
    fun hbsRender(){
        renderHbs {
            template {
                name = "index"
                parameters["name"] = "next"
            }
        }
    }

}