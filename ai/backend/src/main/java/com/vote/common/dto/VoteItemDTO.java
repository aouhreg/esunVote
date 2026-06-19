package com.vote.common.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class VoteItemDTO {
  private Integer id;

  @NotBlank(message = "投票項目名稱不能為空")
  @Size(max = 100, message = "投票項目名稱長度不能超過100")
  private String name;

  private Integer voteCount;

  public VoteItemDTO() {}

  public VoteItemDTO(Integer id, String name, Integer voteCount) {
    this.id = id;
    this.name = name;
    this.voteCount = voteCount;
  }

  public Integer getId() { return id; }
  public void setId(Integer id) { this.id = id; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public Integer getVoteCount() { return voteCount; }
  public void setVoteCount(Integer voteCount) { this.voteCount = voteCount; }
}
