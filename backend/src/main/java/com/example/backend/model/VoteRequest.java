package com.example.backend.model;

import java.util.List;


public class VoteRequest {

    private String voter;
    private List<Integer> itemIds;

    public VoteRequest() {
    }

    public String getVoter() {
        return voter;
    }

    public void setVoter(String voter) {
        this.voter = voter;
    }

    public List<Integer> getItemIds() {
        return itemIds;
    }

    public void setItemIds(List<Integer> itemIds) {
        this.itemIds = itemIds;
    }
}
