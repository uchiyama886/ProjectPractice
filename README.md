# ProjectPractice
京都産業大学プロジェクト演習アプリケーション開発コース2025年度グループ4のwaveletソース開発リポジトリ

## 開発中の作業の流れ

#### 初めにmainにプル（更新）しましょう
```
git pull origin main
```

#### できたら、次は作業ブランチを作成しましょう
```
git checkout -b "working-好きな名前"
```

#### 作業を行い変更したファイルをコミット（変更を保存しておく）しましょう
```
git commit -m "作業内容を書く(コミットメッセージのルールに従って書いてください)"
```

#### さいごに、コミットをプッシュ（みんなが変更をみられるようにする）しましょう
```
git push origin 作成したブランチ名
```

#### 作成したら、GitHub上でプルリクエストを作成しましょう
1. GitHubのリポジトリにアクセス

2. 「Pull requests」タブをクリック
  <img width="1200" alt="スクリーンショット 2025-05-23 14 29 13" src="https://github.com/user-attachments/assets/bd5fd491-8d25-4a72-89b9-e4ad8445e4d5" />

3. 「New pull request」ボタンをクリック  
  <img width="1412" alt="スクリーンショット 2025-05-23 14 30 03" src="https://github.com/user-attachments/assets/02472388-15a4-4e21-b0e8-75f8f27fc114" />

4. 「compare:」を自分のブランチ(今回はworking-ryo)に変更
  <img width="1409" alt="スクリーンショット 2025-05-23 14 31 55" src="https://github.com/user-attachments/assets/f2814cd5-cf75-4d6f-a60f-4ebbee78bcb8" />
     - 「base:」はmainのままにしておく

5. 「Create pull request」ボタンをクリック
  <img width="1407" alt="スクリーンショット 2025-05-23 14 32 37" src="https://github.com/user-attachments/assets/69a84f07-17ea-4ea3-a5ec-6c90b1ac67ff" />

6. タイトルと説明を記入して「Create pull request」ボタンをクリック
   <img width="1413" alt="スクリーンショット 2025-05-23 14 35 45" src="https://github.com/user-attachments/assets/9fa36c93-9d2f-441b-8ddb-b96d5b79200d" />
    - タイトルはデフォルトではコミットメッセージが入っているので、必要であれば変更してください
    - 説明はコミットメッセージより具体的に書いてください


## コミットメッセージのルール
- feat: 新しい機能
- fix: バグの修正
- docs: ドキュメントのみの変更
- style: 空白、フォーマット、セミコロン追加など
- refactor: 仕様に影響がないコード改善(リファクタ)
- perf: パフォーマンス向上関連
- test: テスト関連
- chore: ビルド、補助ツール、ライブラリ関連
