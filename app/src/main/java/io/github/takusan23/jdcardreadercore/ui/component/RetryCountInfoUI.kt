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

/** 残り試行可能回数 */
@Composable
fun RetryCountInfoUI(retryCount:Int) {
    Surface(
        color = MaterialTheme.colors.primary.copy(0.1f),
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column {
            OutlinedTextField(
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxWidth(),
                value = retryCount.toString(),
                onValueChange = {},
                label = { Text(text = "残り試行可能回数") },
                readOnly = true
            )
        }
    }
}