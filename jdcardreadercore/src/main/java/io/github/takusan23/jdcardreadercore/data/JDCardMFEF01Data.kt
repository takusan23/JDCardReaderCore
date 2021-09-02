package io.github.takusan23.jdcardreadercore.data

/**
 * 運転免許証IC仕様書9ページ目参照
 *
 * 共通データ要素のデータです。MF/EF01です。
 *
 * @param documentVersion 仕様書バージョン。例「008」
 * @param publishDate 発行年月日。YYMMDDです。
 * @param endDate 有効期限。YYMMDDです。
 * */
data class JDCardMFEF01Data(
    val documentVersion: String,
    val publishDate: String,
    val endDate: String,
)