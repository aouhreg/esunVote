package com.example.backend.service;

import com.example.backend.model.VoteItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;


@Service
public class VoteService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    
    //取得所有投票項目 + 累計票數。
    //SP04：sp_get_items_with_votes
     
    public List<VoteItem> getItemWithVotes() {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_get_items_with_votes");

        Map<String, Object> out = jdbcCall.execute();
        List<Map<String, Object>> rows = (List<Map<String, Object>>) out.get("#result-set-1");

        List<VoteItem> items = new java.util.ArrayList<>();
        for (int i = 0; i < rows.size(); i++) {
            Map<String, Object> row = rows.get(i);
            VoteItem item = new VoteItem();
            item.setId(((Number) row.get("item_id")).intValue());
            item.setName((String) row.get("item_name"));
            item.setTotalVotes(((Number) row.get("total_votes")).longValue());
            items.add(item);
        }
        return items;
    }

    
    //新增投票項目。
    //SP02：sp_add_item
    
    public void addItem(String name) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_add_item");

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("p_name", name);
        jdbcCall.execute(params);
    }

    
    //修改投票項目名稱。
    //SP05：sp_update_item
    
    public void updateItem(Integer id, String name) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_update_item");

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("p_id", id);
        params.addValue("p_name", name);
        jdbcCall.execute(params);
    }

    
    //刪除投票項目。
    //SP03：sp_delete_item
     
    public void deleteItem(Integer id) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_delete_item");

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("p_id", id);
        jdbcCall.execute(params);
    }

    
    //提交投票（多選）。
    //SP05：sp_submit_vote
    //@Transactional 保證：萬一寫到一半斷線，已寫入的資料會自動撤銷。
    
    @Transactional
    public void submitVotes(String voter, List<Integer> itemIds) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_submit_vote");

        for (int i = 0; i < itemIds.size(); i++) {
            Integer id = itemIds.get(i);
            MapSqlParameterSource params = new MapSqlParameterSource();
            params.addValue("p_voter", voter);
            params.addValue("p_item_id", id);
            jdbcCall.execute(params);
        }
    }
}
