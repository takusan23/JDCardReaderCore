package io.github.takusan23.jdcardreadercore.ui.screen

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import io.github.takusan23.jdcardreadercore.JDCardReaderCore
import io.github.takusan23.jdcardreadercore.data.JDCardData
import io.github.takusan23.jdcardreadercore.ui.component.DF1EF01InfoUI
import io.github.takusan23.jdcardreadercore.ui.component.DF1EF02IndoUI
import io.github.takusan23.jdcardreadercore.ui.component.MFEF01InfoUI
import io.github.takusan23.jdcardreadercore.ui.component.RetryCountInfoUI

/**
 * 免許の情報を取得して表示する
 *
 * @param activity Activity
 * @param pin1 暗証番号１
 * @param pin2 暗証番号２
 * */
@Composable
fun InfoScreen(activity: Activity, pin1: String, pin2: String?) {

    // 通信する
    val cardData = produceState<JDCardData?>(initialValue = null, producer = {
        value = runCatching {
            JDCardReaderCore.startGetCardData(activity, pin1, pin2)
        }.getOrElse {
            Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
            null
        }
    })

    if (cardData.value == null) {
        // つうしんちゅう
        LoadingScreen()
    } else {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {

            // 共通データ要素
            MFEF01InfoUI(jdCardMFEF01Data = cardData.value!!.jdCardMFEF01Data)
            // 試行可能回数など
            RetryCountInfoUI(retryCount = cardData.value!!.retryCount)
            // 記載事項
            DF1EF01InfoUI(jdCardDF1EF01Data = cardData.value!!.jdCardDF1EF01Data)
            // 本籍
            DF1EF02IndoUI(honseki = cardData.value!!.honseki)
        }
    }
}