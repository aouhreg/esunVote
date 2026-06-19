package com.vote.service;

import com.vote.common.dto.VoteItemDTO;
import com.vote.common.dto.VoteRequestDTO;
import java.util.List;

public interface VoteService {
  List<VoteItemDTO> getAllItems();
  VoteItemDTO getItemById(Integer id);
  VoteItemDTO createItem(String name);
  VoteItemDTO updateItem(Integer id, String name);
  void deleteItem(Integer id);
  void castVotes(VoteRequestDTO request);
  boolean hasVoted(String username);
}
