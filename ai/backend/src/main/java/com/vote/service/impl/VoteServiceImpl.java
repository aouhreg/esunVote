package com.vote.service.impl;

import com.vote.common.dto.VoteItemDTO;
import com.vote.common.dto.VoteRequestDTO;
import com.vote.common.util.HtmlSanitizer;
import com.vote.entity.VoteItem;
import com.vote.repository.VoteRepository;
import com.vote.service.VoteService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VoteServiceImpl implements VoteService {

  private final VoteRepository voteRepository;

  public VoteServiceImpl(VoteRepository voteRepository) {
    this.voteRepository = voteRepository;
  }

  @Override
  public List<VoteItemDTO> getAllItems() {
    return voteRepository.findAll().stream()
        .map(this::toDTO)
        .collect(Collectors.toList());
  }

  @Override
  public VoteItemDTO getItemById(Integer id) {
    VoteItem item = voteRepository.findById(id);
    return item != null ? toDTO(item) : null;
  }

  @Override
  @Transactional
  public VoteItemDTO createItem(String name) {
    String safeName = HtmlSanitizer.sanitize(name);
    if (safeName == null || safeName.isBlank()) {
      throw new IllegalArgumentException("投票項目名稱不能為空");
    }
    VoteItem existing = voteRepository.findByName(safeName);
    if (existing != null) {
      throw new IllegalArgumentException("投票項目已存在");
    }
    int id = voteRepository.insert(safeName);
    VoteItem item = voteRepository.findById(id);
    return toDTO(item);
  }

  @Override
  @Transactional
  public VoteItemDTO updateItem(Integer id, String name) {
    String safeName = HtmlSanitizer.sanitize(name);
    if (safeName == null || safeName.isBlank()) {
      throw new IllegalArgumentException("投票項目名稱不能為空");
    }
    VoteItem existing = voteRepository.findByName(safeName);
    if (existing != null && !existing.getId().equals(id)) {
      throw new IllegalArgumentException("投票項目已存在");
    }
    int affected = voteRepository.update(id, safeName);
    if (affected == 0) {
      throw new IllegalArgumentException("投票項目不存在或無需更新");
    }
    VoteItem item = voteRepository.findById(id);
    return toDTO(item);
  }

  @Override
  @Transactional
  public void deleteItem(Integer id) {
    int affected = voteRepository.delete(id);
    if (affected == 0) {
      throw new IllegalArgumentException("投票項目不存在");
    }
  }

  @Override
  @Transactional
  public void castVotes(VoteRequestDTO request) {
    String safeVoter = HtmlSanitizer.sanitize(request.getVoter());
    if (safeVoter == null || safeVoter.isBlank()) {
      throw new IllegalArgumentException("投票人不能為空");
    }
    if (voteRepository.hasVoted(safeVoter)) {
      throw new IllegalArgumentException("您已經投過票了，每人僅限投票一次");
    }
    voteRepository.castVotes(safeVoter, request.getItemIds());
  }

  @Override
  public boolean hasVoted(String username) {
    return voteRepository.hasVoted(username);
  }

  private VoteItemDTO toDTO(VoteItem item) {
    return new VoteItemDTO(item.getId(), item.getName(), item.getVoteCount());
  }
}
