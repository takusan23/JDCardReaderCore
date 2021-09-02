package io.github.takusan23.jdcardreadercore.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/** 読み込み画面 */
@Composable
fun LoadingScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            modifier = Modifier.padding(10.dp)
        )
        Text(
            text = """
                運転免許証を端末のNFCアンテナ部分（おサイフケータイロゴ付近）に近づけてください。
                近づけたら画面が切り替わるまで離さないでください。
                """.trimIndent(),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(10.dp)
        )
    }
}