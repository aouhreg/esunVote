USE vote;

DELIMITER //

-- 【SP_01】新增投票項目
CREATE PROCEDURE sp_add_item(IN p_name VARCHAR(100))
BEGIN
    INSERT INTO vote_items (name) VALUES (p_name);
END //

-- 【SP_02】刪除投票項目
CREATE PROCEDURE sp_delete_item(IN p_id INT)
BEGIN
    DELETE FROM vote_items WHERE id = p_id;
END //

-- 【SP_03】取得所有項目 + 累計票數
CREATE PROCEDURE sp_get_items_with_votes()
BEGIN
    SELECT 
        vi.id AS item_id,
        vi.name AS item_name,
        COUNT(vr.id) AS total_votes
    FROM vote_items vi
    LEFT JOIN vote_records vr ON vi.id = vr.item_id
    GROUP BY vi.id, vi.name;
END //

-- 【SP_04】修改投票項目名稱
CREATE PROCEDURE sp_update_item(IN p_id INT, IN p_name VARCHAR(100))
BEGIN
    UPDATE vote_items SET name = p_name WHERE id = p_id;
END //

-- 【SP_05】單筆投票
CREATE PROCEDURE sp_submit_vote(IN p_voter VARCHAR(50), IN p_item_id INT)
BEGIN
    INSERT INTO vote_records (voter, item_id) VALUES (p_voter, p_item_id);
END //

DELIMITER ;
