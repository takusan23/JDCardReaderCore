package io.github.takusan23.jdcardreadercore

import kotlin.RuntimeException

/** 読み取りに失敗したときに呼ぶ例外 */
class JDCardReaderException(message: String) : Exception(message)