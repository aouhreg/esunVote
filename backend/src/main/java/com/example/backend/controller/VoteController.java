package com.example.backend.controller;

import com.example.backend.model.ItemRequest;
import com.example.backend.model.VoteItem;
import com.example.backend.model.VoteRequest;
import com.example.backend.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api")
public class VoteController {

    @Autowired
    private VoteService voteService;

    // GET /api/items
    // 取得所有投票項目與票數
    @GetMapping("/items")
    public ResponseEntity<List<VoteItem>> getAllItemsWithVotes() {
        List<VoteItem> items = voteService.getItemWithVotes();
        return ResponseEntity.ok(items);
    }

    //POST /api/items
    //新增投票項目，檢查名稱不能空白，也要處理名稱重複（DataIntegrityViolationException）。
    @PostMapping("/items")
    public ResponseEntity<Map<String, Object>> createItem(@RequestBody ItemRequest request) {
        String name = request.getName();
        if (name == null || name.trim().isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "ERROR");
            error.put("message", "投票項目名稱不可為空");
            return ResponseEntity.badRequest().body(error);
        }

        try {
            voteService.addItem(name.trim());

            Map<String, Object> success = new HashMap<>();
            success.put("status", "SUCCESS");
            success.put("message", "新增成功");
            return ResponseEntity.status(HttpStatus.CREATED).body(success);

        } catch (DataIntegrityViolationException e) {
            // 名字重複（uk_name 唯一鍵衝突）
            Map<String, Object> error = new HashMap<>();
            error.put("status", "ERROR");
            error.put("message", "該投票項目名稱已存在");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }
    }

    //PUT /api/items/{id}
    //修改投票項目名稱，檢查名稱不能空白。
    @PutMapping("/items/{id}")
    public ResponseEntity<Map<String, Object>> updateItem(@PathVariable Integer id,
                                                          @RequestBody ItemRequest request) {
        String name = request.getName();
        if (name == null || name.trim().isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "ERROR");
            error.put("message", "投票項目名稱不可為空");
            return ResponseEntity.badRequest().body(error);
        }

        try {
            voteService.updateItem(id, name.trim());

            Map<String, Object> success = new HashMap<>();
            success.put("status", "SUCCESS");
            success.put("message", "更新成功");
            return ResponseEntity.ok(success);

        } catch (DataIntegrityViolationException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "ERROR");
            error.put("message", "該投票項目名稱已存在");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }
    }

  
    //DELETE /api/items/{id}

    @DeleteMapping("/items/{id}")
    public ResponseEntity<Map<String, Object>> deleteItem(@PathVariable Integer id) {
        voteService.deleteItem(id);

        Map<String, Object> success = new HashMap<>();
        success.put("status", "SUCCESS");
        success.put("message", "刪除成功");
        return ResponseEntity.ok(success);
    }

    //POST /api/votes
    //提交投票，檢查 voter 不可空白、itemIds 不能 null 也不能空陣列。

    @PostMapping("/votes")
    public ResponseEntity<Map<String, Object>> submitVote(@RequestBody VoteRequest request) {

        String voter = request.getVoter();
        if (voter == null || voter.trim().isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "ERROR");
            error.put("message", "請填寫投票人姓名");
            return ResponseEntity.badRequest().body(error);
        }

        List<Integer> itemIds = request.getItemIds();
        if (itemIds == null || itemIds.isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "ERROR");
            error.put("message", "請選擇至少一個投票項目");
            return ResponseEntity.badRequest().body(error);
        }

        try {
            voteService.submitVotes(voter.trim(), itemIds);

            Map<String, Object> success = new HashMap<>();
            success.put("status", "SUCCESS");
            success.put("message", "多選投票成功！");
            return ResponseEntity.ok(success);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "ERROR");
            error.put("message", "投票失敗：系統發生異常！");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }
    }
}
