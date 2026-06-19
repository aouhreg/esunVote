
CREATE DATABASE IF NOT EXISTS vote
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE vote;

CREATE TABLE IF NOT EXISTS vote_items(
    id INT NOT NULL AUTO_INCREMENT,      
    name VARCHAR(100) NOT NULL,          
    PRIMARY KEY(id),
    UNIQUE KEY uk_name(name)              
)ENGINE = InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS vote_records(
    id INT NOT NULL AUTO_INCREMENT,   
    voter VARCHAR(50) NOT NULL,          
    item_id INT NOT NULL,                 
    PRIMARY KEY(id),
    INDEX idx_item_id(item_id),           -- 加速依 item_id 查詢票數
    CONSTRAINT fk_records_item
    FOREIGN KEY(item_id) REFERENCES vote_items(id) ON DELETE CASCADE  -- 項目刪除時，相關票數自動刪除
)ENGINE = InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 寫入兩個預設的投票項目
INSERT INTO vote_items (name) VALUES ('電腦'), ('滑鼠');
