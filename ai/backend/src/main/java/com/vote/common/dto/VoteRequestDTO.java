package com.vote.common.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

public class VoteRequestDTO {

  @NotBlank(message = "投票人不能為空")
  @Size(max = 100, message = "投票人名稱長度不能超過100")
  private String voter;

  @NotEmpty(message = "請至少選擇一個投票項目")
  private List<Integer> itemIds;

  public String getVoter() { return voter; }
  public void setVoter(String voter) { this.voter = voter; }
  public List<Integer> getItemIds() { return itemIds; }
  public void setItemIds(List<Integer> itemIds) { this.itemIds = itemIds; }
}
