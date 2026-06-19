package com.vote.entity;

import java.time.LocalDateTime;

public class VoteRecord {
  private Integer id;
  private String voter;
  private Integer itemId;
  private LocalDateTime createdAt;

  public Integer getId() { return id; }
  public void setId(Integer id) { this.id = id; }
  public String getVoter() { return voter; }
  public void setVoter(String voter) { this.voter = voter; }
  public Integer getItemId() { return itemId; }
  public void setItemId(Integer itemId) { this.itemId = itemId; }
  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
