package cn.v2rayj.component

import androidx.compose.runtime.remember
import cn.v2rayj.bean.Config
import cn.v2rayj.constant.Constants
import cn.v2rayj.util.FileUtil
import cn.v2rayj.util.JsonUtil

class AppConfig {

    var config = remember { Config() }

    init {
        try {
            config = JsonUtil.parseObject(FileUtil.file2byte(Constants.baseConfig), Config::class.java)
        } catch (e: Exception) {

        }
    }

}