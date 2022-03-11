package cn.v2rayj.proxy.plugins

import cn.v2rayj.constant.Constants
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.locations.*
import io.ktor.server.plugins.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.webjars.*
import java.io.File

fun Application.configureRouting() {

    install(Webjars) {
        path = "/webjars" //defaults to /webjars
    }

    routing {
        get("/proxy.pac") {
            //本地文件暂时不考虑加入缓存
            call.respondFile(File(Constants.pacPath), "proxy.pac")
        }
        // Static plugin. Try to access `/static/index.html`
        static("/static") {
            resources("static")
        }
        install(StatusPages) {
            exception<AuthenticationException> { call, cause ->
                call.respond(HttpStatusCode.Unauthorized)
            }
            exception<AuthorizationException> { call, cause ->
                call.respond(HttpStatusCode.Forbidden)
            }

        }
        get("/webjars") {
            call.respondText("<script src='/webjars/jquery/jquery.js'></script>", ContentType.Text.Html)
        }
    }
}

class AuthenticationException : RuntimeException()
class AuthorizationException : RuntimeException()
