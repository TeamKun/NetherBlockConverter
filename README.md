# NetherBlockConverter
ネザー化プラグイン

## 動作環境
- Minecraft 1.16.5
- PaperMC 1.16.5

## コマンド

- nbc
    - add <playerName>

      指定したプレイヤー周辺をネザー化

    - rm <playerName>

      指定したプレイヤー周辺のネザー化を解除

    - gate-switch

      ネザーゲートのポータルを作成したプレイヤー周辺のネザー化のon/off切り替え

    - conf-reload

      以下を初期化します

      - gate-switch の状態
      - tick の値
      - range の値
      - ch-add/ch-rmで設定された内容

    - cb-add <Block1> <Block2> <overworld|nether>

      overworldまたはnether(指定した方)内でBlock1をBlock2に変更する設定を追加

    - cb-rm <Block> <overworld|nether>

      overworldまたはnether(指定した方)内でBlockを別のブロックに変換する設定を削除

    - conf-set

      コマンド単体だと現在の設定を確認

      - tick <数字>

        ネザー化の処理間隔を設定

      - range <数字>

        ネザー化の範囲を設定

## 設定のデフォルト値一覧

* 初期設定を持つ設定のデフォルト値,

| 設定名                                             | デフォルト値                                                 |
| -------------------------------------------------- | ------------------------------------------------------------ |
| gate-switch                                        | 周りをネザー化する                                           |
| tick                                               | 10                                                           |
| range                                              | 6                                                            |
| ブロックの変換リスト(cb-add/cb-rmで設定できる内容) | [CSVファイル](https://github.com/TeamKun/NetherBlockConverter/blob/master/src/main/resources/convertTable.csv)を参照 |

## cb-add/cb-rmコマンドについて

本コマンドは基本的には多用しない想定ですが、プラグインの利用の仕方によって、ブロックの変換内容を変えたい時に用います。例えば、黒曜石の変換については画面映えが大きく変わるので、必要に応じて下記の例のように実行することが予想されます。

* オーバーワールドで黒曜石を溶岩に変えたい場合

  ```
  /nbc cb-add OBSIDIAN LAVA overworld
  ```

* ネザーで黒曜石を水に変えたい場合

  ```
  /nbc cb-add OBSIDIAN WATER nether
  ```
