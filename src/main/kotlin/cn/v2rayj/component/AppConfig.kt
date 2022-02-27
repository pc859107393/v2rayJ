package cn.v2rayj.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import cn.v2rayj.bean.Config
import cn.v2rayj.constant.Constants
import cn.v2rayj.util.FileUtil
import cn.v2rayj.util.JsonUtil

class AppConfig {

}

@Composable
fun getConfig(): MutableState<Config> {
    val config = remember { mutableStateOf(Config()) }
    FileUtil.file2byte(Constants.baseConfig)?.run {
        config.value = JsonUtil.parseObject(this, Config::class.java)
    }
    return config
}