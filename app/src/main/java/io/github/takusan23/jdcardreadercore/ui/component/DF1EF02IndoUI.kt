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

/** 本籍を表示する */
@Composable
fun DF1EF02IndoUI(honseki: String?) {
    Surface(
        color = MaterialTheme.colorScheme.primary.copy(0.1f),
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column {
            Text(
                text = "本籍 DF1/EF02",
                modifier = Modifier.padding(5.dp),
                fontSize = 20.sp,
            )

            OutlinedTextField(
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxWidth(),
                value = honseki ?: "暗証番号２を入力する必要があります。",
                onValueChange = {},
                label = { Text(text = "本籍") },
                readOnly = true
            )

        }
    }
}