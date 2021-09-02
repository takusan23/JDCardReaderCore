package io.github.takusan23.jdcardreadercore.data

/**
 * 読み取った値を収納するデータクラス
 *
 * @param jdCardMFEF01Data 共通事項を入れたデータクラス。MF/EF01
 * @param retryCount 残り試行可能回数
 * @param jdCardDF1EF01Data 運転免許証の記載事項を入れたデータクラス。DF1/EF01
 * @param honseki [io.github.takusan23.jdcardreadercore.JDCardReaderCore.startReader]で暗証番号２を入力した場合のみ、本籍が入ります。
 * */
data class JDCardData(
    val jdCardMFEF01Data: JDCardMFEF01Data,
    val retryCount: Int,
    val jdCardDF1EF01Data: JDCardDF1EF01Data,
    val honseki: String? = null
)