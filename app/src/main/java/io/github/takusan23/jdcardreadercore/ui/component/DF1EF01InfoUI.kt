package io.github.takusan23.jdcardreadercore.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.takusan23.jdcardreadercore.data.JDCardDF1EF01Data

/** 記載事項 DF1/EF01 を表示する */
@Composable
fun DF1EF01InfoUI(jdCardDF1EF01Data: JDCardDF1EF01Data) {
    Surface(
        color = MaterialTheme.colors.primary.copy(0.1f),
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column {
            Text(
                text = "記載事項 DF1/EF01",
                modifier = Modifier.padding(5.dp),
                fontSize = 20.sp,
            )
            // 一個一個かくの面倒なのでらくする
            listOf(
                "JIS X 0208 制定年番号" to jdCardDF1EF01Data.jisX0208Year,
                "氏名" to jdCardDF1EF01Data.name,
                "読み" to jdCardDF1EF01Data.yomi,
                "通称名" to jdCardDF1EF01Data.tuusyoumei,
                "統一氏名" to jdCardDF1EF01Data.touitsusimei,
                "誕生日" to jdCardDF1EF01Data.birthday,
                "住所" to jdCardDF1EF01Data.address,
                "交付年月日" to jdCardDF1EF01Data.publishDate,
                "照会番号" to jdCardDF1EF01Data.syoukaiNumber,
                "運転免許証の色" to jdCardDF1EF01Data.cardColor,
                "有効期限" to jdCardDF1EF01Data.endDate,
                "免許の条件１" to jdCardDF1EF01Data.requirement1,
                "免許の条件２" to jdCardDF1EF01Data.requirement2,
                "免許の条件３" to jdCardDF1EF01Data.requirement3,
                "免許の条件４" to jdCardDF1EF01Data.requirement4,
                "公安委員会名" to jdCardDF1EF01Data.publicSafetyCommissionName,
                "運転免許証の番号" to jdCardDF1EF01Data.cardNumber,
            ).forEach {
                OutlinedTextField(
                    modifier = Modifier
                        .padding(5.dp)
                        .fillMaxWidth(),
                    value = it.second ?: "",
                    onValueChange = {},
                    label = { Text(text = it.first) },
                    readOnly = true
                )
            }
        }
    }

}