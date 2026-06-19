USE vote_system;

DELIMITER //
CREATE PROCEDURE IF NOT EXISTS sp_get_user_by_username(
  IN p_username VARCHAR(50)
)
BEGIN
  SELECT id, username, password_hash, role, created_at FROM users WHERE username = p_username;
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE IF NOT EXISTS sp_insert_user(
  IN p_username VARCHAR(50),
  IN p_password_hash VARCHAR(255),
  IN p_role VARCHAR(20)
)
BEGIN
  INSERT INTO users (username, password_hash, role) VALUES (p_username, p_password_hash, p_role);
  SELECT LAST_INSERT_ID() AS id;
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE IF NOT EXISTS sp_find_item_by_name(IN p_name VARCHAR(100))
BEGIN
  SELECT id, name, created_at, updated_at FROM vote_items WHERE name = p_name LIMIT 1;
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE IF NOT EXISTS sp_get_all_items()
BEGIN
  SELECT v.id, v.name, v.created_at, v.updated_at, COUNT(r.id) AS vote_count
  FROM vote_items v LEFT JOIN vote_records r ON r.item_id = v.id
  GROUP BY v.id, v.name, v.created_at, v.updated_at ORDER BY v.id ASC;
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE IF NOT EXISTS sp_insert_item(IN p_name VARCHAR(100))
BEGIN
  INSERT INTO vote_items (name) VALUES (p_name);
  SELECT LAST_INSERT_ID() AS id;
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE IF NOT EXISTS sp_update_item(IN p_id INT, IN p_name VARCHAR(100))
BEGIN
  UPDATE vote_items SET name = p_name, updated_at = NOW() WHERE id = p_id;
  SELECT ROW_COUNT() AS affected;
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE IF NOT EXISTS sp_delete_item(IN p_id INT)
BEGIN
  DECLARE EXIT HANDLER FOR SQLEXCEPTION
  BEGIN ROLLBACK; SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = '刪除失敗，交易已回滾'; END;
  START TRANSACTION;
  DELETE FROM vote_records WHERE item_id = p_id;
  DELETE FROM vote_items WHERE id = p_id;
  COMMIT;
  SELECT ROW_COUNT() AS affected;
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE IF NOT EXISTS sp_has_voter_voted(IN p_voter VARCHAR(100))
BEGIN
  SELECT COUNT(*) AS cnt FROM vote_records WHERE voter = p_voter;
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE IF NOT EXISTS sp_cast_votes(IN p_voter VARCHAR(100), IN p_item_ids TEXT)
BEGIN
  DECLARE v_done INT DEFAULT 0;
  DECLARE v_item_id INT;
  DECLARE v_cursor CURSOR FOR
    SELECT CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(p_item_ids, ',', n), ',', -1) AS UNSIGNED) AS item_id
    FROM (SELECT @rownum := @rownum + 1 AS n FROM information_schema.COLUMNS CROSS JOIN (SELECT @rownum := 0) r LIMIT 100) nums
    WHERE n <= 1 + LENGTH(p_item_ids) - LENGTH(REPLACE(p_item_ids, ',', ''));
  DECLARE CONTINUE HANDLER FOR NOT FOUND SET v_done = 1;
  DECLARE EXIT HANDLER FOR SQLEXCEPTION
  BEGIN ROLLBACK; SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = '投票失敗，交易已回滾'; END;
  START TRANSACTION;
  SET @rownum := 0;
  OPEN v_cursor;
  read_loop: LOOP
    FETCH v_cursor INTO v_item_id;
    IF v_done THEN LEAVE read_loop; END IF;
    IF NOT EXISTS (SELECT 1 FROM vote_items WHERE id = v_item_id) THEN
      CLOSE v_cursor; ROLLBACK; SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = '投票項目不存在';
    END IF;
  END LOOP;
  CLOSE v_cursor;
  SET @rownum := 0; SET v_done := 0;
  OPEN v_cursor;
  insert_loop: LOOP
    FETCH v_cursor INTO v_item_id;
    IF v_done THEN LEAVE insert_loop; END IF;
    INSERT INTO vote_records (voter, item_id) VALUES (p_voter, v_item_id);
  END LOOP;
  CLOSE v_cursor;
  COMMIT;
  SELECT 'OK' AS result;
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE IF NOT EXISTS sp_get_item_by_id(IN p_id INT)
BEGIN
  SELECT v.id, v.name, v.created_at, v.updated_at, COUNT(r.id) AS vote_count
  FROM vote_items v LEFT JOIN vote_records r ON r.item_id = v.id
  WHERE v.id = p_id GROUP BY v.id, v.name, v.created_at, v.updated_at;
END //
DELIMITER ;
