package io.github.takusan23.jdcardreadercore

import android.app.Activity
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import io.github.takusan23.jdcardreadercore.data.JDCardDF1EF01Data
import io.github.takusan23.jdcardreadercore.data.JDCardData
import io.github.takusan23.jdcardreadercore.data.JDCardMFEF01Data
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * 運転免許証を読み取るライブラリ。読み取る関数はここにあります。
 *
 * 運転免許証IC仕様書のURLはここです。https://www.npa.go.jp/laws/notification/koutuu/menkyo/menkyo20210630_150.pdf
 *
 * コルーチンです。
 * */
object JDCardReaderCore {

    /**
     * カードの読み取りを開始する
     * @param activity Activity
     * @param callBack コールバック
     * */
    private fun startReader(activity: Activity, callBack: (Tag, NfcAdapter) -> Unit) {
        val nfcAdapter = NfcAdapter.getDefaultAdapter(activity)
        // 読み取りを開始する
        nfcAdapter.enableReaderMode(
            activity,
            { tag -> callBack(tag, nfcAdapter) },
            NfcAdapter.FLAG_READER_NFC_B,
            null
        )
    }

    /**
     * 読み取りを開始して、値を返す。値を返したら内部のコールバック関数を解除する
     *
     * コルーチンを利用しています。失敗した場合は[JDCardReaderException]を吐きます。
     *
     * @param activity Activity。NFC読み取りに何故か必要
     * @param pinCode1 暗証番号１
     * @param pinCode2 暗証番号２。本籍を読み出す場合のみ必要
     * @return ICカードに入ってるデータ。本籍は[pinCode2]が必要です。
     * */
    suspend fun startGetCardData(activity: Activity, pinCode1: String, pinCode2: String? = null): JDCardData {
        // 読み取り終わるまでコルーチン一時停止
        return suspendCoroutine { callbackCoroutine ->
            startReader(activity) { tag, nfcAdapter ->

                val isoDep = IsoDep.get(tag)
                isoDep.connect()

                /** 共通データ要素 MF/EF01 */
                var mfeF01Data: JDCardMFEF01Data? = null

                /** 残り試行可能回数 */
                var retryCount = 3

                /** 記載事項 DF1/EF01 */
                var dF1EF01Data: JDCardDF1EF01Data? = null

                /** 本籍。暗証番号２がない場合はnull */
                var honseki: String? = null

                /**
                 * 例外を吐いて後始末をする
                 * @param isoDep IsoDep
                 * */
                fun throwExceptionAndClose(errorMessage: String) {
                    isoDep.close()
                    nfcAdapter.disableReaderMode(activity)
                    callbackCoroutine.resumeWithException(JDCardReaderException(errorMessage))
                }

                // MFを選択する
                val mfSelectCommand = byteArrayOf(
                    0x00.toByte(), // CLA
                    0xA4.toByte(), // INS (SELECT FILE)
                    0x00.toByte(), // P1
                    0x00.toByte(), // P2
                )
                val mfSelectCommandResult = isoDep.transceive(mfSelectCommand)
                if (mfSelectCommandResult[0] != 0x90.toByte()) {
                    throwExceptionAndClose("MF選択に失敗しました。${mfSelectCommandResult.toHexString()}")
                }

                // カレントディレクトリを共通データ要素に設定する
                val mfEf01SelectCommand = byteArrayOf(
                    0x00.toByte(),
                    0xA4.toByte(),
                    0x02.toByte(),
                    0x0C.toByte(),
                    0x02.toByte(),
                    0x2F.toByte(),
                    0x01.toByte(),
                )
                val mfEf01SelectCommandResult = isoDep.transceive(mfEf01SelectCommand)
                if (mfEf01SelectCommandResult[0] != 0x90.toByte()) {
                    throwExceptionAndClose("DF/EF01の選択に失敗しました。${mfSelectCommandResult.toHexString()}")
                }

                // カレントディレクトリを読み取る
                val mfEf01ReadBinaryCommand = byteArrayOf(
                    0x00.toByte(),
                    0xB0.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x11.toByte()
                )
                val mfEf01ReadBinaryCommandResult = isoDep.transceive(mfEf01ReadBinaryCommand)
                // 成功した場合、最後から2番目の16進数が0x90
                if (mfEf01ReadBinaryCommandResult[mfEf01ReadBinaryCommandResult.size - 2] == 0x90.toByte()) {
                    // カード発行者データの長さを取得
                    val cardPublisherDataLength = mfEf01ReadBinaryCommandResult[1].toInt() // 多分11
                    // 先頭から cardPublisherDataLength 分のバイト配列取得
                    val cardPublisherDataBinary = mfEf01ReadBinaryCommandResult.copyOfRange(2, 2 + cardPublisherDataLength)
                    // カード発狂者データ、最初の3バイトが仕様書バージョン（SJIS変換後確認可能）、次の4バイトが交付年月日、次の4バイトが有効期限
                    val version = cardPublisherDataBinary.copyOfRange(0, 3).toString(charset("sjis"))
                    val publishDate = cardPublisherDataBinary.copyOfRange(4, 7).joinToString(separator = "") { "%02x".format(it) }
                    val dateOfExpiry = cardPublisherDataBinary.copyOfRange(8, 11).joinToString(separator = "") { "%02x".format(it) }
                    mfeF01Data = JDCardMFEF01Data(version, publishDate, dateOfExpiry)
                } else {
                    throwExceptionAndClose("DF/EF01読み取りに失敗しました。${mfSelectCommandResult.toHexString()}")
                }

                // 残り照合可能回数を取得する
                val retryCountVerifyCommand = byteArrayOf(
                    0x00.toByte(),
                    0x20.toByte(),
                    0x00.toByte(),
                    0x81.toByte()
                )
                val retryCountVerifyCommandResult = isoDep.transceive(retryCountVerifyCommand)
                if (retryCountVerifyCommandResult[0] == 0x63.toByte()) {
                    val retryCountHex = retryCountVerifyCommandResult.last().toInt() - 0xC0 // 0xC0を引けば最後が残る
                    retryCount = "%x".format(retryCountHex).last().toString().toInt() // ffffff03みたいな感じになる、ので最後だけ取得
                } else {
                    throwExceptionAndClose("残り試行可能回数の取得に失敗しました。${retryCountVerifyCommandResult.toHexString()}")
                }

                // 暗証番号1を照合する
                val pinCode1CharList = pinCode1.toCharArray() // 各自暗証番号を入力
                val pinCode1EncodedList = pinCode1CharList.map { toJIS(it.toString()[0]) }
                val pinCode1VerifyCommand = byteArrayOf(
                    0x00.toByte(),
                    0x20.toByte(),
                    0x00.toByte(),
                    0x81.toByte(),
                    0x04.toByte(),
                ) + pinCode1EncodedList
                val pinCode1VerifyCommandResult = isoDep.transceive(pinCode1VerifyCommand)
                if (pinCode1VerifyCommandResult[pinCode1VerifyCommandResult.size - 2] != 0x90.toByte()) {
                    throwExceptionAndClose("暗証番号１の照合に失敗。${pinCode1VerifyCommandResult.toHexString()}")
                }

                // カレントディレクトリをDF1へ
                val df1SelectCommand = byteArrayOf(
                    0x00.toByte(),
                    0xA4.toByte(),
                    0x04.toByte(),
                    0x0C.toByte(),
                    0x10.toByte(),
                    0xA0.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x02.toByte(),
                    0x31.toByte(),
                    0x01.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                )
                val df1SelectCommandResult = isoDep.transceive(df1SelectCommand)
                if (df1SelectCommandResult[0] != 0x90.toByte()) {
                    throwExceptionAndClose("DF1の選択に失敗。${df1SelectCommandResult.toHexString()}")
                }

                // 記載事項(DF1/EF01)を読み出す
                val df1Ef01ReadBinaryCommand = byteArrayOf(
                    0x00.toByte(),
                    0xB0.toByte(),
                    0x81.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x03.toByte(),
                    0x70.toByte(),
                )
                val df1Ef01ReadBinaryCommandResult = isoDep.transceive(df1Ef01ReadBinaryCommand)
                if (df1Ef01ReadBinaryCommandResult[df1Ef01ReadBinaryCommandResult.size - 2] == 0x90.toByte()) {

                    var currentPos = 0
                    // 記載事項を上から順番に取得していく
                    val byteArrayList = mutableListOf<ByteArray>()
                    repeat(35) {
                        val (pos, data) = df1Ef01ReadBinaryCommandResult.getValueField(currentPos)
                        byteArrayList.add(data)
                        currentPos = pos
                    }

                    // JIS X 0208 制定年番号
                    val jisX0208 = "%02x".format(byteArrayList[0].last())
                    // 名前
                    val name = byteArrayList[1].toJISX0208()
                    // 読み
                    val yomi = byteArrayList[2].toJISX0208()
                    // 通称名
                    val tuusyoumei = byteArrayList[3].toJISX0208()
                    // 統一氏名
                    val touitusimei = byteArrayList[4].toJISX0208()
                    // 生年月日
                    val birthday = byteArrayList[5].toJISX0201DateString()
                    // 住所
                    val location = byteArrayList[6].toJISX0208()
                    // 交付年月日
                    val publishDate = byteArrayList[7].toJISX0201DateString()
                    // 照会番号
                    val syoukaiNum = byteArrayList[8].toJISX0201()
                    // 免許証の色区分
                    val color = byteArrayList[9].toJISX0208()
                    // 有効期限
                    val endTimeAt = byteArrayList[10].toJISX0201DateString()
                    // 運転免許の条件。メガネなど。条件ない場合はnull
                    val requirement1 = byteArrayList[11].toJISX0208().ifEmpty { null }
                    val requirement2 = byteArrayList[12].toJISX0208().ifEmpty { null }
                    val requirement3 = byteArrayList[13].toJISX0208().ifEmpty { null }
                    val requirement4 = byteArrayList[14].toJISX0208().ifEmpty { null }
                    // 公安委員会名
                    val publicSafetyCommissionName = byteArrayList[15].toJISX0208()
                    // 運転免許証の番号
                    val cardNumber = byteArrayList[16].toJISX0201()
                    // 他の免許
                    val nirin = byteArrayList[17].toJISX0201DateString()
                    val hoka = byteArrayList[18].toJISX0201DateString()
                    val nisyu = byteArrayList[19].toJISX0201DateString()
                    val oogata = byteArrayList[20].toJISX0201DateString()
                    val hutuu = byteArrayList[21].toJISX0201DateString()
                    val oogatatokusyu = byteArrayList[22].toJISX0201DateString()
                    val oogatazidounirin = byteArrayList[23].toJISX0201DateString()
                    val hutuuzidounirin = byteArrayList[24].toJISX0201DateString()
                    val kogatatokusyu = byteArrayList[25].toJISX0201DateString()
                    val gentuki = byteArrayList[26].toJISX0201DateString()
                    val kanninn = byteArrayList[27].toJISX0201DateString()
                    val oogatanisyu = byteArrayList[28].toJISX0201DateString()
                    val hutuunisyu = byteArrayList[29].toJISX0201DateString()
                    val oogataokusyunisyu = byteArrayList[30].toJISX0201DateString()
                    val kenninnnisyu = byteArrayList[31].toJISX0201DateString()
                    val tyuugata = byteArrayList[32].toJISX0201DateString()
                    val tyuugatanisyu = byteArrayList[33].toJISX0201DateString()
                    val zyuntyuugata = byteArrayList[34].toJISX0201DateString()

                    dF1EF01Data = JDCardDF1EF01Data(
                        jisX0208,
                        name,
                        yomi,
                        tuusyoumei,
                        touitusimei,
                        birthday!!,
                        location,
                        publishDate!!,
                        syoukaiNum,
                        color,
                        endTimeAt!!,
                        requirement1,
                        requirement2,
                        requirement3,
                        requirement4,
                        publicSafetyCommissionName,
                        cardNumber,
                        nirin,
                        hoka,
                        nisyu,
                        oogata,
                        hutuu,
                        oogatatokusyu,
                        oogatazidounirin,
                        hutuuzidounirin,
                        kogatatokusyu,
                        gentuki,
                        kanninn,
                        oogatanisyu,
                        hutuunisyu,
                        oogataokusyunisyu,
                        kenninnnisyu,
                        tyuugata,
                        tyuugatanisyu,
                        zyuntyuugata,
                    )
                } else {
                    throwExceptionAndClose("DF1/EF01の読み出しに失敗。${df1Ef01ReadBinaryCommandResult.toHexString()}")
                }

                /**
                 * 暗証番号２が渡されている場合は本籍も取得する
                 *
                 * なぜか本籍は暗証番号２が必要なので（本籍って千代田区千代田1-1とか自由に決められるらしいね？）
                 * */
                if (pinCode2 != null) {
                    // MFを選択する
                    val pin2MfSelectCommandResult = isoDep.transceive(mfSelectCommand)
                    if (pin2MfSelectCommandResult[0] != 0x90.toByte()) {
                        throwExceptionAndClose("MF選択に失敗しました。${pin2MfSelectCommandResult.toHexString()}")
                    }
                    // IEF02(暗証番号２)を指定したVERIFYコマンドを送る
                    val pinCode2CharList = pinCode2.toCharArray() // 各自暗証番号を入力
                    val pinCode2EncodedList = pinCode2CharList.map { toJIS(it.toString()[0]) }
                    val pinCode2VerifyCommand = byteArrayOf(
                        0x00.toByte(),
                        0x20.toByte(),
                        0x00.toByte(),
                        0x82.toByte(), // IEF02選択
                        0x04.toByte(),
                    ) + pinCode2EncodedList
                    val pinCode2VerifyCommandResult = isoDep.transceive(pinCode2VerifyCommand)
                    if (pinCode2VerifyCommandResult[pinCode2VerifyCommandResult.size - 2] != 0x90.toByte()) {
                        throwExceptionAndClose("暗証番号２の照合に失敗。${pinCode2VerifyCommandResult.toHexString()}")
                    }
                    // 本籍を読み出すためにDF1へ移動
                    val pin2Df1SelectCommandResult = isoDep.transceive(df1SelectCommand)
                    if (pin2Df1SelectCommandResult[0] != 0x90.toByte()) {
                        throwExceptionAndClose("DF1の選択に失敗。${pin2Df1SelectCommandResult.toHexString()}")
                    }
                    // 本籍（DF1/EF02）を読み出す
                    val df1Ef02ReadBinaryCommand = byteArrayOf(
                        0x00.toByte(),
                        0xB0.toByte(),
                        0x82.toByte(),
                        0x00.toByte(),
                        0x00.toByte(),
                        0x03.toByte(),
                        0x70.toByte(),
                    )
                    val df1Ef02ReadBinaryCommandResult = isoDep.transceive(df1Ef02ReadBinaryCommand)
                    if (df1Ef02ReadBinaryCommandResult[df1Ef02ReadBinaryCommandResult.size - 2] == 0x90.toByte()) {
                        val honsekiLength = df1Ef02ReadBinaryCommandResult[1]
                        val honsekiData = df1Ef02ReadBinaryCommandResult.copyOfRange(2, 2 + honsekiLength)
                        honseki = honsekiData.toJISX0208()
                    } else {
                        throwExceptionAndClose("DF1/EF02の読み出しに失敗。${pin2Df1SelectCommandResult.toHexString()}")
                    }
                }

                // おかたずけ
                isoDep.close()
                nfcAdapter.disableReaderMode(activity)

                // 値を返す
                callbackCoroutine.resume(JDCardData(mfeF01Data!!, retryCount, dF1EF01Data!!, honseki))
            }
        }
    }

