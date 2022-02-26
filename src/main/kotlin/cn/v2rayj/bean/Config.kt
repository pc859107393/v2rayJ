package cn.v2rayj.bean

/**
 * 基础设置，负责软件基本设置
 */
class Config : java.io.Serializable {
    var autoStart = false
    var isOpen = false
    var coreFilePath: String? = null

    override fun toString(): String {
        return "Config(autoStart=$autoStart, isOpen=$isOpen, coreFilePath=$coreFilePath)"
    }
}