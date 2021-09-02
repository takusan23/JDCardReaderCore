package io.github.takusan23.jdcardreadercore.data

/**
 * 運転免許証IC仕様書11ページ目参照
 *
 * 記載事項のデータクラス
 *
 * @param jisX0208Year JIS X 0208 制定年番号
 * @param name 氏名
 * @param yomi 読み（カタカナ）
 * @param tuusyoumei 通称名
 * @param touitsusimei 統一氏名
 * @param birthday 誕生日
 * @param address 住所
 * @param publishDate 交付年月日
 * @param syoukaiNumber 照会番号
 * @param cardColor 運転免許証の色。新規とか
 * @param endDate 有効期限
 * @param requirement1 免許の条件１。メガネとか。記載ない場合はnullです。
 * @param requirement2 免許の条件２。メガネとか。記載ない場合はnullです。
 * @param requirement3 免許の条件３。メガネとか。記載ない場合はnullです。
 * @param requirement4 免許の条件４。メガネとか。記載ない場合はnullです。
 * @param publicSafetyCommissionName 公安委員会名
 * @param cardNumber 運転免許証の番号
 * @param nirin 免許の年月日(二・小・原)。ない場合はnull
 * @param hoka 免許の年月日(他)。ない場合はnull
 * @param nisyu 免許の年月日(二種)。ない場合はnull
 * @param oogata 免許の年月日(大型)。ない場合はnull
 * @param hutuu 免許の年月日(普通)。ない場合はnull
 * @param oogatatokusyu 免許の年月日(大特)。ない場合はnull
 * @param oogatazidounirin 免許の年月日(大自二)。ない場合はnull
 * @param hutuuzidounirin 免許の年月日(普自二)。ない場合はnull
 * @param kogatatokusyu 免許の年月日(小特)。ない場合はnull
 * @param gentuki 免許の年月日(原付)。ない場合はnull
 * @param kanninn 免許の年月日(け引)。ない場合はnull
 * @param oogatanisyu 免許の年月日(大二)。ない場合はnull
 * @param hutuunisyu 免許の年月日(普二)。ない場合はnull
 * @param oogataokusyunisyu 免許の年月日(大特二)。ない場合はnull
 * @param kenninnnisyu 免許の年月日(け引二)。ない場合はnull
 * @param tyuugata 免許の年月日(中型)。ない場合はnull
 * @param tyuugatanisyu 免許の年月日(中二)。ない場合はnull
 * @param zyuntyuugata 免許の年月日(準中型)。ない場合はnull
 * */
data class JDCardDF1EF01Data(
    val jisX0208Year: String,
    val name: String,
    val yomi: String,
    val tuusyoumei: String,
    val touitsusimei: String,
    val birthday: String,
    val address: String,
    val publishDate: String,
    val syoukaiNumber: String,
    val cardColor: String,
    val endDate: String,
    val requirement1: String?,
    val requirement2: String?,
    val requirement3: String?,
    val requirement4: String?,
    val publicSafetyCommissionName: String,
    val cardNumber: String,
    val nirin: String?,
    val hoka: String?,
    val nisyu: String?,
    val oogata: String?,
    val hutuu: String?,
    val oogatatokusyu: String?,
    val oogatazidounirin: String?,
    val hutuuzidounirin: String?,
    val kogatatokusyu: String?,
    val gentuki: String?,
    val kanninn: String?,
    val oogatanisyu: String?,
    val hutuunisyu: String?,
    val oogataokusyunisyu: String?,
    val kenninnnisyu: String?,
    val tyuugata: String?,
    val tyuugatanisyu: String?,
    val zyuntyuugata: String?,
)