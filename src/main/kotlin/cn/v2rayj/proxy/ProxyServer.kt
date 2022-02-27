package cn.v2rayj.proxy

import cn.v2rayj.proxy.plugins.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        configureRouting()
        configureHTTP()
        configureMonitoring()
        configureTemplating()
        configureSerialization()
        configureAdministration()
    }.start(wait = true)
}

/**
 * 代理服务器的相关控制单例
 *
 * @author ciba
 * @date 2022年02月27日14:06:31
 */
class ProxyServer {

    private var realServer: ApplicationEngine? = null

    private var isStart = false

    private val coreThread = Executors.newSingleThreadExecutor()

    companion object {
        @JvmStatic
        val instance: ProxyServer by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            ProxyServer()
        }
    }

    fun start(port: Int) {
        if (isStart) return
        synchronized(this) {
            coreThread.execute {
                realServer = embeddedServer(Netty, port = port, host = "0.0.0.0") {
                    configureRouting()
                    configureHTTP()
                    configureMonitoring()
                    configureTemplating()
                    configureSerialization()
                    configureAdministration()
                }
                isStart = true
                realServer?.start(wait = true)
            }
        }
    }

    fun stop() {
        if (null != realServer && isStart) {
            realServer?.run {
                val application = this.application
                val environment = application.environment

                val latch = CompletableDeferred<Nothing>()
                application.launch {
                    latch.join()

                    environment.monitor.raise(ApplicationStopPreparing, environment)
                    if (environment is ApplicationEngineEnvironment) {
                        environment.stop()
                    } else {
                        application.dispose()
                    }
                }
            }

            realServer = null
            isStart = false

            synchronized(this) {
                coreThread.execute {

                }
            }

        }
    }
}