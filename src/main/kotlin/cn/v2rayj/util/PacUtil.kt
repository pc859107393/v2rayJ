package cn.v2rayj.util

import cn.v2rayj.constant.Constants
import cn.v2rayj.exceptions.ParamErrorException
import java.io.ByteArrayOutputStream

object PacUtil {

    private val fileName = "abp.js"

    private var proxy =
        "var proxy = \"SOCKS5 __SOCKS5ADDR__:__SOCKS5PORT__; SOCKS __SOCKS5ADDR__:__SOCKS5PORT__; DIRECT;\"\n"

    private var rules = "var rules = __rules__;\n"

    class Builder {
        private var address5: String = "127.0.0.1"
        private var port5: Int = 1086

        private val hosts = ArrayList<String>()

        @JvmOverloads
        fun socket5(address: String = "127.0.0.1", port: Int = 1086): Builder {
            address5 = address
            port5 = port
            return this
        }

        @Throws(exceptionClasses = [ParamErrorException::class])
        fun appendHost(host: String): Builder {
            hosts.add(host)
            return this
        }

        @Throws(exceptionClasses = [ParamErrorException::class])
        fun appendHost(hosts: List<String>): Builder {
            this.hosts.addAll(hosts)
            return this
        }

        @Throws(exceptionClasses = [ParamErrorException::class])
        fun appendHost(hosts: ByteArray): Builder {
            val hostsStr = String(hosts)
            if (hostsStr.contains("\r\n")) {
                hostsStr.split("\r\n").forEach {
                    var tmp = it.replace("\"", "")
                    tmp = tmp.replace(",", "")
                    this.hosts.add(tmp.trim())
                }
            } else {
                hostsStr.split("\n").forEach {
                    var tmp = it.replace("\"", "")
                    tmp = tmp.replace(",", "")
                    this.hosts.add(tmp.trim())
                }
            }
            return this
        }

        fun build(path: String) {
            var proxyTmp = proxy.replace("__SOCKS5ADDR__", address5)
            proxyTmp = proxyTmp.replace("__SOCKS5PORT__", "$port5")
            val rulesTmp = rules.replace("__rules__", JsonUtil.toJsonString(hosts))

            "$proxyTmp$rulesTmp".toByteArray(Charsets.UTF_8).run {
                val outputStream = ByteArrayOutputStream()
                outputStream.write(this)
                PacUtil::class.java.classLoader.getResourceAsStream("proxy/$fileName")?.readBytes()?.let {
                    outputStream.write(it)
                    FileUtil.bytes2File(outputStream.toByteArray(), "$path/proxy.pac")
                    outputStream.close()
                }
            }


        }
    }

}

fun main(args: Array<String>) {
    PacUtil.Builder()
        .socket5()
        .appendHost("baidu.com")
        .appendHost(
            "  \"@@||youdao.com\",\n  \"@@||zhongsou.com\",\n  \"@@|http:\\/\\/ime.baidu.jp\",\n  \"@@|http:\\/\\/ershoudong.com\"".toByteArray(Charsets.UTF_8)
        ).build(Constants.baseDir)
}