package cn.v2rayj.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import java.io.Serializable

/**
 * 简易通用Compose-ListView
 * @author ciba
 * @date 2021-09-22 14:05:57
 */
object CommonList {

    interface CommonAdapter<T : Serializable> {
        fun dataInject(): List<T>
    }

    @Composable
    fun <T : Serializable> listView(
        status: MutableState<Int>,
        adapter: CommonAdapter<T>,
        item: @Composable (item: T) -> Unit
    ) {
//        val scroll = rememberScrollState(0)
        Box(Modifier.fillMaxWidth().wrapContentHeight()) {
            Column(
                Modifier.fillMaxWidth().wrapContentHeight()
            ) {
                for (x in 0 until status.value) {
                    listItem(adapter.dataInject()[x], item)
                }
            }
//            //配置滚动条
//            VerticalScrollbar(
//                rememberScrollbarAdapter(scroll),
//                Modifier.align(Alignment.CenterEnd)
//            )
        }
    }

    @Composable
    fun <T : Serializable> LazyList(
        status: LazyListState,
        adapter: CommonAdapter<T>,
        width: Dp,
        height: Dp,
        item: @Composable (item: T) -> Unit
    ) {
        LazyColumn(
            state = status,
            modifier = Modifier.height(height).width(width)
        ) {
            itemsIndexed(items = adapter.dataInject()) { _, itemContent ->
                listItem(itemContent, item)
            }
        }
    }

    @Composable
    fun <T : Serializable> listItem(
        item: T,
        content: @Composable (item: T) -> Unit
    ) {
        Card(modifier = Modifier.fillMaxWidth()) {
            content(item)
        }
    }

}