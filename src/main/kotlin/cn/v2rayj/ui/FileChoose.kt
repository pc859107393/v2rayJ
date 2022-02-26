package cn.v2rayj.ui

import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

/**
 *  文件选择器
 *  @author ciba
 *  @date 2021-09-07
 */
class FileChoose {

    private val jFileChooser = JFileChooser()

    init {
        jFileChooser.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
        jFileChooser.fileFilter = FileNameExtensionFilter("目录", "/")
        jFileChooser.dialogTitle = "选择v2ray-core目录"
    }

    companion object {
        @JvmStatic
        val instance: FileChoose by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            FileChoose()
        }
    }

    fun show(
        onCloseRequest: (result: String?) -> Unit
    ) {
        val state: Int = jFileChooser.showOpenDialog(null)
        if (state == JFileChooser.APPROVE_OPTION) {
            onCloseRequest(String.format("%s", jFileChooser.selectedFile))
        }
    }

}