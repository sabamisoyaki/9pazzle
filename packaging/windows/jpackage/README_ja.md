# EXE化用データ（jpackage）

このフォルダは `9pazzle` を Windows 用 EXE にするための同梱データです。

## 必要なもの
- JDK 17+（`jpackage` コマンドを含む）
- ルートに `app.jar`
- JavaFX JMODS
  - 配置先: `openjfx-25.0.1_windows-x64_bin-sdk/javafx-jmods-25.0.1`
  - 例: `javafx.controls.jmod`, `javafx.graphics.jmod`, `javafx.fxml.jmod`, `javafx.media.jmod`

## 生成手順
1. `app.jar` をプロジェクトルートに置く
2. JavaFX jmods を上記パスに配置
3. `packaging/windows/jpackage/build-exe.bat` を実行
4. 生成物は `packaging/windows/jpackage/dist` に出力

## 自動同梱されるデータ
- `app.jar`
- `images/`（存在する場合）
- `audio/`（存在する場合）

## 任意データ
- `assets/app.ico` を置くとインストーラ/アプリのアイコンとして使用
- `config/launcher.properties` でランチャー設定を変更可能

## 備考
- 配布時は各種ライセンス表記を `LICENSE.txt` および別途同梱資料で整備してください。
