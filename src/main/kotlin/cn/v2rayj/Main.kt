// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package cn.v2rayj

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Tray
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import cn.v2rayj.proxy.ProxyServer
import cn.v2rayj.theme.AppTheme.lightThemeColors
import cn.v2rayj.ui.FileChoose

@Composable
@Preview
fun App() {
    var text by remember { mutableStateOf("Hello, World!") }

    MaterialTheme(
        colors = lightThemeColors
    ) {
        Button(onClick = {
            FileChoose.instance.show {
                text = it!!
            }
        }) {
            Text(text)
        }
        Divider(modifier = Modifier.height(1.dp).fillMaxWidth(), color = Color(0xFF88919d))

    }
}

fun main() = application {

    var isVisible by remember { mutableStateOf(true) }
    var runStatus by remember { mutableStateOf(false) }

    Window(
        onCloseRequest = { isVisible = false },
        visible = isVisible,
        title = "config"
    ) {
        this.window.setSize(1060, 800)
        this.window.setLocationRelativeTo(null)

        Tray(
            object : Painter() {
                override val intrinsicSize = Size(180f, 180f)

                override fun DrawScope.onDraw() {
                    drawOval(Color(0xFFFFA500))
                }
            },
            tooltip = "v2rayJ",
            onAction = { isVisible = true },
            menu = {
                Item("主界面", onClick = {
                    isVisible = true
                })
                Separator()
                Item("运行状态：${runStatus}", onClick = {
                    runStatus = runStatus.not()
                    if (runStatus) {
                        ProxyServer.instance.start(8999)
                    } else {
                        ProxyServer.instance.stop()
                    }
                })
                Separator()
                Item("Pac自动模式", onClick = {
                    //启用pac自动控制
                })
                Item("全局模式", onClick = {
                    //启用全局模式
                })
                Item("手动模式", onClick = {
                    //启用全局模式
                })
                Item("外部PAC模式", onClick = {
                    //启用全局模式
                })
                Separator()
                Menu("服务器列表", content = {

                })
                Item("服务器设置", onClick = {
                })
                Item("Pac设置", onClick = {
                })
                Separator()
                Item("从剪贴板导入", onClick = {
                })
                Item("扫描屏幕二维码", onClick = {
                })
                Item("分享二维码和链接", onClick = {
                })
                Separator()
                Item("复制终端命令", onClick = {
                })
                Separator()
                Item("高级设置", onClick = {
                })
                Item("检查更新", onClick = {
                })
                Item("帮助", onClick = {
                })
                Separator()
                Item("退出", onClick = ::exitApplication)
            },
        )

        App()
    }
}
