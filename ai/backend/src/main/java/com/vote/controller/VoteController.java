package com.vote.controller;

import com.vote.common.dto.ApiResponse;
import com.vote.common.dto.VoteItemDTO;
import com.vote.common.dto.VoteRequestDTO;
import com.vote.service.VoteService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class VoteController {

  private final VoteService voteService;

  public VoteController(VoteService voteService) {
    this.voteService = voteService;
  }

  @GetMapping("/items")
  public ResponseEntity<ApiResponse<List<VoteItemDTO>>> getAllItems() {
    List<VoteItemDTO> items = voteService.getAllItems();
    return ResponseEntity.ok(ApiResponse.ok(items));
  }

  @GetMapping("/items/{id}")
  public ResponseEntity<ApiResponse<VoteItemDTO>> getItemById(@PathVariable Integer id) {
    VoteItemDTO item = voteService.getItemById(id);
    if (item == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(ApiResponse.fail("投票項目不存在"));
    }
    return ResponseEntity.ok(ApiResponse.ok(item));
  }

  @PostMapping("/items")
  public ResponseEntity<ApiResponse<VoteItemDTO>> createItem(
      @Valid @RequestBody VoteItemDTO dto) {
    VoteItemDTO created = voteService.createItem(dto.getName());
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.ok("新增成功", created));
  }

  @PutMapping("/items/{id}")
  public ResponseEntity<ApiResponse<VoteItemDTO>> updateItem(
      @PathVariable Integer id,
      @Valid @RequestBody VoteItemDTO dto) {
    VoteItemDTO updated = voteService.updateItem(id, dto.getName());
    return ResponseEntity.ok(ApiResponse.ok("更新成功", updated));
  }

  @DeleteMapping("/items/{id}")
  public ResponseEntity<ApiResponse<Void>> deleteItem(@PathVariable Integer id) {
    voteService.deleteItem(id);
    return ResponseEntity.ok(ApiResponse.ok("刪除成功", null));
  }

  @PostMapping("/votes")
  public ResponseEntity<ApiResponse<Void>> castVotes(
      @Valid @RequestBody VoteRequestDTO dto) {
    voteService.castVotes(dto);
    return ResponseEntity.ok(ApiResponse.ok("投票成功", null));
  }

  @GetMapping("/votes/check")
  public ResponseEntity<ApiResponse<Boolean>> checkVoted(
      @RequestParam String username) {
    boolean voted = voteService.hasVoted(username);
    return ResponseEntity.ok(ApiResponse.ok(voted));
  }
}
