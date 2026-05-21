-- 家用备件库存统计 数据库建表脚本
-- 此文件为文档参考，Spring Boot JPA 会在启动时自动建表 (ddl-auto: update)

CREATE TABLE IF NOT EXISTS parts (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE,
    quantity    INT NOT NULL DEFAULT 0,
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS history_records (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    part_name   VARCHAR(100) NOT NULL,
    type        VARCHAR(3) NOT NULL COMMENT 'IN=入库, OUT=消耗',
    quantity    INT NOT NULL,
    record_date DATE NOT NULL,
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP
);
