# esunVote

----

> 專案名稱：esunVote  
> 技術架構：Vue 3 + Spring Boot + MySQL  
> 版本：1.0.0

---

## 目錄

1. [系統概述](#1-系統概述)
2. [系統架構](#2-系統架構)
3. [資料庫設計](#3-資料庫設計)
4. [後端 API 規格](#4-後端-api-規格)
5. [前端頁面規格](#5-前端頁面規格)
6. [技術棧](#6-技術棧)
7. [環境建置與啟動](#7-環境建置與啟動)
8. [目錄結構](#8-目錄結構)

---

## 1. 系統概述

線上投票系統提供兩個主要功能：

- **前台投票區**：使用者輸入姓名後勾選多個項目送出投票，可查看即時票數。
- **後台管理區**：管理者可新增、編輯、刪除投票項目。

### 系統特色

- 不需註冊／登入，輸入姓名即可投票
- 支援複選投票
- 票數即時更新
- 項目刪除時連動清除相關投票紀錄（外鍵 CASCADE）
- 全部資料庫操作皆透過 Stored Procedure

---

## 2. 系統架構

```
┌─────────────────────────────────────────────────────┐
│                   瀏覽器 (使用者)                      │
│              http://localhost:5173                   │
└──────────────────────┬──────────────────────────────┘
                       │
                 ┌─────┴─────┐
                 │  Vite Proxy │  （開發模式）
                 │   /api →    │
                 │ localhost:8080│
                 └─────┬─────┘
                       ▼
┌─────────────────────────────────────────────────────┐
│               Spring Boot 後端                        │
│              http://localhost:8080                   │
│                                                      │
│  ┌──────────────┐  ┌──────────────┐  ┌────────────┐ │
│  │ VoteController│→│ VoteService  │→│ SimpleJdbc  │ │
│  │ (展示層/REST) │  │ (業務層)     │  │ Call (資料層)│ │
│  └──────────────┘  └──────────────┘  └──────┬─────┘ │
└──────────────────────────────────────────────┼───────┘
                                               ▼
┌─────────────────────────────────────────────────────┐
│                    MySQL 資料庫                        │
│                  localhost:3306                      │
│                                                      │
│  ┌──────────────┐  ┌──────────────┐                  │
│  │ vote_items   │  │ vote_records │                  │
│  │ (投票項目)    │  │ (投票紀錄)    │                  │
│  └──────────────┘  └──────────────┘                  │
│                                                      │
│  5 支 Stored Procedure (SP01 ~ SP05)                  │
└─────────────────────────────────────────────────────┘
```

### 三層式架構對應

| 傳統分層 | 本專案實作 |
|----------|-----------|
| 展示層 | `VoteController.java`（REST API 入口） |
| 業務層 | `VoteService.java`（商業邏輯、@Transactional） |
| 資料層 | Stored Procedure（DB 內）+ `SimpleJdbcCall` 呼叫 |
| 共用層 | `model/` 下的 DTO（`VoteItem`、`VoteRequest`、`ItemRequest`） |

---

## 3. 資料庫設計

### 3.1 表格結構

#### `vote_items` — 投票項目表

| 欄位 | 型別 | 約束 | 說明 |
|------|------|------|------|
| id | INT | PRIMARY KEY, AUTO_INCREMENT | 項目編號 |
| name | VARCHAR(100) | NOT NULL, UNIQUE (uk_name) | 項目名稱（不可重複） |

#### `vote_records` — 投票紀錄表

| 欄位 | 型別 | 約束 | 說明 |
|------|------|------|------|
| id | INT | PRIMARY KEY, AUTO_INCREMENT | 紀錄編號 |
| voter | VARCHAR(50) | NOT NULL | 投票人姓名 |
| item_id | INT | NOT NULL, FOREIGN KEY → vote_items(id) ON DELETE CASCADE | 投給哪個項目 |

### 3.2 關聯圖

```
vote_items (1) ────── (N) vote_records
     id  ←──────────────── item_id（外鍵）
```

- 一個項目可以有多筆投票紀錄（一對多）
- 刪除項目時，該項目的所有投票紀錄自動刪除（`ON DELETE CASCADE`）

### 3.3 Stored Procedure 一覽

| 編號 | 名稱 | 功能 | 參數 |
|------|------|------|------|
| SP_01 | `sp_add_item` | 新增項目 | `IN p_name` |
| SP_02 | `sp_delete_item` | 刪除項目 | `IN p_id` |
| SP_03 | `sp_get_items_with_votes` | 取得所有項目與票數（LEFT JOIN） | 無 |
| SP_04 | `sp_update_item` | 修改項目名稱 | `IN p_id`, `IN p_name` |
| SP_05 | `sp_submit_vote` | 單筆投票（Java 端用 @Transactional 保護迴圈） | `IN p_voter`, `IN p_item_id` |

---

## 4. 後端 API 規格

Base URL：`http://localhost:8080/api`

### 4.1 投票項目

#### `GET /api/items`

取得所有項目與累計票數（前台投票頁使用）。

**Response `200`：**
```json
[
  { "id": 1, "name": "電腦", "totalVotes": 5 },
  { "id": 2, "name": "滑鼠", "totalVotes": 3 }
]
```

---

#### `POST /api/items`

新增投票項目。

**Request：**
```json
{ "name": "鍵盤" }
```

**Response `201`：**
```json
{ "status": "SUCCESS", "message": "新增成功" }
```

---

#### `PUT /api/items/{id}`

修改投票項目名稱。

**Request：**
```json
{ "name": "無線滑鼠" }
```

**Response `200`：**
```json
{ "status": "SUCCESS", "message": "更新成功" }
```

---

#### `DELETE /api/items/{id}`

刪除投票項目（相關投票紀錄一併刪除）。

**Response `200`：**
```json
{ "status": "SUCCESS", "message": "刪除成功" }
```

---

### 4.2 投票

#### `POST /api/votes`

提交投票（多選），需填寫投票人姓名。

**Request：**
```json
{ "voter": "Leo", "itemIds": [1, 2, 3] }
```

**Response `200`（成功）：**
```json
{ "status": "SUCCESS", "message": "多選投票成功！" }
```

**Response `409`（失敗）：**
```json
{ "status": "ERROR", "message": "投票失敗：系統發生異常！" }
```

---

### 4.3 錯誤回應格式

所有錯誤統一回傳：

```json
{ "status": "ERROR", "message": "錯誤說明" }
```

| HTTP 狀態碼 | 情境 |
|-------------|------|
| 400 | 參數驗證失敗（空名稱、空 itemIds、缺投票人） |
| 409 | 資料衝突（名稱重複、投票失敗） |

---

## 5. 前端頁面規格

### 5.1 投票區 `/vote`

| 項目 | 說明 |
|------|------|
| 功能 | 輸入姓名、瀏覽項目、勾選投票、查看即時票數 |
| 操作流程 | 輸入姓名 → 勾選 checkbox → 點「提交投票」→ 成功提示 → 自動刷新票數 |
| 限制 | 姓名必填、至少勾選一項才能提交 |

**【截圖：投票區頁面】**  
> 此處放 `screenshots/vote-page.png`

---

### 5.2 後台管理 `/admin`

| 項目 | 說明 |
|------|------|
| 功能 | 瀏覽所有項目、新增 / 編輯 / 刪除 |
| 新增 | 點「新增項目」→ 彈出對話框 → 輸入名稱 → 儲存 |
| 編輯 | 點「編輯」→ 彈出對話框（帶入原名稱）→ 修改 → 儲存 |
| 刪除 | 點「刪除」→ 二次確認 → 確定刪除 |
| 驗證 | 名稱必填、最長 100 字、不可與現有項目重複 |

**【截圖：後台管理頁面】**  
> 此處放 `screenshots/admin-page.png`

**【截圖：新增/編輯對話框】**  
> 此處放 `screenshots/admin-dialog.png`

---

### 5.3 導覽列

位於頁面頂端，包含：

- **系統標題**：線上投票系統
- **投票統計**：連結到 `/vote`
- **後台管理**：連結到 `/admin`

---

## 6. 技術棧

### 後端

| 技術 | 版本 | 用途 |
|------|------|------|
| Java | 17 | 程式語言 |
| Spring Boot | 3.4.1 | 應用框架 |
| Spring WebMVC | — | REST API |
| Spring JDBC | — | 資料庫連線（JdbcTemplate + SimpleJdbcCall） |
| MySQL Connector | — | MySQL JDBC 驅動 |
| Maven (mvnw) | — | 建置工具 |

### 前端

| 技術 | 版本 | 用途 |
|------|------|------|
| Vue | 3.4 | 前端框架（Options API） |
| Vite | 5 | 開發伺服器與建置工具 |
| Element Plus | 2.5 | UI 元件庫 |
| Vue Router | 4 | 前端路由 |
| Axios | 1.6 | HTTP 客戶端 |

### 資料庫

| 技術 | 版本 | 用途 |
|------|------|------|
| MySQL | 8.x | 關聯式資料庫 |
| Stored Procedure | — | 封裝資料存取邏輯於資料庫層 |

---

## 7. 環境建置與啟動

### 7.1 前置需求

- JDK 17+
- Node.js 18+
- MySQL 8.x
- Maven（或使用專案內建的 `mvnw`）

### 7.2 資料庫初始化

```bash
# 建立資料庫與表格
mysql -u root < DB/init.sql

# 建立 Stored Procedure
mysql -u root < DB/dml.sql
```

### 7.3 啟動後端

```bash
cd backend
./mvnw spring-boot:run
```

後端啟動於 `http://localhost:8080`

### 7.4 啟動前端

```bash
cd frontend
npm install
npm run dev
```

前端啟動於 `http://localhost:5173`

### 7.5 驗證

```bash
# 測試後端 API
curl http://localhost:8080/api/items

# 預期回傳：
# [{"id":1,"name":"電腦","totalVotes":0},{"id":2,"name":"滑鼠","totalVotes":0}]
```

---

## 8. 目錄結構

```
esunVote/
├── .gitignore                         # Git 忽略規則
├── README.md                          # 專案簡介
├── 系統規格說明書.md                   # 本文件
├── 從零開始學習計畫.md                  # 學習指南
│
├── DB/                                # 資料庫腳本
│   ├── init.sql                       # 表格建立 + 種子資料
│   └── dml.sql                        # Stored Procedure
│
├── backend/                           # 後端（Spring Boot）
│   ├── pom.xml                        # Maven 依賴設定
│   └── src/main/java/com/example/backend/
│       ├── BackendApplication.java    # 啟動類別
│       ├── config/
│       │   └── WebConfig.java         # CORS 設定
│       ├── controller/
│       │   └── VoteController.java    # REST API（展示層）
│       ├── service/
│       │   └── VoteService.java       # 業務邏輯 + @Transactional
│       └── model/
│           ├── VoteItem.java          # 項目資料模型
│           ├── VoteRequest.java       # 投票請求 DTO（含 voter）
│           └── ItemRequest.java       # 項目請求 DTO
│
├── frontend/                          # 前端（Vue 3）
│   ├── index.html                     # HTML 進入點
│   ├── vite.config.js                 # Vite + Proxy 設定
│   ├── package.json                   # npm 依賴
│   └── src/
│       ├── main.js                    # Vue 進入點
│       ├── App.vue                    # 根元件（導覽列）
│       ├── router/index.js            # 路由設定
│       ├── api/index.js               # Axios API 封裝
│       └── views/
│           ├── VoteView.vue           # 投票區頁面
│           └── AdminView.vue          # 後台管理頁面
│
└── screenshots/                       # 截圖放置處（自行建立）
    ├── vote-page.png
    ├── admin-page.png
    └── admin-dialog.png
```

---

> 文件建立日期：2026-06-19