    /** 16進数に変換するやつ */
    private fun ByteArray.toHexString() = this.joinToString { "%02x".format(it) }

    /**
     * 次のデータを取得する
     * @param currentPos 今の位置。初回時は0？
     * @return Intは、今の位置を返します。２回目以降この関数を呼ぶ際に使ってください、ByteArrayは値フィールドです
     * */
    private fun ByteArray.getValueField(currentPos: Int): Pair<Int, ByteArray> {
        // 長さを読み取る
        val length = this[currentPos + 1]
        return currentPos + 2 + length to copyOfRange(currentPos + 2, currentPos + 2 + length)
    }

    /** JIS X 0208で変換されたバイト配列を戻す */
    private fun ByteArray.toJISX0208(): String {
        // 変換する。JISコードで変換できる。JISコードはエスケープシーケンスにより、文字集合を切り替えることができる
        val escapeSequence = byteArrayOf(0x1B.toByte(), 0x24.toByte(), 0x42.toByte()) // JIS X 0208
        return String(escapeSequence + this, charset("jis"))
    }

    /** JIS X 0201で変換されたバイト配列を戻す */
    private fun ByteArray.toJISX0201(): String {
        // 変換する。JISコードで変換できる。JISコードはエスケープシーケンスにより、文字集合を切り替えることができる
        val escapeSequence = byteArrayOf(0x1B.toByte(), 0x28.toByte(), 0x42.toByte()) // ASCII
        return String(escapeSequence + this, charset("jis"))
    }

