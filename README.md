# JDCardReaderCore
Androidで使える運転免許証読み取りライブラリ

[![](https://jitpack.io/v/takusan23/JDCardReaderCore.svg)](https://jitpack.io/#takusan23/JDCardReaderCore)

# サンプルアプリ
`app`フォルダがそうです。`Jetpack Compose`で出来てます。

# 対応している

- MF/EF01 共通データ要素
- 残り暗証番号試行可能回数
- DF1/EF01 記載事項
  - 氏名とか住所とか
- 本籍
  - 暗証番号２が必要

## 対応してない

- JIS X 0208以外の文字
- 顔写真
- 対応しているに書いてないこと

# 導入
`JitPack`で公開しています。

`settings.gradle`を開いて、`repositories{ }`に書き足します。

```gradle
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        jcenter() // Warning: this repository is going to shut down soon
        maven { url 'https://jitpack.io' } // これ
    }
}
```

そしたら、`build.gradle`を開いて書き足します。コルーチンを利用しているのでコルーチンのライブラリも必要です。

```gradle
dependencies {
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.0'
    implementation 'androidx.lifecycle:lifecycle-runtime-ktx:2.3.1'
    implementation 'com.github.takusan23:JDCardReaderCore:1.0.0'

    // 省略
}
```

# 使い方

## NFCの権限を宣言

`AndroidManifest.xml`に書き足します。

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="io.github.takusan23.japandriverlicensecardreader">

    <uses-permission android:name="android.permission.NFC"/>

```

## MainActivity.kt とかで
こんな感じ。第二引数は暗証番号１、第３引数は本籍を取得したい場合は暗証番号２を入力、取得しない場合は省略してください。  
コルーチンを使ってるので、関数を呼ぶ場所には注意してください。

```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lifecycleScope.launch {

            // 暗証番号１のみ
            val cardData = JDCardReaderCore.startGetCardData(this@MainActivity, "0000")

            println("誕生日")
            println(cardData.jdCardDF1EF01Data.birthday)

        }

    }
}
```

`Logcat`にこう出れば成功。

```
I/System.out: 誕生日
I/System.out: 平成 14年 09月 13日
```

## Composeで使う場合？
`produceState()`を使うとちょっと便利？

`InfoScreen.kt`を参照してください。

# その他

暗証番号を３回間違えるとICカードがロックされます。  
ロックされたら、試験場か警察署まで足を運ぶことになるので注意してください（一敗）

# 解説？

https://takusan.negitoro.dev/posts/android_nfc_japan_driver_license_card_reader/

# ライセンス

```
Copyright 2021 takusan_23

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
