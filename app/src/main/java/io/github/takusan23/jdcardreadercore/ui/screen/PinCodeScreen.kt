package io.github.takusan23.jdcardreadercore.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 暗証番号入力画面
 *
 * @param onEnter 暗証番号を確定した場合に呼ばれる。１個目が暗証番号１、二個目が暗証番号２
 * */
@Composable
fun PinCodeScreen(onEnter: (String, String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        val pin1 = remember { mutableStateOf("") }
        val pin2 = remember { mutableStateOf("") }

        Text(
            modifier = Modifier.padding(10.dp),
            text = "暗証番号の入力",
            fontSize = 30.sp
        )

        Text(
            modifier = Modifier.padding(10.dp),
            text = "本籍を表示させたい場合は暗証番号２を入力する必要があります。",
        )

        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            singleLine = true,
            value = pin1.value,
            isError = pin1.value.length > 4,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            label = { Text(text = "暗証番号１") },
            onValueChange = { pin1.value = it }
        )
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            singleLine = true,
            value = pin2.value,
            isError = pin2.value.length > 4,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            label = { Text(text = "暗証番号２（本籍を表示しない場合は不要）") },
            onValueChange = { pin2.value = it }
        )

        Button(
            modifier = Modifier
                .padding(10.dp),
            enabled = pin1.value.length == 4,
            onClick = {
                onEnter(pin1.value, pin2.value)
            },
            content = {
                Text(
                    text = """
                        暗証番号を確定
                        （カードを読み取る準備へ進む）
                        """.trimIndent(),
                    textAlign = TextAlign.Center
                )
            }
        )

    }
}