    /**
     * JIS X 0201で変換されたバイト配列を戻して、日付形式にする
     * @return nullの場合は不正な値の場合（例えば普通免許以外持っていない場合は00000なのでそのときはnullを返します。）
     * */
    private fun ByteArray.toJISX0201DateString(): String? {
        // とりあえずJIS X 0201の変換後データを取得
        val valueField = this.toJISX0201()
        // 持ってない免許の場合は (元号)000000 なので
        if (valueField.contains("000000")) return null
        val gengo = when (valueField.first()) {
            '1' -> "明治"
            '2' -> "大正"
            '3' -> "昭和"
            '4' -> "平成"
            else -> "令和"
        }
        val year = valueField.substring(1, 3)
        val month = valueField.substring(3, 5)
        val date = valueField.substring(5, 7)
        return "$gengo ${year}年 ${month}月 ${date}日"
    }

    /** 数値文字をJIS X 0201にエンコードする */
    private fun toJIS(c: Char): Byte {
        return when (c) {
            '0' -> 0x30
            '1' -> 0x31
            '2' -> 0x32
            '3' -> 0x33
            '4' -> 0x34
            '5' -> 0x35
            '6' -> 0x36
            '7' -> 0x37
            '8' -> 0x38
            '9' -> 0x39
            else -> 0x00
        }.toByte()
    }

}