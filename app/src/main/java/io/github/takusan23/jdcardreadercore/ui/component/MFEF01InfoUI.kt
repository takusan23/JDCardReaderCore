package io.github.takusan23.jdcardreadercore.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.takusan23.jdcardreadercore.data.JDCardMFEF01Data

/** 共通データ要素を表示する */
@Composable
fun MFEF01InfoUI(jdCardMFEF01Data: JDCardMFEF01Data) {
    Surface(
        color = MaterialTheme.colorScheme.primary.copy(0.1f),
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column {
            Text(
                text = "共通事項 MF/EF01",
                modifier = Modifier.padding(5.dp),
                fontSize = 20.sp,
            )

            listOf(
                "運転免許証IC仕様書バージョン" to jdCardMFEF01Data.documentVersion,
                "発行年月日" to jdCardMFEF01Data.publishDate,
                "有効期限" to jdCardMFEF01Data.endDate,
            ).forEach {
                OutlinedTextField(
                    modifier = Modifier
                        .padding(5.dp)
                        .fillMaxWidth(),
                    value = it.second,
                    onValueChange = {},
                    label = { Text(text = it.first) },
                    readOnly = true
                )
            }
        }
    }
}