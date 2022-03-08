import cn.v2rayj.constant.Constants
import cn.v2rayj.util.FileUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.*
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.stream.Collectors
import kotlin.test.Test


val path: String = "proxy/bin/macos/V2rayUTool"

val log: Logger = LoggerFactory.getLogger(FileUtil::class.java)

class UtilTest {

    @Test
    fun testV2rayUTool() {
        //将V2rayUTool释放到指定目录
        Thread.currentThread().contextClassLoader.getResourceAsStream(path)?.readBytes()?.let {
            FileUtil.bytes2File(it, "${Constants.binPath}/V2rayUTool")
        }

//        val exec = Runtime.getRuntime()
//            .exec("/configProxy.sh 172839 ''")
//        val reader = BufferedReader(InputStreamReader(exec.inputStream))
//        exec.waitFor()
//        var s: String?
//        while (reader.readLine().also { s = it } != null) {
//            log.info("打印结果：$s")
//        }

        //log.info("网优异步处理文件 -> 下载文件{}完成", sourceFileName);
        val processBuilder = ProcessBuilder()
        processBuilder.command(
            "sh",
            "/Users/cheng/IdeaProjects/v2rayJ/src/test/resources/configProxy.sh",
            "http://localhost:1089/proxy.pac",
            "1087",
            "1086"
        )

        val startTime = System.currentTimeMillis()

        val process = processBuilder.start()
        val `in`: InputStream = process.inputStream
        var collect =
            BufferedReader(InputStreamReader(`in`, StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"))
        collect = collect.replace("[\\x00-\\x08\\x0b-\\x0c\\x0e-\\x1f]".toRegex(), "")
        log.info(
            "\n----------------执行时间：{}, 执行脚本返回结果----------------\n{}",
            System.currentTimeMillis() - startTime,
            collect
        )
        `in`.close()
        val input: InputStream = process.errorStream
        val collect1 = BufferedReader(InputStreamReader(input)).lines().collect(Collectors.joining("\n"))
        log.info("\n----------------完整执行结果----------------\n{}", collect1)
        input.close()
    }

    @Test
    fun runWithPrivileges() {
        val input: InputStreamReader
        val output: OutputStreamWriter
        try {
            //Create the process and start it.
            val pb =
                ProcessBuilder("/bin/bash", "-c", "echo 172839 | /usr/bin/sudo -S /bin/cat /etc/sudoers 2>&1").start()
            output = OutputStreamWriter(pb.outputStream)
            input = InputStreamReader(pb.inputStream)
            var bytes: Int
            var tryies = 0
            val buffer = CharArray(1024)
            while (input.read(buffer, 0, 1024).also { bytes = it } != -1) {
                if (bytes == 0) continue
                //Output the data to console, for debug purposes
                val data = String(buffer, 0, bytes)
                println(data)
                // Check for password request
                if (data.contains("[sudo] password")) {
                    // Here you can request the password to user using JOPtionPane or System.console().readPassword();
                    // I'm just hard coding the password, but in real it's not good.
                    val password = charArrayOf('t', 'e', 's', 't')
                    output.write(password)
                    output.write('\n'.code)
                    output.flush()
                    // erase password data, to avoid security issues.
                    Arrays.fill(password, '\u0000')
                    tryies++
                }
            }

        } catch (ex: IOException) {
            log.error(ex.message)
        }

    }

}