# CLAUDE.md

Claude Code (claude.ai/code) がこのリポジトリで作業する際のガイドです。

## プロジェクト概要

Minecraft Fabric MOD（1.21.11）。100ブロックのワールドで、地上（y>=64）の敵モブを全滅させて30秒維持すれば勝ちのミニゲーム。Java 21で記述。[lightgame100](https://github.com/stakiran/lightgame100) の亜種。仕様は `CONTEXT.md`。

Yarn マッピングは 1.21.11 が最後のため、これ以上のバージョンアップには Mojang マッピングへの移行が必要。

## ビルド・実行

```bash
./gradlew build          # ビルド → build/libs/mopup-*.jar
./gradlew vscode         # VSCode プロジェクトファイル生成
./gradlew runClient      # 開発用クライアント起動
```

テストスイートはなし。

## アーキテクチャ

`src/main/java/com/stakiran/mopup/` に7クラス:

- **MopupMod** — エントリポイント。コマンドとティックイベントを登録。
- **MopupCommand** — `/mopup {item,setup}` のBrigadierコマンドツリー。
- **GameManager** — コアゲームロジック。フェーズ（IDLE → GAME → WON / LOST）、setup時のコマンド実行、毎ティックのターゲットモブ集計、発光（1分毎に10秒）、30秒カウントダウン、勝利処理、死亡時のゲームオーバー処理。全状態はstaticフィールド。
- **TargetMobs** — ターゲットモブの判定。
- **SpawnScanner** — setup時に規模（ボーダー内・y>=64 で敵モブが湧ける場所の数）を屋外／屋内・洞窟に分けて数える。屋外は空の光12以上。
- **HudManager** — プレイヤーごとのボスバーに敵数・カウントダウン・Y座標・発光タイミングをまとめて表示。
- **KitManager** — 初期アイテム（lightgame100 のキットのエリトラとロケット花火を松明に置き換えたもの）。

## 重要事項

- 全ゲーム状態はGameManagerのstaticフィールド。シングルゲームインスタンスのみ。
- `/mopup setup` はいつでも呼べ、ゲームのリセットを兼ねる。
- バージョン定義は `gradle.properties`（Minecraft、Yarnマッピング、Fabric loader/API、Loom）。
- UIメッセージは日本語。
