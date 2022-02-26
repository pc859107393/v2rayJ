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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Config

        if (autoStart != other.autoStart) return false
        if (isOpen != other.isOpen) return false
        if (coreFilePath != other.coreFilePath) return false

        return true
    }

    override fun hashCode(): Int {
        var result = autoStart.hashCode()
        result = 31 * result + isOpen.hashCode()
        result = 31 * result + (coreFilePath?.hashCode() ?: 0)
        return result
    }


}