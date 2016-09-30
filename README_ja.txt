
# ========================================================================================
#                                                                                 Overview
#                                                                                 ========
「ermaster-b」を元にさせて頂いています。
https://github.com/naoki-iwami/ermaster-b


# ========================================================================================
#                                                                                Direction
#                                                                                =========
【改善したい箇所】
o カラムのデータ型の管理をシンプルに => 追加しやすいように
o 保存ファイルのXMLをシンプルに => 辞書機能なし
o 全体ビューでもコメント書けるように

【削りたい機能】
o 辞書機能、翻訳 => xmlをすっきりさせたい
o Hibernateのなんとかって機能
o ANTを利用したなんとか機能
o マイナーなオプションを弾く

【残したい機能】
o ERMasterのモデリング機能
o DDLの吐き出し
o ダイアグラム分割機能
o ctrl+Oによるアウトライン検索

【バグ？】
o 全体ビューとダイアグラムビューを行き来すると、Tool Bar Visibility が増えて楽しい

【Converter】
o &lt;現在日時&gt; => MySQLならNOW(), ...


# ========================================================================================
#                                                                              Development
#                                                                              ===========
【ブランチ】
mission_slim にて、ひたすらスリムにしていく

【ハッシュタグ】
o #willdelete :: 削除するぞ (いずれ抹消する)
o #deleted :: 削除したぞ
o #willanalyze :: 分析するぞ (そのうち)
o #analyzed :: 分析したぞ

# ========================================================================================
#                                                                                Analyzing
#                                                                                =========
【パッケージ構成】
src/org.insightech.er
 |-common     // 共通のコンポーネント (20個くらい)
 |  |-dialog
 |  |-exception
 |  |-widgets
 |-db         // ☆DBMSに関するクラス!? (多め)
 |  |-impl
 |  |-sqltype
 |  |-DBManager
 |  |-...
 |-editor     // ☆エディター、根幹!? (めちゃ多め)
 |  |-controller
 |  |  |-command
 |  |  |-editpart
 |  |  |-editpolicy
 |  |-model
 |  |  |-dbexport         // DDLとか画像へのエクスポート
 |  |  |-dbimport         // 要はDBからのリバース
 |  |  |-diagram_contents // ダイアグラムの内容を保持するクラスなど
 |  |  |-edit             // CopyManagerしかいないぞー
 |  |  |-search           // 検索のためのクラス
 |  |  |-settings         // 設定のためのクラス
 |  |  |-tracking         // 変更などのトラッキング
 |  |  |-ERDiagram
 |  |  |-...
 |  |-persistent
 |  |  |-impl
 |  |  |  |-PersistentSerializeImpl
 |  |  |  |-PersistentXmlImpl
 |  |  |  |-XMLLoader
 |  |  |-Persistent
 |  |-view
 |  |  |-action
 |  |  |-contributor
 |  |  |-dialog
 |  |  |-drag_drop
 |  |  |-editmanager
 |  |  |-figure
 |  |  |-outline
 |  |  |-property_source
 |  |  |-tool
 |  |  |-ERDiagramGotoMarker
 |  |  |-ERDiagramOnePopupMenuManager
 |  |  |-ERDiagramPopupMenuManager
 |  |-ERDiagramEditor
 |  |-ERDiagramElementStateListener
 |  |-ErDiagramInformationControl
 |  |-ERDiagramMultiPageEditor
 |  |-EROneDiagramEditor
 |  |-TestEditor
 |  |-TranslationResources
 |
 |-extention  // 拡張ポイント!? (少ない)
 |-preference // Eclipseのpreferences (少なめ)
 |  |-jdbc           // JDBCドライバーの設定
 |  |-template       // DB定義書テンプレート
 |  |-translation    // 翻訳辞書
 |  |-PreferencePage
 |  |-...
 |
 |-test       // main()から実行するテストクラス (少なめ)
 |-util       // ちょっとしたユーティリティ (少なめ)
 |-wizard     // Eclipseのウィザード、新規ER図とか (少なめ)
 |  |-page
 |  |  |-NewDiagramWizardPage1
 |  |  |-NewDiagramWizardPage2
 |  |-NewDiagramWizard
 |
 |-Activator
 |-ImageKey
 |-InternalDirectoryDialog
 |-InternalFileDialog
 |-Resources
 |-ResourceString
 |-ERDiagram_ja.properties
 |-ERDiagram.properties

