package com.vote.entity;

import java.time.LocalDateTime;

public class VoteItem {
  private Integer id;
  private String name;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private Integer voteCount;

  public Integer getId() { return id; }
  public void setId(Integer id) { this.id = id; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
  public LocalDateTime getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
  public Integer getVoteCount() { return voteCount; }
  public void setVoteCount(Integer voteCount) { this.voteCount = voteCount; }
}
